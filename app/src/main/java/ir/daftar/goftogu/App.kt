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
import kotlinx.coroutines.withContext
import java.security.MessageDigest

/** سقف تعداد صفحات سینک دلتا در هر فراخوانی (هر صفحه حداکثر ۲۰۰ رویداد). */
private const val MAX_SYNC_PAGES = 20

/** اندازهٔ هر صفحه از پیام‌های تیم در حالت اسنپ‌شات (باید با limit سرور هم‌خوان باشد). */
private const val SNAPSHOT_TEAM_PAGE_SIZE = 100

class App : Application() {
    lateinit var db: Db
        private set

    // Scope سراسری در پس‌زمینه برای حذف قطعی ANR
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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

    /** خواندن آخرین شماره توالی رویدادهای سینک (PTS) برای کاربر از SharedPreferences */
    fun getLocalPts(userId: String): Long {
        if (userId.isBlank()) return 0L
        val prefs = getSharedPreferences("sync_pts_prefs", Context.MODE_PRIVATE)
        return prefs.getLong("pts_$userId", 0L)
    }

    /** ذخیره آخرین شماره توالی رویدادهای سینک (PTS) کاربر در SharedPreferences */
    fun saveLocalPts(userId: String, pts: Long) {
        if (userId.isBlank()) return
        val prefs = getSharedPreferences("sync_pts_prefs", Context.MODE_PRIVATE)
        prefs.edit().putLong("pts_$userId", pts).apply()
    }

    /** ثبت زمان پایان آخرین سینک موفق. */
    fun markSyncCompleted() {
        getSharedPreferences("sync_pts_prefs", Context.MODE_PRIVATE)
            .edit().putLong("last_sync_at", System.currentTimeMillis()).apply()
    }

    /** زمان آخرین سینک موفق (میلی‌ثانیه) یا صفر در صورت نبود سابقه. */
    fun lastSyncAt(): Long =
        getSharedPreferences("sync_pts_prefs", Context.MODE_PRIVATE).getLong("last_sync_at", 0L)

    /**
     * دریافت تجمیعی تغییرات از سرور (اسنپ‌شات کامل یا دلتای مبتنی بر PTS) و اعمال اتمیک آن روی دیتابیس لوکال.
     * - اگر داده لوکال کاربر خالی باشد، PTS صفر فرستاده می‌شود تا اسنپ‌شات کامل برگردد.
     * - در صورت hasMore، صفحات بعدی تا سقف [MAX_SYNC_PAGES] ادامه داده می‌شوند.
     * - در پایان زمان سینک ثبت و [onComplete] روی ترد اصلی فراخوانی می‌شود.
     */
    fun fetchUpdatesDifferenceAsync(userId: String, onComplete: ((Boolean) -> Unit)? = null) {
        if (userId.isBlank()) {
            onComplete?.invoke(false)
            return
        }

        appScope.launch {
            try {
                val api = NetworkModule.getApiService()
                val isDbEmpty = runCatching { db.isLocalDataEmpty(userId) }.getOrDefault(false)
                var currentPts = if (isDbEmpty) 0L else getLocalPts(userId)
                var inSnapshot = currentPts <= 0L
                var snapshotOffset = 0
                var applied = false

                var page = 0
                while (page < MAX_SYNC_PAGES) {
                    page++
                    val response = api.getSyncDifference(
                        SyncDifferenceRequest(
                            userId = userId,
                            pts = currentPts,
                            limit = 200,
                            offset = snapshotOffset
                        )
                    )
                    val diff = response.body()
                    if (!response.isSuccessful || diff == null) {
                        Log.w("SyncPTS", "Difference sync failed for $userId: HTTP ${response.code()}")
                        break
                    }

                    // اعمال اتمیک داده‌ها در دیتابیس لوکال (استراتژی Merge)
                    db.applyDifferenceLocally(
                        userId = userId,
                        dialogs = diff.dialogs,
                        entries = diff.entries,
                        teamMessages = diff.teamMessages,
                        supportMessages = diff.supportMessages
                    )

                    // اتصال خودکار تنظیم مدل هوش مصنوعی دریافتی از سرور به مدل فعال لوکال
                    diff.aiConfig?.let { ai ->
                        runCatching { db.ensureServerAiModel(ai.provider, ai.modelName, ai.endpoint) }
                    }

                    applied = true

                    // فاز اسنپ‌شات: پیام‌های تیم با offset صفحه‌بندی می‌شوند و PTS فقط پس از پایان ثبت می‌شود.
                    if (inSnapshot) {
                        if (diff.hasMore) {
                            snapshotOffset += SNAPSHOT_TEAM_PAGE_SIZE
                            continue
                        }
                        inSnapshot = false
                        currentPts = diff.newPts
                        saveLocalPts(userId, currentPts)
                        break
                    }

                    // فاز دلتا: PTS از سرور گرفته و در صورت وجود صفحهٔ بعد ادامه می‌دهیم.
                    currentPts = diff.newPts
                    saveLocalPts(userId, currentPts)
                    if (!diff.hasMore) break
                }

                if (applied) markSyncCompleted()

                withContext(Dispatchers.Main) { onComplete?.invoke(applied) }
            } catch (e: Exception) {
                Log.e("SyncPTS", "Difference sync failed for $userId", e)
                withContext(Dispatchers.Main) { onComplete?.invoke(false) }
            }
        }
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

