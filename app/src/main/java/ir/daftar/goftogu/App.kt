package ir.daftar.goftogu

import android.app.Application
import android.content.Context
import java.security.MessageDigest

class App : Application() {
    lateinit var db: Db
        private set

    override fun onCreate() {
        super.onCreate()
        db = Db(this)
        db.ensureAdmin()
        db.ensureOpenRouter()
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
