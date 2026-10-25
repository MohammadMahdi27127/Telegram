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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.security.MessageDigest

class App : Application() {
    lateinit var db: Db
        private set

    override fun onCreate() {
        super.onCreate()
        db = Db(this)
        db.ensureAdmin()
        db.ensureOpenRouter()
        createSupportChannel(this)
    }

    fun sessionId(): String? = getSharedPreferences("session", Context.MODE_PRIVATE).getString("userId", null)

    fun setSession(id: String?) {
        getSharedPreferences("session", Context.MODE_PRIVATE).edit().putString("userId", id).apply()
    }
}

fun hashPassword(password: String, salt: String): String {
    val digest = MessageDigest.getInstance("SHA-256").digest("$salt:$password".toByteArray())
    return digest.joinToString("") { "%02x".format(it) }
}

fun normalizePhone(input: String): String? {
    val persian = "۰۱۲۳۴۵۶۷۸۹"
    val arabic = "٠١٢٣٤٥٦٧٨٩"
    var text = input.trim()
    for (i in 0..9) {
        text = text.replace(persian[i].toString(), i.toString()).replace(arabic[i].toString(), i.toString())
    }
    var digits = text.filter { it.isDigit() }
    if (digits.startsWith("0098")) digits = digits.drop(4)
    if (digits.startsWith("98") && digits.length == 12) digits = digits.drop(2)
    if (Regex("^9\\d{9}$").matches(digits)) digits = "0$digits"
    return digits.takeIf { Regex("^09\\d{9}$").matches(it) }
}

const val ADMIN_PHONE = "09012345678"

fun isAdmin(phone: String?) = phone == "09012345678" || phone == "09052893115"
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
