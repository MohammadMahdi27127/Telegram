package ir.daftar.goftogu

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class App : Application() {
    lateinit var db: Db
        private set

    // Scope سراسری در پس‌زمینه برای حذف قطعی ANR
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // قفل سینک اولیه تا در صورت فراخوانی هم‌زمان (MainActivity و صفحهٔ چت‌ها) فقط یک‌بار اجرا شود
    private val initialSyncMutex = kotlinx.coroutines.sync.Mutex()

    override fun onCreate() {
        super.onCreate()

        // ۱. نمونه‌سازی اولیه دیتابیس
        db = Db(this)

        // ۲. انتقال کوئری‌های سنگین به ترد پس‌زمینه
        appScope.launch {
            try {
                Log.d("AppInit", "Starting database warm-up on IO thread...")
                db.ensureAdmin()
                db.ensureOpenRouter()
                Log.d("AppInit", "Database warm-up completed successfully on background thread.")
            } catch (e: Exception) {
                Log.e("AppInit", "Failed to complete database warm-up on background thread", e)
            }
        }

        // ۳. ساخت کانال نوتیفیکیشن
        createSupportChannel(this)
    }

    fun sessionId(): String? = getSharedPreferences("session", Context.MODE_PRIVATE).getString("userId", null)

    fun setSession(id: String?) {
        getSharedPreferences("session", Context.MODE_PRIVATE).edit().putString("userId", id).apply()
    }

    /**
     * سرور سے کاربر کی پروفائل معلومات (plan, balance، username وغیرہ) کو سنک کریں
     * یہ method Cache clear ہونے کے بعد plan کو دوبارہ restore کرتا ہے
     * 
     * @param userId کاربر کی شناسہ
     */
    fun syncUserProfile(userId: String) {
        if (userId.isBlank()) return
        
        appScope.launch {
            try {
                val apiService = NetworkModule.getApiService()
                val response = apiService.getUserProfile(userId)
                
                if (response.isSuccessful && response.body() != null) {
                    // لوکال ڈیٹابیس میں پروفائل اپڈیٹ کریں
                    db.updateUserProfileFromServer(response.body()!!)
                    Log.d("ProfileSync", "User profile synced successfully for: $userId")
                } else {
                    Log.w("ProfileSync", "Failed to fetch user profile: ${response.code()}")
                }
            } catch (e: Exception) {
                // آفلائن mode میں خاموشی سے ناکام ہوں - کریش نہ کریں
                Log.e("ProfileSync", "Error syncing user profile: ${e.message}")
            }
        }
    }

    /**
     * سینک اولیهٔ کامل — بازگردانی تمامی گفتگوها، پیام‌ها و تیم‌های کاربر از سرور (PostgreSQL)
     * به دیتابیس محلی SQLite. منبع اصلی داده‌ها سرور است؛ این متد پس از نصب مجدد یا پاک‌شدن
     * کش/دیتای برنامه، تاریخچهٔ کامل کاربر را برمی‌گرداند.
     *
     * عملیات درج به صورت Transaction گروهی انجام می‌شود تا در حجم بالای پیام، برنامه فریز نشود.
     */
    suspend fun performFullInitialSync(userId: String) {
        if (userId.isBlank()) return
        try {
            val apiService = NetworkModule.getApiService()

            // ۱. دریافت و ذخیره تمامی گفتگوها/مخاطبین
            val peopleResponse = apiService.getAllUserPeople(userId)
            if (peopleResponse.isSuccessful) {
                val people = peopleResponse.body().orEmpty()
                withContext(Dispatchers.IO) { db.upsertPeopleLocally(userId, people) }
                Log.d("InitialSync", "People restored: ${people.size}")
            } else {
                Log.w("InitialSync", "Failed to fetch people: ${peopleResponse.code()}")
            }

            // ۲. دریافت و ذخیره تمامی پیام‌ها و یادداشت‌ها
            val entriesResponse = apiService.getAllUserEntries(userId)
            if (entriesResponse.isSuccessful) {
                val entries = entriesResponse.body().orEmpty()
                withContext(Dispatchers.IO) { db.upsertEntriesLocally(userId, entries) }
                Log.d("InitialSync", "Entries restored: ${entries.size}")
            } else {
                Log.w("InitialSync", "Failed to fetch entries: ${entriesResponse.code()}")
            }

            // ۳. دریافت و ذخیره تیم‌ها (هم‌راستا با منطق TeamsScreen)
            val teamsResponse = apiService.getUserTeams(userId)
            if (teamsResponse.isSuccessful) {
                val teams = teamsResponse.body().orEmpty()
                withContext(Dispatchers.IO) {
                    val remoteIds = teams.map { it.id }.toSet()
                    teams.forEach { team ->
                        db.upsertGroupTeam(
                            userId = userId,
                            teamId = team.id,
                            name = team.name,
                            description = team.description,
                            members = team.members,
                            creatorId = team.creatorId,
                        )
                    }
                    // حذف تیم‌های محلی که دیگر روی سرور برای این کاربر وجود ندارند
                    db.localTeamIds(userId)
                        .filter { it !in remoteIds }
                        .forEach { db.deleteTeamLocally(it) }
                }
                Log.d("InitialSync", "Teams restored: ${teams.size}")
            }

            Log.d("InitialSync", "Full initial sync completed for: $userId")
        } catch (e: Exception) {
            Log.e("InitialSync", "Error in full initial sync", e)
        }
    }

    /**
     * اجرای سینک اولیه فقط در صورت خالی‌بودن دیتابیس محلی.
     * با Mutex سریالی می‌شود تا فراخوانی هم‌زمان از MainActivity و صفحهٔ چت‌ها
     * به اجرای دوبارهٔ سنگین منجر نشود.
     */
    suspend fun ensureInitialSync(userId: String) {
        if (userId.isBlank()) return
        initialSyncMutex.withLock {
            if (!db.isLocalDataEmpty(userId)) return
            Log.d("InitialSync", "Local DB empty → running full initial sync for: $userId")
            performFullInitialSync(userId)
        }
    }

    /** نسخهٔ غیرمسدودکننده برای فراخوانی از onCreate (بستر پس‌زمینه). */
    fun runInitialSyncIfNeeded(userId: String) {
        if (userId.isBlank()) return
        appScope.launch { ensureInitialSync(userId) }
    }
}

fun hashPassword(password: String, salt: String): String {
    val digest = MessageDigest.getInstance("SHA-256").digest("$salt:$password".toByteArray())
    return digest.joinToString("") { "%02x".format(it) }
}

fun String.toEnglishDigits(): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    var result = this
    for (i in 0..9) {
        result = result.replace(persianDigits[i], '0' + i)
            .replace(arabicDigits[i], '0' + i)
    }
    return result.trim()
}

fun normalizePhone(input: String): String? {
    val text = input.toEnglishDigits().trim()
    var digits = text.filter { it.isDigit() }
    if (digits.startsWith("0098")) digits = digits.drop(4)
    if (digits.startsWith("98") && digits.length == 12) digits = digits.drop(2)
    if (Regex("^9\\d{9}$").matches(digits)) digits = "0$digits"
    return digits.takeIf { Regex("^09\\d{9}$").matches(it) }
}

const val ADMIN_PHONE = "09052893115"

fun isAdmin(phone: String?) = phone == "09052893115"
const val OTP = "123456"

/** شناسهٔ کانال نوتیفیکیشن پاسخ‌های پشتیبانی. */
const val SUPPORT_CHANNEL_ID = "support_replies"

/** شناسهٔ نوتیفیکیشن پاسخ پشتیبانی. */
private const val SUPPORT_NOTIFICATION_ID = 12727

/** ساخت کانال نوتیفیکیشن پشتیبانی (اندروید ۸ به بالا). */
fun createSupportChannel(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val channel = NotificationChannel(
        SUPPORT_CHANNEL_ID,
        "پاسخ پشتیبانی",
        NotificationManager.IMPORTANCE_HIGH,
    ).apply { description = "اطلاع از پاسخ تیم پشتیبانی در گفتگوی درون‌برنامه‌ای" }
    context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
}

/** آیا اجازهٔ نمایش نوتیفیکیشن داده شده است؟ */
fun notificationsAllowed(context: Context): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else {
        NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

/** نمایش نوتیفیکیشن پاسخ پشتیبانی؛ با کلیک، اپ باز می‌شود. */
@SuppressLint("MissingPermission")
fun showSupportNotification(context: Context, title: String, body: String) {
    if (!notificationsAllowed(context)) return
    createSupportChannel(context)
    val open = PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
    val notification = NotificationCompat.Builder(context, SUPPORT_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_stat_support)
        .setContentTitle(title)
        .setContentText(body)
        .setStyle(NotificationCompat.BigTextStyle().bigText(body))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setContentIntent(open)
        .build()
    runCatching { NotificationManagerCompat.from(context).notify(SUPPORT_NOTIFICATION_ID, notification) }
}

