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

    // قفل رفرش داده تا از اجرای موازی/تکراری رفرش‌های هم‌زمان (onResume + swipe-refresh + اجرای اپ) جلوگیری شود
    private val refreshMutex = kotlinx.coroutines.sync.Mutex()

    // زمان آخرین همگام‌سازی موفق (epoch-millis) برای جلوگیری از بمباران درخواست‌ها در چرخهٔ ON_RESUME
    @Volatile
    var lastFullSyncTimestamp: Long = 0L
        private set

    /** علامت‌گذاری پایان موفق یک همگام‌سازی کامل (برای محاسبهٔ کول‌داون رفرش خودکار). */
    fun markSyncCompleted() {
        lastFullSyncTimestamp = System.currentTimeMillis()
    }

    /**
     * بررسی نیاز به همگام‌سازی خودکار: اگر کمتر از بازهٔ مشخص‌شده از آخرین سینک موفق گذشته باشد
     * (کول‌داون پیش‌فرض ۶۰ ثانیه)، مقدار false برمی‌گردد تا درخواست لغو شود.
     */
    fun shouldTriggerAutoSync(cooldownMillis: Long = 60_000L): Boolean {
        val now = System.currentTimeMillis()
        return (now - lastFullSyncTimestamp) >= cooldownMillis
    }

    /**
     * بکفیل یکبارهٔ سرور برای هر کاربر: مخاطبین/پیام‌های موجود محلی که پیش‌تر بدون user_id
     * روی سرور ثبت شده‌اند، با مالک صحیح دوباره push می‌شوند تا واکشی ابری آن‌ها را برگرداند.
     * فقط یک‌بار به‌ازای هر کاربر (کلید در SharedPreferences) اجرا می‌شود.
     */
    fun backfillOutboxIfNeeded(userId: String) {
        if (userId.isBlank()) return
        val prefs = getSharedPreferences("session", Context.MODE_PRIVATE)
        val key = "outbox_backfill_done_$userId"
        if (prefs.getBoolean(key, false)) return
        // اگر دادهٔ محلی خالی است، ابتدا باید از سرور بازیابی شود؛ بکفیل لازم نیست
        if (db.isLocalDataEmpty(userId)) return
        appScope.launch {
            try {
                val queued = db.backfillOutboxForUser(userId)
                withContext(Dispatchers.Main) { prefs.edit().putBoolean(key, true).apply() }
                Log.d("CloudRefresh", "Outbox backfill queued ($queued records) for: $userId")
            } catch (e: Exception) {
                Log.e("CloudRefresh", "Outbox backfill failed", e)
            }
        }
    }

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
     * بازخوانی کامل داده‌ها از سرور (Server-Driven / Cloud-Driven).
     *
     * تمام داده‌ها از PostgreSQL سرور واکشی شده و دیتابیس محلی SQLite فقط نقش
     * کش موقت/آفلاین را بازی می‌کند. ترتیب مراحل:
     *   ۱. پروفایل و وضعیت اشتراک (plan/plan_purchased_at/plan_expires_at)
     *   ۲. تنظیمات هوش مصنوعی کاربر (provider/model/api_key)
     *   ۳. گفتگوها و عناوین (people)
     *   ۴. پیام‌ها و یادداشت‌ها (entries)
     *   ۵. پیام‌های پشتیبانی (support_messages)
     *   ۶. تیم‌ها و پیام‌های تیمی
     *
     * @return اطلاعات پروفایل/تنظیمات از سرور برای درج در State رابط کاربری، یا null در خطا.
     */
    suspend fun refreshAppDataFromServer(userId: String): CloudRefreshResult? {
        if (userId.isBlank()) return null
        // سریال‌سازی: جلوگیری از اجرای موازی رفرش‌های هم‌زمان (onResume + swipe + اجرای اپ)
        return refreshMutex.withLock { doRefreshAppDataFromServer(userId) }
    }

    private suspend fun doRefreshAppDataFromServer(userId: String): CloudRefreshResult? {
        var result: CloudRefreshResult? = null
        try {
            val apiService = NetworkModule.getApiService()

            // ۱ و ۲. پروفایل + اشتراک + تنظیمات هوش مصنوعی (Single Source of Truth = سرور)
            val profileResponse = apiService.getUserProfile(userId)
            if (profileResponse.isSuccessful && profileResponse.body() != null) {
                val p = profileResponse.body()!!
                withContext(Dispatchers.IO) { db.updateUserProfileFromServer(p) }
                result = CloudRefreshResult(
                    plan = p.plan,
                    planPurchasedAt = p.planPurchasedAt,
                    planExpiresAt = p.planExpiresAt,
                    aiProvider = p.aiProvider,
                    aiModelName = p.aiModelName,
                    aiApiKey = p.aiApiKey,
                )
                Log.d("CloudRefresh", "Profile + AI config refreshed from server")
            } else {
                Log.w("CloudRefresh", "Failed to fetch profile: ${profileResponse.code()}")
            }

            // ۳. گفتگوها/مخاطبین
            val peopleResponse = apiService.getAllUserPeople(userId)
            if (peopleResponse.isSuccessful) {
                val people = peopleResponse.body().orEmpty()
                withContext(Dispatchers.IO) { db.upsertPeopleLocally(userId, people) }
                Log.d("CloudRefresh", "People refreshed: ${people.size}")
            }

            // ۴. پیام‌ها و یادداشت‌ها
            val entriesResponse = apiService.getAllUserEntries(userId)
            if (entriesResponse.isSuccessful) {
                val entries = entriesResponse.body().orEmpty()
                withContext(Dispatchers.IO) { db.upsertEntriesLocally(userId, entries) }
                Log.d("CloudRefresh", "Entries refreshed: ${entries.size}")
            }

            // ۵. پیام‌های پشتیبانی
            val supportResponse = apiService.getUserSupportMessages(userId)
            if (supportResponse.isSuccessful) {
                val messages = supportResponse.body().orEmpty()
                withContext(Dispatchers.IO) { db.upsertSupportMessagesLocally(userId, messages) }
                Log.d("CloudRefresh", "Support messages refreshed: ${messages.size}")
            }

            // ۶. تیم‌ها و اعضای آنها
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
                    db.localTeamIds(userId)
                        .filter { it !in remoteIds }
                        .forEach { db.deleteTeamLocally(it) }
                }
                Log.d("CloudRefresh", "Teams refreshed: ${teams.size}")
            }

            Log.d("CloudRefresh", "App data refreshed from server for: $userId")
            // ثبت زمان پایان موفق سینک برای محاسبهٔ کول‌داون رفرش خودکار
            markSyncCompleted()
        } catch (e: Exception) {
            Log.e("CloudRefresh", "Error refreshing app data from server", e)
        }
        return result
    }

    /** نسخهٔ غیرمسدودکننده برای فراخوانی از onResume/onCreate؛ نتیجه به ترد اصلی برمی‌گردد. */
    fun refreshAppDataFromServerAsync(userId: String, onDone: ((CloudRefreshResult?) -> Unit)? = null) {
        if (userId.isBlank()) return
        appScope.launch {
            val result = refreshAppDataFromServer(userId)
            if (onDone != null) withContext(Dispatchers.Main) { onDone(result) }
        }
    }

    /**
     * تثبیت تنظیمات هوش مصنوعی روی سرور (PUT /api/v1/users/{user_id}/ai-config)
     * و سپس اعمال در حافظهٔ محلی. ابتدا سرور (منبع حقیقت) و سپس کش محلی به‌روز می‌شود.
     */
    fun saveAiConfigToServer(
        userId: String,
        provider: String?,
        modelName: String?,
        apiKey: String?,
        onDone: ((Boolean) -> Unit)? = null,
    ) {
        if (userId.isBlank()) return
        appScope.launch {
            var ok = false
            try {
                val response = NetworkModule.getApiService().updateAiConfig(
                    userId,
                    UpdateAiConfigReq(aiProvider = provider, aiModelName = modelName, aiApiKey = apiKey),
                )
                if (response.isSuccessful) {
                    ok = true
                    val dto = response.body()
                    withContext(Dispatchers.IO) {
                        db.setUserAiConfig(
                            userId = userId,
                            provider = dto?.aiProvider ?: provider,
                            modelName = dto?.aiModelName ?: modelName,
                            apiKey = dto?.aiApiKey ?: apiKey,
                        )
                    }
                    Log.d("AiConfig", "AI config persisted on server for: $userId")
                } else {
                    Log.w("AiConfig", "Server rejected ai-config: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("AiConfig", "Failed to persist ai-config to server", e)
            }
            if (onDone != null) withContext(Dispatchers.Main) { onDone(ok) }
        }
    }
}

/**
 * نتیجهٔ بازخوانی پروفایل/تنظیمات از سرور (Cloud-Driven).
 * برای محاسبهٔ روزهای باقیماندهٔ اشتراک و اعمال تنظیمات AI در State رابط کاربری.
 */
data class CloudRefreshResult(
    val plan: String,
    val planPurchasedAt: Long?,
    val planExpiresAt: Long?,
    val aiProvider: String?,
    val aiModelName: String?,
    val aiApiKey: String?,
) {
    /** تعداد روزهای باقیماندهٔ اشتراک بر اساس plan_expires_at سرور؛ null اگر انقضا نامشخص باشد. */
    val remainingDays: Int?
        get() {
            val expires = planExpiresAt ?: return null
            val now = System.currentTimeMillis()
            if (expires <= now) return 0
            return ((expires - now) / (24L * 60 * 60 * 1000)).toInt()
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

