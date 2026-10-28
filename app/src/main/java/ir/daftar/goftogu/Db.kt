package ir.daftar.goftogu

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

data class User(
    val id: String,
    val phone: String,
    val name: String,
    val plan: String,
    val balance: Int,
    val planExpiresAt: Long?,
    val suspended: Boolean,
    val bannerSeen: Boolean,
    val chatBg: String? = null,
    val chatColor: String? = null,
    val avatar: String? = null,
    val username: String? = null,
    val monthlyDownloadCount: Int = 0,
    val monthlyAiUsageCount: Int = 0,
    val lastQuotaResetMonth: String? = null,
)

data class Person(
    val id: String,
    val name: String,
    val phone: String,
    val role: String,
    val pinned: Boolean,
    val lastBody: String,
    val lastAt: Long,
    val lastKind: String?,
    val lastChannel: String?,
    val hasNote: Boolean,
    val avatar: String? = null,
    val unread: Int = 0,
    val isGroup: Boolean = false,
    val saved: Boolean = false,
    val archived: Boolean = false,
)

data class Team(
    val id: String,
    val name: String,
    val description: String,
    val createdAt: Long,
    val memberCount: Int,
    val lastBody: String,
    val lastAt: Long,
)

data class TeamMember(val id: String, val name: String, val contactId: String?, val isMe: Boolean)

data class Entry(
    val id: String,
    val userId: String,
    val body: String,
    val kind: String,
    val channel: String,
    val occurredAt: Long,
    val pinned: Boolean = false,
    val voicePath: String? = null,
    val mediaPath: String? = null,
    val memberName: String? = null,
    val targetRecipientId: String? = null,
)

data class Slip(
    val id: String,
    val userId: String,
    val userName: String,
    val kind: String,
    val plan: String?,
    val amount: Int,
    val status: String,
    val receipt: String?,
    val refCode: String?,
    val createdAt: Long = 0,
)

data class ExportLog(
    val id: String,
    val userId: String,
    val timestamp: Long,
    val fileType: String,
    val targetName: String,
    val recordsCount: Int
)

data class Notice(val id: String, val title: String, val body: String, val createdAt: Long, val read: Boolean)
data class SupportMessage(
    val id: String,
    val userId: String,
    val fromAdmin: Boolean,
    val body: String,
    val kind: String,
    val path: String?,
    val createdAt: Long,
)
data class SupportThread(
    val userId: String,
    val name: String,
    val phone: String,
    val username: String?,
    val avatar: String?,
    val lastBody: String,
    val lastKind: String,
    val lastAt: Long,
    val unread: Int,
)
data class AiMessage(val id: String, val personId: String, val role: String, val body: String, val image: String?, val createdAt: Long)
data class Member(val id: String, val name: String)
data class Banner(val id: String, val title: String, val body: String, val image: String? = null)

class Db(context: Context) : SQLiteOpenHelper(context, "daftar.db", null, 17) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE users (
                id TEXT PRIMARY KEY, phone TEXT UNIQUE, salt TEXT, password_hash TEXT,
                name TEXT, plan TEXT, balance INTEGER, plan_expires_at INTEGER, suspended INTEGER,
                banner_seen INTEGER NOT NULL DEFAULT 0, chat_bg TEXT, chat_color TEXT, avatar TEXT,
                username TEXT, created_at INTEGER,
                monthly_download_count INTEGER NOT NULL DEFAULT 0,
                monthly_ai_usage_count INTEGER NOT NULL DEFAULT 0,
                last_quota_reset_month TEXT
            )""",
        )
        db.execSQL(
            """CREATE TABLE people (
                id TEXT PRIMARY KEY, user_id TEXT, name TEXT, phone TEXT, role TEXT, pinned INTEGER, created_at INTEGER
            )""",
        )
        db.execSQL(
            """CREATE TABLE entries (
                id TEXT PRIMARY KEY, user_id TEXT, person_id TEXT, body TEXT, kind TEXT, channel TEXT,
                occurred_at INTEGER, created_at INTEGER, updated_at INTEGER, target_recipient_id TEXT
            )""",
        )
        db.execSQL("CREATE TABLE notices (id TEXT PRIMARY KEY, user_id TEXT, title TEXT, body TEXT, read INTEGER, created_at INTEGER)")
        db.execSQL("CREATE TABLE banners (id TEXT PRIMARY KEY, title TEXT NOT NULL, body TEXT NOT NULL, image TEXT, active INTEGER NOT NULL DEFAULT 1, created_at INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE ai_models (id TEXT PRIMARY KEY, name TEXT, endpoint TEXT, model_id TEXT, api_key TEXT, input_cost REAL, output_cost REAL, active INTEGER NOT NULL DEFAULT 0, created_at INTEGER)")
        db.execSQL("CREATE TABLE announcements (id TEXT PRIMARY KEY, title TEXT, body TEXT, created_at INTEGER)")
        db.execSQL("CREATE TABLE slips (id TEXT PRIMARY KEY, user_id TEXT, kind TEXT, plan TEXT, amount INTEGER, status TEXT, receipt TEXT, ref_code TEXT, created_at INTEGER)")
        db.execSQL("ALTER TABLE entries ADD COLUMN pinned INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE entries ADD COLUMN voice_path TEXT")
        db.execSQL("ALTER TABLE entries ADD COLUMN media_path TEXT")
        db.execSQL("ALTER TABLE people ADD COLUMN avatar TEXT")
        db.execSQL("ALTER TABLE people ADD COLUMN is_group INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE people ADD COLUMN saved INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE people ADD COLUMN archived INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE entries ADD COLUMN member_id TEXT")
        db.execSQL("CREATE TABLE members (id TEXT PRIMARY KEY, person_id TEXT, name TEXT, created_at INTEGER)")
        db.execSQL("CREATE TABLE wallpapers (id TEXT PRIMARY KEY, title TEXT, category TEXT, path TEXT, created_at INTEGER)")
        db.execSQL("CREATE TABLE outbox (id TEXT PRIMARY KEY, kind TEXT, payload TEXT, created_at INTEGER)")
        db.execSQL("CREATE TABLE ai_messages (id TEXT PRIMARY KEY, user_id TEXT, person_id TEXT, role TEXT, body TEXT, image_path TEXT, created_at INTEGER)")
        db.execSQL("CREATE TABLE support_messages (id TEXT PRIMARY KEY, user_id TEXT, from_admin INTEGER NOT NULL DEFAULT 0, body TEXT, kind TEXT, path TEXT, read_admin INTEGER NOT NULL DEFAULT 0, read_user INTEGER NOT NULL DEFAULT 0, created_at INTEGER)")
        db.execSQL("CREATE TABLE IF NOT EXISTS export_logs (id TEXT PRIMARY KEY, user_id TEXT, timestamp INTEGER, file_type TEXT, target_name TEXT, records_count INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("CREATE TABLE IF NOT EXISTS export_logs (id TEXT PRIMARY KEY, user_id TEXT, timestamp INTEGER, file_type TEXT, target_name TEXT, records_count INTEGER)")
        db.execSQL("CREATE TABLE IF NOT EXISTS banners (id TEXT PRIMARY KEY, title TEXT NOT NULL, body TEXT NOT NULL, active INTEGER NOT NULL DEFAULT 1, created_at INTEGER NOT NULL)")
        val columns = db.rawQuery("PRAGMA table_info(users)", null).use {
            val names = mutableListOf<String>()
            while (it.moveToNext()) names += it.getString(1)
            names
        }
        if ("banner_seen" !in columns) db.execSQL("ALTER TABLE users ADD COLUMN banner_seen INTEGER NOT NULL DEFAULT 0")
        val bannerColumns = db.rawQuery("PRAGMA table_info(banners)", null).use {
            val names = mutableListOf<String>()
            while (it.moveToNext()) names += it.getString(1)
            names
        }
        if (bannerColumns.isNotEmpty() && "image" !in bannerColumns) db.execSQL("ALTER TABLE banners ADD COLUMN image TEXT")
        if ("chat_bg" !in columns) db.execSQL("ALTER TABLE users ADD COLUMN chat_bg TEXT")
        if ("chat_color" !in columns) db.execSQL("ALTER TABLE users ADD COLUMN chat_color TEXT")
        if ("avatar" !in columns) db.execSQL("ALTER TABLE users ADD COLUMN avatar TEXT")
        if ("username" !in columns) db.execSQL("ALTER TABLE users ADD COLUMN username TEXT")
        if ("monthly_download_count" !in columns) db.execSQL("ALTER TABLE users ADD COLUMN monthly_download_count INTEGER NOT NULL DEFAULT 0")
        if ("monthly_ai_usage_count" !in columns) db.execSQL("ALTER TABLE users ADD COLUMN monthly_ai_usage_count INTEGER NOT NULL DEFAULT 0")
        if ("last_quota_reset_month" !in columns) db.execSQL("ALTER TABLE users ADD COLUMN last_quota_reset_month TEXT")
        db.execSQL("CREATE TABLE IF NOT EXISTS ai_models (id TEXT PRIMARY KEY, name TEXT, endpoint TEXT, model_id TEXT, api_key TEXT, input_cost REAL, output_cost REAL, active INTEGER NOT NULL DEFAULT 0, created_at INTEGER)")
        val modelColumns = db.rawQuery("PRAGMA table_info(ai_models)", null).use {
            val names = mutableListOf<String>()
            while (it.moveToNext()) names += it.getString(1)
            names
        }
        if ("active" !in modelColumns) db.execSQL("ALTER TABLE ai_models ADD COLUMN active INTEGER NOT NULL DEFAULT 0")
        val activeCount = db.rawQuery("SELECT COUNT(*) FROM ai_models WHERE active=1", null).use { if (it.moveToFirst()) it.getInt(0) else 0 }
        if (activeCount == 0) db.execSQL("UPDATE ai_models SET active=1 WHERE id=(SELECT id FROM ai_models ORDER BY created_at DESC LIMIT 1)")
        db.execSQL("CREATE TABLE IF NOT EXISTS announcements (id TEXT PRIMARY KEY, title TEXT, body TEXT, created_at INTEGER)")
        db.execSQL("CREATE TABLE IF NOT EXISTS slips (id TEXT PRIMARY KEY, user_id TEXT, kind TEXT, plan TEXT, amount INTEGER, status TEXT, receipt TEXT, ref_code TEXT, created_at INTEGER)")
        val entryColumns = db.rawQuery("PRAGMA table_info(entries)", null).use {
            val names = mutableListOf<String>()
            while (it.moveToNext()) names += it.getString(1)
            names
        }
        if ("pinned" !in entryColumns) db.execSQL("ALTER TABLE entries ADD COLUMN pinned INTEGER NOT NULL DEFAULT 0")
        if ("voice_path" !in entryColumns) db.execSQL("ALTER TABLE entries ADD COLUMN voice_path TEXT")
        if ("media_path" !in entryColumns) db.execSQL("ALTER TABLE entries ADD COLUMN media_path TEXT")
        val peopleColumns = db.rawQuery("PRAGMA table_info(people)", null).use {
            val names = mutableListOf<String>()
            while (it.moveToNext()) names += it.getString(1)
            names
        }
        if ("avatar" !in peopleColumns) db.execSQL("ALTER TABLE people ADD COLUMN avatar TEXT")
        if ("is_group" !in peopleColumns) db.execSQL("ALTER TABLE people ADD COLUMN is_group INTEGER NOT NULL DEFAULT 0")
        if ("saved" !in peopleColumns) db.execSQL("ALTER TABLE people ADD COLUMN saved INTEGER NOT NULL DEFAULT 0")
        if ("member_id" !in entryColumns) db.execSQL("ALTER TABLE entries ADD COLUMN member_id TEXT")
        if ("target_recipient_id" !in entryColumns) db.execSQL("ALTER TABLE entries ADD COLUMN target_recipient_id TEXT")
        db.execSQL("CREATE TABLE IF NOT EXISTS members (id TEXT PRIMARY KEY, person_id TEXT, name TEXT, created_at INTEGER)")
        db.execSQL("CREATE TABLE IF NOT EXISTS wallpapers (id TEXT PRIMARY KEY, title TEXT, category TEXT, path TEXT, created_at INTEGER)")
        db.execSQL("CREATE TABLE IF NOT EXISTS outbox (id TEXT PRIMARY KEY, kind TEXT, payload TEXT, created_at INTEGER)")
        db.execSQL("CREATE TABLE IF NOT EXISTS ai_messages (id TEXT PRIMARY KEY, user_id TEXT, person_id TEXT, role TEXT, body TEXT, image_path TEXT, created_at INTEGER)")
        db.execSQL("CREATE TABLE IF NOT EXISTS support_messages (id TEXT PRIMARY KEY, user_id TEXT, from_admin INTEGER NOT NULL DEFAULT 0, body TEXT, kind TEXT, path TEXT, read_admin INTEGER NOT NULL DEFAULT 0, read_user INTEGER NOT NULL DEFAULT 0, created_at INTEGER)")
    }

    fun enqueue(kind: String, payload: String) {
        writableDatabase.insert("outbox", null, ContentValues().apply {
            put("id", UUID.randomUUID().toString())
            put("kind", kind)
            put("payload", payload)
            put("created_at", System.currentTimeMillis())
        })
    }

    fun outboxCount(): Int = readableDatabase.rawQuery("SELECT COUNT(*) FROM outbox", null).use { if (it.moveToFirst()) it.getInt(0) else 0 }

    data class Wallpaper(val id: String, val title: String, val category: String, val path: String)

    fun addWallpaper(title: String, category: String, path: String) {
        if (title.isBlank() || category.isBlank()) error("عنوان و دسته لازم است")
        writableDatabase.insert("wallpapers", null, ContentValues().apply {
            put("id", UUID.randomUUID().toString())
            put("title", title.trim())
            put("category", category.trim())
            put("path", path)
            put("created_at", System.currentTimeMillis())
        })
    }

    fun wallpapers(): List<Wallpaper> = readableDatabase.rawQuery("SELECT id, title, category, path FROM wallpapers ORDER BY category, created_at DESC", null).use {
        val list = mutableListOf<Wallpaper>()
        while (it.moveToNext()) list += Wallpaper(it.getString(0), it.getString(1), it.getString(2), it.getString(3))
        list
    }

    fun sendSupport(userId: String, fromAdmin: Boolean, body: String, kind: String = "text", path: String? = null) {
        writableDatabase.insert("support_messages", null, ContentValues().apply {
            put("id", UUID.randomUUID().toString())
            put("user_id", userId)
            put("from_admin", if (fromAdmin) 1 else 0)
            put("body", body)
            put("kind", kind)
            if (path == null) putNull("path") else put("path", path)
            put("read_admin", if (fromAdmin) 1 else 0)
            put("read_user", if (fromAdmin) 0 else 1)
            put("created_at", System.currentTimeMillis())
        })
    }

    fun supportMessages(userId: String): List<SupportMessage> {
        val cursor = readableDatabase.rawQuery(
            "SELECT id, user_id, from_admin, body, kind, path, created_at FROM support_messages WHERE user_id=? ORDER BY created_at",
            arrayOf(userId),
        )
        val list = mutableListOf<SupportMessage>()
        cursor.use {
            while (it.moveToNext()) {
                list += SupportMessage(
                    it.getString(0),
                    it.getString(1),
                    it.getInt(2) == 1,
                    it.getString(3).orEmpty(),
                    it.getString(4).orEmpty(),
                    if (it.isNull(5)) null else it.getString(5),
                    it.getLong(6),
                )
            }
        }
        return list
    }

    fun supportThreads(): List<SupportThread> {
        val cursor = readableDatabase.rawQuery(
            """SELECT m.user_id, m.body, m.kind, m.created_at,
                      (SELECT COUNT(*) FROM support_messages s WHERE s.user_id=m.user_id AND s.from_admin=0 AND s.read_admin=0),
                      u.name, u.phone, u.username, u.avatar
               FROM support_messages m
               JOIN users u ON u.id = m.user_id
               WHERE m.created_at = (SELECT MAX(s.created_at) FROM support_messages s WHERE s.user_id=m.user_id)
               ORDER BY m.created_at DESC""",
            null,
        )
        val list = mutableListOf<SupportThread>()
        cursor.use {
            while (it.moveToNext()) {
                list += SupportThread(
                    it.getString(0),
                    it.getString(5).orEmpty(),
                    it.getString(6).orEmpty(),
                    if (it.isNull(7)) null else it.getString(7),
                    if (it.isNull(8)) null else it.getString(8),
                    it.getString(1).orEmpty(),
                    it.getString(2).orEmpty(),
                    it.getLong(3),
                    it.getInt(4),
                )
            }
        }
        return list
    }

    fun supportUnreadForAdmin(): Int = readableDatabase.rawQuery(
        "SELECT COUNT(*) FROM support_messages WHERE from_admin=0 AND read_admin=0",
        null,
    ).use { if (it.moveToFirst()) it.getInt(0) else 0 }

    fun supportUnreadForUser(userId: String): Int = readableDatabase.rawQuery(
        "SELECT COUNT(*) FROM support_messages WHERE user_id=? AND from_admin=1 AND read_user=0",
        arrayOf(userId),
    ).use { if (it.moveToFirst()) it.getInt(0) else 0 }

    fun markSupportReadByAdmin(userId: String) {
        writableDatabase.update("support_messages", ContentValues().apply { put("read_admin", 1) }, "user_id=? AND from_admin=0", arrayOf(userId))
    }

    fun markSupportReadByUser(userId: String) {
        writableDatabase.update("support_messages", ContentValues().apply { put("read_user", 1) }, "user_id=? AND from_admin=1", arrayOf(userId))
    }

    fun ensureAdmin() {
        val phone = "09012345678"
        val password = "11111111"
        if (findUser(phone) == null) signup(phone, "ادمین", password) else reset(phone, password)
    }

    fun signupGuest(): String {
        var phone: String
        do {
            phone = "09" + (100_000_000..999_999_999).random()
        } while (findUser(phone) != null)
        val id = signup(phone, "مهمان", "guest-" + UUID.randomUUID().toString().replace("-", "").take(12))
        writableDatabase.update("users", ContentValues().apply { put("plan", "guest") }, "id=?", arrayOf(id))
        return id
    }

    fun signup(phone: String, name: String, password: String): String {
        if (findUser(phone) != null) error("این شماره قبلاً ثبت شده")
        if (password.length < 8) error("رمز باید حداقل ۸ کاراکتر باشد")
        if (name.trim().length < 2) error("نام را کامل‌تر بنویس")
        val id = UUID.randomUUID().toString()
        val salt = UUID.randomUUID().toString()
        val values = ContentValues().apply {
            put("id", id)
            put("phone", phone)
            put("salt", salt)
            put("password_hash", hashPassword(password, salt))
            put("name", name.trim())
            put("plan", "free")
            put("balance", 0)
            put("suspended", 0)
            put("created_at", System.currentTimeMillis())
        }
        writableDatabase.insert("users", null, values)
        addNotice(id, "خوش آمدی", "مکالمه روشن است و یادداشت زرد. هر روز سقف ثبت پلن رایگان ۵ عدد است.")
        return id
    }

    fun login(phone: String, password: String): User {
        val cursor = readableDatabase.rawQuery("SELECT id, salt, password_hash, suspended FROM users WHERE phone=?", arrayOf(phone))
        cursor.use {
            if (!it.moveToFirst()) error("شماره یا رمز درست نیست")
            val salt = it.getString(1)
            val hash = it.getString(2)
            if (hashPassword(password, salt) != hash) error("شماره یا رمز درست نیست")
            if (it.getInt(3) == 1) error("این حساب معلق شده")
            val id = it.getString(0)
            it.close()
            return user(id) ?: error("شماره یا رمز درست نیست")
        }
    }

    fun reset(phone: String, password: String) {
        if (password.length < 8) error("رمز باید حداقل ۸ کاراکتر باشد")
        val user = findUser(phone) ?: error("حسابی با این شماره نیست")
        val salt = UUID.randomUUID().toString()
        val values = ContentValues().apply {
            put("salt", salt)
            put("password_hash", hashPassword(password, salt))
        }
        writableDatabase.update("users", values, "id=?", arrayOf(user.id))
    }

    fun user(id: String): User? {
        val cursor = readableDatabase.rawQuery(
            "SELECT id, phone, name, plan, balance, plan_expires_at, suspended, banner_seen, chat_bg, chat_color, avatar, username, monthly_download_count, monthly_ai_usage_count, last_quota_reset_month FROM users WHERE id=?",
            arrayOf(id),
        )
        cursor.use {
            if (!it.moveToFirst()) return null
            val expires = if (it.isNull(5)) null else it.getLong(5)
            val plan = if (expires != null && expires < System.currentTimeMillis()) "free" else it.getString(3)
            val user = User(
                it.getString(0),
                it.getString(1),
                it.getString(2),
                plan,
                it.getInt(4),
                expires,
                it.getInt(6) == 1,
                it.getInt(7) == 1,
                if (it.isNull(8)) null else it.getString(8),
                if (it.isNull(9)) null else it.getString(9),
                if (it.isNull(10)) null else it.getString(10),
                if (it.isNull(11)) null else it.getString(11),
                it.getInt(12),
                it.getInt(13),
                if (it.isNull(14)) null else it.getString(14),
            )
            return ensureQuotaReset(user)
        }
    }

    private fun ensureQuotaReset(user: User): User {
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())
        if (user.lastQuotaResetMonth != currentMonth) {
            writableDatabase.update("users", ContentValues().apply {
                put("monthly_download_count", 0)
                put("monthly_ai_usage_count", 0)
                put("last_quota_reset_month", currentMonth)
            }, "id=?", arrayOf(user.id))
            return user.copy(monthlyDownloadCount = 0, monthlyAiUsageCount = 0, lastQuotaResetMonth = currentMonth)
        }
        return user
    }

    fun incrementAiUsage(userId: String) {
        writableDatabase.execSQL("UPDATE users SET monthly_ai_usage_count = monthly_ai_usage_count + 1 WHERE id=?", arrayOf(userId))
    }

    fun incrementDownloadUsage(userId: String) {
        writableDatabase.execSQL("UPDATE users SET monthly_download_count = monthly_download_count + 1 WHERE id=?", arrayOf(userId))
    }

    fun mediaCount(userId: String): Int {
        val cursor = readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM entries WHERE user_id=? AND (voice_path IS NOT NULL OR media_path IS NOT NULL)",
            arrayOf(userId)
        )
        return cursor.use { if (it.moveToFirst()) it.getInt(0) else 0 }
    }

    fun setAvatar(userId: String, path: String?) {
        writableDatabase.update("users", ContentValues().apply {
            if (path == null) putNull("avatar") else put("avatar", path)
        }, "id=?", arrayOf(userId))
    }

    fun setUsername(userId: String, username: String?) {
        writableDatabase.update("users", ContentValues().apply {
            if (username.isNullOrBlank()) putNull("username") else put("username", username.trim().removePrefix("@"))
        }, "id=?", arrayOf(userId))
    }

    fun setChatStyle(userId: String, background: String?, color: String?) {
        writableDatabase.update("users", ContentValues().apply {
            if (background == null) putNull("chat_bg") else put("chat_bg", background)
            if (color == null) putNull("chat_color") else put("chat_color", color)
        }, "id=?", arrayOf(userId))
    }

    private fun findUser(phone: String): User? {
        val id = readableDatabase.rawQuery("SELECT id FROM users WHERE phone=?", arrayOf(phone)).use {
            if (it.moveToFirst()) it.getString(0) else null
        } ?: return null
        return user(id)
    }

    fun findUserByContact(query: String): User? {
        val q = query.trim().removePrefix("@")
        if (q.isBlank()) return null
        val cursor = readableDatabase.rawQuery(
            "SELECT id FROM users WHERE phone=? OR username=? LIMIT 1",
            arrayOf(q, q)
        )
        val id = cursor.use { if (it.moveToFirst()) it.getString(0) else null } ?: return null
        return user(id)
    }

    fun people(userId: String): List<Person> {
        val cursor = readableDatabase.rawQuery("SELECT id, name, phone, role, pinned, created_at, avatar, is_group, saved FROM people WHERE user_id=? ORDER BY pinned DESC, created_at DESC", arrayOf(userId))
        val list = mutableListOf<Person>()
        val day = startOfDay()
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getString(0)
                val last = readableDatabase.rawQuery(
                    "SELECT body, occurred_at, kind, channel FROM entries WHERE person_id=? ORDER BY occurred_at DESC LIMIT 1",
                    arrayOf(id),
                )
                var body = ""
                var at = it.getLong(5)
                var kind: String? = null
                var channel: String? = null
                last.use { row ->
                    if (row.moveToFirst()) {
                        body = row.getString(0)
                        at = row.getLong(1)
                        kind = row.getString(2)
                        channel = row.getString(3)
                    }
                }
                val notes = readableDatabase.rawQuery("SELECT 1 FROM entries WHERE person_id=? AND kind='note' LIMIT 1", arrayOf(id))
                val hasNote = notes.use { n -> n.moveToFirst() }
                val unread = readableDatabase.rawQuery("SELECT COUNT(*) FROM entries WHERE person_id=? AND occurred_at>=?", arrayOf(id, day.toString())).use { row -> if (row.moveToFirst()) row.getInt(0) else 0 }
                list += Person(id, it.getString(1), it.getString(2) ?: "", it.getString(3) ?: "", it.getInt(4) == 1, body, at, kind, channel, hasNote, if (it.isNull(6)) null else it.getString(6), unread, it.getInt(7) == 1, it.getInt(8) == 1)
            }
        }
        return list.sortedWith(compareByDescending<Person> { it.pinned }.thenByDescending { it.lastAt })
    }

    fun addPerson(userId: String, name: String, phone: String, role: String, avatar: String? = null, members: List<String> = emptyList(), saved: Boolean = false): String {
        if (name.isBlank()) error("نام را بنویسید")
        val owner = user(userId)
        if (owner?.plan == "guest" && people(userId).any { !it.saved }) error("در حالت مهمان فقط یک نفر می‌توانید اضافه کنید")
        val mobile = if (phone.isBlank()) "" else normalizePhone(phone) ?: error("شماره موبایل درست نیست")
        if (mobile.isNotEmpty()) {
            val taken = readableDatabase.rawQuery("SELECT 1 FROM people WHERE user_id=? AND phone=? LIMIT 1", arrayOf(userId, mobile)).use { it.moveToFirst() }
            if (taken) error("این شماره قبلاً ثبت شده")
        }
        val id = UUID.randomUUID().toString()
        writableDatabase.insert("people", null, ContentValues().apply {
            put("id", id)
            put("user_id", userId)
            put("name", name.trim())
            put("phone", mobile)
            put("role", role.trim())
            put("pinned", 0)
            put("created_at", System.currentTimeMillis())
            if (avatar == null) putNull("avatar") else put("avatar", avatar)
            put("is_group", if (members.any { it.isNotBlank() }) 1 else 0)
            put("saved", if (saved) 1 else 0)
        })
        members.map { it.trim() }.filter { it.isNotEmpty() }.distinct().forEach { addMember(id, it) }
        return id
    }

    fun members(personId: String): List<Member> = readableDatabase.rawQuery(
        "SELECT id, name FROM members WHERE person_id=? ORDER BY created_at",
        arrayOf(personId),
    ).use {
        val list = mutableListOf<Member>()
        while (it.moveToNext()) {
            val id = it.getString(0)
            var name = it.getString(1)
            // If name is a UUID, it's likely a user ID from old data
            if (isUuid(name)) {
                user(name)?.let { u ->
                    name = if (!u.name.isNullOrBlank() && !u.username.isNullOrBlank()) {
                        "${u.name} (@${u.username})"
                    } else if (!u.username.isNullOrBlank()) {
                        "@${u.username}"
                    } else {
                        u.name.ifBlank { "کاربر ${u.phone.takeLast(4)}" }
                    }
                    // Optionally update the database to fix it permanently
                    writableDatabase.execSQL("UPDATE members SET name=? WHERE id=?", arrayOf(name, id))
                }
            }
            list += Member(id, name)
        }
        list
    }

    private fun isUuid(s: String?): Boolean {
        if (s == null || s.length != 36) return false
        return try {
            UUID.fromString(s)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun addMember(personId: String, contact: String): String {
        if (contact.isBlank()) error("شماره تماس یا نام کاربری را بنویسید")
        val user = findUserByContact(contact) ?: error("کاربری با این مشخصات یافت نشد")
        
        // Check for Plus Subscription
        if (user.plan != "advanced") {
            error("تنها کاربران دارای اشتراک پلاس امکان عضویت در تیم را دارند")
        }
        
        val nameToSave = if (!user.name.isNullOrBlank() && !user.username.isNullOrBlank()) {
            "${user.name} (@${user.username})"
        } else if (!user.username.isNullOrBlank()) {
            "@${user.username}"
        } else {
            user.name.ifBlank { "کاربر ${user.phone.takeLast(4)}" }
        }
        
        val id = UUID.randomUUID().toString()
        writableDatabase.insert("members", null, ContentValues().apply {
            put("id", id)
            put("person_id", personId)
            put("name", nameToSave)
            put("created_at", System.currentTimeMillis())
        })
        writableDatabase.update("people", ContentValues().apply { put("is_group", 1) }, "id=?", arrayOf(personId))
        return id
    }

    fun ensureSaved(userId: String): String {
        val existing = readableDatabase.rawQuery("SELECT id FROM people WHERE user_id=? AND saved=1 LIMIT 1", arrayOf(userId)).use {
            if (it.moveToFirst()) it.getString(0) else null
        }
        if (existing != null) return existing
        return addPerson(userId, "پیام‌های ذخیره‌شده", "", "", saved = true)
    }

    fun setPersonAvatar(id: String, path: String?) {
        writableDatabase.update("people", ContentValues().apply {
            if (path == null) putNull("avatar") else put("avatar", path)
        }, "id=?", arrayOf(id))
    }

    fun updatePerson(id: String, name: String, phone: String, role: String) {
        if (name.isBlank()) error("نام را بنویسید")
        val mobile = if (phone.isBlank()) "" else normalizePhone(phone) ?: error("شماره موبایل درست نیست")
        if (mobile.isNotEmpty()) {
            val taken = readableDatabase.rawQuery("SELECT 1 FROM people WHERE user_id=(SELECT user_id FROM people WHERE id=?) AND phone=? AND id<>? LIMIT 1", arrayOf(id, mobile, id)).use { it.moveToFirst() }
            if (taken) error("این شماره قبلاً ثبت شده")
        }
        writableDatabase.update("people", ContentValues().apply {
            put("name", name.trim())
            put("phone", mobile)
            put("role", role.trim())
        }, "id=?", arrayOf(id))
    }

    fun setPinned(id: String, pinned: Boolean) {
        writableDatabase.update("people", ContentValues().apply { put("pinned", if (pinned) 1 else 0) }, "id=?", arrayOf(id))
    }

    fun updatePersonArchived(personId: String, archived: Boolean) {
        writableDatabase.update("people", ContentValues().apply { put("archived", if (archived) 1 else 0) }, "id=?", arrayOf(personId))
    }

    fun entries(personId: String): List<Entry> {
        val cursor = readableDatabase.rawQuery(
            "SELECT e.id, e.user_id, e.body, e.kind, e.channel, e.occurred_at, e.pinned, e.voice_path, e.media_path, m.name, e.target_recipient_id FROM entries e LEFT JOIN members m ON m.id = e.member_id WHERE e.person_id=? ORDER BY e.pinned DESC, e.occurred_at",
            arrayOf(personId),
        )
        return cursor.use {
            val list = mutableListOf<Entry>()
            while (it.moveToNext()) {
                list += Entry(
                    it.getString(0),
                    it.getString(1),
                    it.getString(2),
                    it.getString(3),
                    it.getString(4),
                    it.getLong(5),
                    it.getInt(6) == 1,
                    if (it.isNull(7)) null else it.getString(7),
                    if (it.isNull(8)) null else it.getString(8),
                    if (it.isNull(9)) null else it.getString(9),
                    if (it.isNull(10)) null else it.getString(10),
                )
            }
            list
        }
    }

    fun personName(id: String): String {
        val cursor = readableDatabase.rawQuery("SELECT name FROM people WHERE id=?", arrayOf(id))
        return cursor.use { if (it.moveToFirst()) it.getString(0) else "" }
    }

    fun teams(userId: String): List<Team> {
        val cursor = readableDatabase.rawQuery(
            """SELECT p.id, p.name, p.role, p.created_at,
                      (SELECT COUNT(*) FROM members m WHERE m.person_id = p.id),
                      (SELECT body FROM entries e WHERE e.person_id = p.id ORDER BY e.occurred_at DESC LIMIT 1),
                      (SELECT occurred_at FROM entries e WHERE e.person_id = p.id ORDER BY e.occurred_at DESC LIMIT 1)
               FROM people p
               WHERE p.user_id = ? AND p.is_group = 1
               ORDER BY created_at DESC""",
            arrayOf(userId)
        )
        val list = mutableListOf<Team>()
        cursor.use {
            while (it.moveToNext()) {
                list += Team(
                    it.getString(0),
                    it.getString(1),
                    it.getString(2) ?: "",
                    it.getLong(3),
                    it.getInt(4),
                    it.getString(5) ?: "",
                    it.getLong(6).takeIf { at -> at > 0 } ?: it.getLong(3)
                )
            }
        }
        return list
    }

    fun addTeam(userId: String, name: String, description: String, members: List<String>) {
        addPerson(userId, name, "", description, members = members)
    }

    fun quota(user: User): Pair<Int, Int> {
        if (user.plan == "guest") {
            val used = readableDatabase.rawQuery("SELECT COUNT(*) FROM entries WHERE user_id=?", arrayOf(user.id)).use { if (it.moveToFirst()) it.getInt(0) else 0 }
            return used to 5
        }
        val limit = if (user.plan == "free") 5 else 1000000
        val start = startOfDay()
        val cursor = readableDatabase.rawQuery("SELECT COUNT(*) FROM entries WHERE user_id=? AND created_at>=?", arrayOf(user.id, start.toString()))
        val used = cursor.use { if (it.moveToFirst()) it.getInt(0) else 0 }
        return used to limit
    }

    fun setEntryPinned(id: String, pinned: Boolean) {
        writableDatabase.update("entries", ContentValues().apply { put("pinned", if (pinned) 1 else 0) }, "id=?", arrayOf(id))
    }

    fun addEntry(user: User, personId: String, body: String, kind: String, channel: String, occurredAt: Long, voicePath: String? = null, mediaPath: String? = null, memberId: String? = null, targetRecipientId: String? = null) {
        if (voicePath != null || mediaPath != null) {
            if (user.plan == "free" && mediaCount(user.id) >= 20) {
                error("سقف بارگذاری فایل: شما به سقف مجاز ۲۰ فایل در پلن رایگان رسیده‌اید. برای ارسال نامحدود فایل، اشتراک پیشرفته را فعال کنید.")
            }
        }
        val (used, limit) = quota(user)
        if (used >= limit) {
            if (user.plan == "guest") error("در حالت مهمان فقط ۵ پیام آزمایشی می‌توانید بفرستید")
            error("سقف پلن شما به اتمام رسیده، لطفا پلن خود را تغییر دهید")
        }
        val now = System.currentTimeMillis()
        writableDatabase.insert("entries", null, ContentValues().apply {
            put("id", UUID.randomUUID().toString())
            put("user_id", user.id)
            put("person_id", personId)
            put("body", body.trim())
            put("kind", kind)
            put("channel", channel)
            put("occurred_at", occurredAt)
            put("created_at", now)
            put("updated_at", now)
            put("pinned", 0)
            if (voicePath == null) putNull("voice_path") else put("voice_path", voicePath)
            if (mediaPath == null) putNull("media_path") else put("media_path", mediaPath)
            if (memberId == null) putNull("member_id") else put("member_id", memberId)
            if (targetRecipientId == null) putNull("target_recipient_id") else put("target_recipient_id", targetRecipientId)
        })
    }

    fun submitSlip(userId: String, kind: String, plan: String?, amount: Int, receipt: String?, refCode: String?) {
        if (amount < 1) error("مبلغ را بنویس")
        if (receipt.isNullOrBlank() && refCode.isNullOrBlank()) error("عکس رسید یا شناسه واریز لازم است")
        writableDatabase.insert("slips", null, ContentValues().apply {
            put("id", UUID.randomUUID().toString())
            put("user_id", userId)
            put("kind", kind)
            put("plan", plan)
            put("amount", amount)
            put("status", "pending")
            if (receipt == null) putNull("receipt") else put("receipt", receipt)
            if (refCode == null) putNull("ref_code") else put("ref_code", refCode)
            put("created_at", System.currentTimeMillis())
        })
    }

    fun slips(): List<Slip> = readableDatabase.rawQuery("SELECT id, user_id, kind, plan, amount, status, receipt, ref_code, created_at FROM slips ORDER BY created_at DESC", null).use {
        val list = mutableListOf<Slip>()
        while (it.moveToNext()) {
            val user = user(it.getString(1))
            list += Slip(it.getString(0), it.getString(1), user?.name ?: "", it.getString(2), if (it.isNull(3)) null else it.getString(3), it.getInt(4), it.getString(5), if (it.isNull(6)) null else it.getString(6), if (it.isNull(7)) null else it.getString(7), it.getLong(8))
        }
        list
    }

    fun userSlips(userId: String): List<Slip> = readableDatabase.rawQuery("SELECT id, user_id, kind, plan, amount, status, receipt, ref_code, created_at FROM slips WHERE user_id=? ORDER BY created_at DESC", arrayOf(userId)).use {
        val list = mutableListOf<Slip>()
        while (it.moveToNext()) {
            val user = user(it.getString(1))
            list += Slip(it.getString(0), it.getString(1), user?.name ?: "", it.getString(2), if (it.isNull(3)) null else it.getString(3), it.getInt(4), it.getString(5), if (it.isNull(6)) null else it.getString(6), if (it.isNull(7)) null else it.getString(7), it.getLong(8))
        }
        list
    }

    fun addExportLog(userId: String, fileType: String, targetName: String, count: Int) {
        writableDatabase.insert("export_logs", null, ContentValues().apply {
            put("id", UUID.randomUUID().toString())
            put("user_id", userId)
            put("timestamp", System.currentTimeMillis())
            put("file_type", fileType)
            put("target_name", targetName)
            put("records_count", count)
        })
    }

    fun exportLogs(userId: String): List<ExportLog> = readableDatabase.rawQuery("SELECT id, user_id, timestamp, file_type, target_name, records_count FROM export_logs WHERE user_id=? ORDER BY timestamp DESC", arrayOf(userId)).use {
        val list = mutableListOf<ExportLog>()
        while (it.moveToNext()) {
            list += ExportLog(it.getString(0), it.getString(1), it.getLong(2), it.getString(3), it.getString(4), it.getInt(5))
        }
        list
    }

    fun reviewSlip(id: String, status: String) {
        val slip = slips().find { it.id == id } ?: error("پرداخت پیدا نشد")
        if (slip.status != "pending") error("این پرداخت بررسی شده")
        writableDatabase.update("slips", ContentValues().apply { put("status", status) }, "id=?", arrayOf(id))
        if (status == "approved") {
            topUp(slip.userId, slip.amount)
            // If the slip is for a plan, activate it immediately and deduct price
            if (!slip.plan.isNullOrBlank()) {
                val isSeasonal = slip.plan.contains("فصلی")
                val planType = if (isSeasonal) "advanced" else "advanced" // Plus is advanced
                val price = if (isSeasonal) 2_500_000 else 1_000_000
                val durationDays = if (isSeasonal) 90L else 30L
                val expires = System.currentTimeMillis() + durationDays * 24 * 60 * 60 * 1000
                
                writableDatabase.execSQL(
                    "UPDATE users SET balance = balance - ?, plan=?, plan_expires_at=? WHERE id=?",
                    arrayOf<Any>(price.toLong(), "advanced", expires, slip.userId),
                )
                addNotice(slip.userId, "اشتراک فعال شد", "اشتراک ${slip.plan} با موفقیت فعال شد و قابلیت تیم‌ها باز گردید.")
            }
        }
    }

    fun updateEntry(id: String, body: String, kind: String, channel: String, occurredAt: Long) {
        writableDatabase.update("entries", ContentValues().apply {
            put("body", body.trim())
            put("kind", kind)
            put("channel", channel)
            put("occurred_at", occurredAt)
            put("updated_at", System.currentTimeMillis())
        }, "id=?", arrayOf(id))
    }

    fun clearEntries(personId: String) {
        writableDatabase.delete("entries", "person_id=?", arrayOf(personId))
    }

    fun deleteEntry(id: String) {
        writableDatabase.delete("entries", "id=?", arrayOf(id))
    }

    fun deletePerson(personId: String) {
        val db = writableDatabase
        db.delete("entries", "person_id=?", arrayOf(personId))
        db.delete("members", "person_id=?", arrayOf(personId))
        db.delete("ai_messages", "person_id=?", arrayOf(personId))
        db.delete("people", "id=?", arrayOf(personId))
    }

    fun notices(userId: String): List<Notice> {
        val cursor = readableDatabase.rawQuery("SELECT id, title, body, created_at, read FROM notices WHERE user_id=? ORDER BY created_at DESC", arrayOf(userId))
        return cursor.use {
            val list = mutableListOf<Notice>()
            while (it.moveToNext()) list += Notice(it.getString(0), it.getString(1), it.getString(2), it.getLong(3), it.getInt(4) == 1)
            list
        }
    }

    fun markNoticesRead(userId: String) {
        writableDatabase.update("notices", ContentValues().apply { put("read", 1) }, "user_id=?", arrayOf(userId))
    }

    fun saveBanner(title: String, body: String, image: String? = null) {
        if (title.isBlank() || body.isBlank()) error("عنوان و متن بنر لازم است")
        writableDatabase.delete("banners", null, null)
        writableDatabase.insert("banners", null, ContentValues().apply {
            put("id", UUID.randomUUID().toString())
            put("title", title.trim())
            put("body", body.trim())
            if (image == null) putNull("image") else put("image", image)
            put("active", 1)
            put("created_at", System.currentTimeMillis())
        })
        writableDatabase.execSQL("UPDATE users SET banner_seen = 0")
    }

    fun activeBanner(): Banner? {
        val cursor = readableDatabase.rawQuery("SELECT id, title, body, image FROM banners WHERE active=1 ORDER BY created_at DESC LIMIT 1", null)
        return cursor.use {
            if (!it.moveToFirst()) return null
            Banner(it.getString(0), it.getString(1), it.getString(2), if (it.isNull(3)) null else it.getString(3))
        }
    }

    fun unreadNotices(userId: String): List<Notice> = notices(userId).filter { !it.read }

    fun markBannerSeen(userId: String) {
        writableDatabase.update("users", ContentValues().apply { put("banner_seen", 1) }, "id=?", arrayOf(userId))
    }

    private fun addNotice(userId: String, title: String, body: String) {
        writableDatabase.insert("notices", null, ContentValues().apply {
            put("id", UUID.randomUUID().toString())
            put("user_id", userId)
            put("title", title)
            put("body", body)
            put("read", 0)
            put("created_at", System.currentTimeMillis())
        })
    }

    fun rename(userId: String, name: String) {
        if (name.trim().length < 2) error("نام را کامل‌تر بنویس")
        writableDatabase.update("users", ContentValues().apply { put("name", name.trim()) }, "id=?", arrayOf(userId))
    }

    fun topUp(userId: String, amount: Int) {
        if (amount < 1) error("مبلغ شارژ را بنویس")
        writableDatabase.execSQL("UPDATE users SET balance = balance + ? WHERE id=?", arrayOf<Any>(amount.toLong(), userId))
    }

    fun buyPlan(user: User, plan: String): String {
        val isSeasonal = plan.contains("فصلی")
        val price = if (isSeasonal) 2_500_000 else 1_000_000
        val durationDays = if (isSeasonal) 90L else 30L
        
        if (user.balance < price) error("موجودی کیف پول کافی نیست")
        val expires = System.currentTimeMillis() + durationDays * 24 * 60 * 60 * 1000
        writableDatabase.execSQL(
            "UPDATE users SET balance = balance - ?, plan=?, plan_expires_at=? WHERE id=?",
            arrayOf<Any>(price.toLong(), "advanced", expires, user.id),
        )
        addNotice(user.id, "پلن فعال شد", "پلن ${if (isSeasonal) "فصلی" else "ماهانه"} با موفقیت فعال شد.")
        return "پیشرفته"
    }

    fun users(): List<User> = readableDatabase.rawQuery("SELECT id FROM users ORDER BY created_at DESC", null).use {
        val list = mutableListOf<User>()
        while (it.moveToNext()) user(it.getString(0))?.let(list::add)
        list
    }

    fun setSuspended(id: String, suspended: Boolean) {
        writableDatabase.update("users", ContentValues().apply { put("suspended", if (suspended) 1 else 0) }, "id=?", arrayOf(id))
    }

    data class AiModel(val id: String, val name: String, val endpoint: String, val modelId: String, val apiKey: String, val inputCost: Double, val outputCost: Double, val active: Boolean)

    fun models(): List<AiModel> = readableDatabase.rawQuery("SELECT id, name, endpoint, model_id, api_key, input_cost, output_cost, active FROM ai_models ORDER BY created_at DESC", null).use {
        val list = mutableListOf<AiModel>()
        while (it.moveToNext()) list += AiModel(it.getString(0), it.getString(1), it.getString(2), it.getString(3), it.getString(4), it.getDouble(5), it.getDouble(6), it.getInt(7) == 1)
        list
    }

    fun activeModel(): AiModel? = models().firstOrNull { it.active } ?: models().firstOrNull()

    fun aiMessages(userId: String, personId: String): List<AiMessage> = readableDatabase.rawQuery(
        "SELECT id, person_id, role, body, image_path, created_at FROM ai_messages WHERE user_id=? AND person_id=? ORDER BY created_at",
        arrayOf(userId, personId),
    ).use {
        val list = mutableListOf<AiMessage>()
        while (it.moveToNext()) {
            list += AiMessage(it.getString(0), it.getString(1), it.getString(2), it.getString(3), if (it.isNull(4)) null else it.getString(4), it.getLong(5))
        }
        list
    }

    fun addAiMessage(userId: String, personId: String, role: String, body: String, image: String?, createdAt: Long = System.currentTimeMillis()): AiMessage {
        val id = UUID.randomUUID().toString()
        writableDatabase.insert("ai_messages", null, ContentValues().apply {
            put("id", id)
            put("user_id", userId)
            put("person_id", personId)
            put("role", role)
            put("body", body)
            if (image == null) putNull("image_path") else put("image_path", image)
            put("created_at", createdAt)
        })
        return AiMessage(id, personId, role, body, image, createdAt)
    }

    fun updateAiMessage(id: String, body: String, image: String?) {
        writableDatabase.update("ai_messages", ContentValues().apply {
            put("body", body)
            if (image == null) putNull("image_path") else put("image_path", image)
        }, "id=?", arrayOf(id))
    }

    fun deleteAiAfter(userId: String, personId: String, createdAt: Long) {
        writableDatabase.delete("ai_messages", "user_id=? AND person_id=? AND created_at>?", arrayOf(userId, personId, createdAt.toString()))
    }

    fun clearAi(userId: String, personId: String) {
        writableDatabase.delete("ai_messages", "user_id=? AND person_id=?", arrayOf(userId, personId))
    }

    fun ensureOpenRouter() {
        if (models().isNotEmpty()) return
        val key = BuildConfig.OPENROUTER_KEY.trim()
        if (key.isBlank()) return
        addModel("Gemma 4 31B", "https://openrouter.ai/api/v1", "google/gemma-4-31b-it:free", key, 0.0, 0.0)
    }

    fun addModel(name: String, endpoint: String, modelId: String, apiKey: String, inputCost: Double, outputCost: Double) {
        if (name.isBlank() || modelId.isBlank()) error("نام و شناسه مدل لازم است")
        val id = UUID.randomUUID().toString()
        val makeActive = models().none { it.active }
        writableDatabase.insert("ai_models", null, ContentValues().apply {
            put("id", id)
            put("name", name.trim())
            put("endpoint", endpoint.trim().ifBlank { "https://api.openai.com/v1" })
            put("model_id", modelId.trim())
            put("api_key", apiKey.trim())
            put("input_cost", inputCost)
            put("output_cost", outputCost)
            put("active", if (makeActive) 1 else 0)
            put("created_at", System.currentTimeMillis())
        })
    }

    fun activateModel(id: String) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.execSQL("UPDATE ai_models SET active=0")
            db.update("ai_models", ContentValues().apply { put("active", 1) }, "id=?", arrayOf(id))
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun deleteModel(id: String) {
        val wasActive = models().any { it.id == id && it.active }
        writableDatabase.delete("ai_models", "id=?", arrayOf(id))
        if (wasActive) {
            val next = models().firstOrNull() ?: return
            activateModel(next.id)
        }
    }

    fun announcements(): List<Notice> = readableDatabase.rawQuery("SELECT id, title, body, created_at FROM announcements ORDER BY created_at DESC", null).use {
        val list = mutableListOf<Notice>()
        while (it.moveToNext()) list += Notice(it.getString(0), it.getString(1), it.getString(2), it.getLong(3), true)
        list
    }

    fun publishAnnouncement(title: String, body: String) {
        if (title.isBlank() || body.isBlank()) error("عنوان و متن لازم است")
        writableDatabase.insert("announcements", null, ContentValues().apply {
            put("id", UUID.randomUUID().toString())
            put("title", title.trim())
            put("body", body.trim())
            put("created_at", System.currentTimeMillis())
        })
        users().forEach { addNotice(it.id, title.trim(), body.trim()) }
    }

    fun deleteAnnouncement(id: String) {
        writableDatabase.delete("announcements", "id=?", arrayOf(id))
    }

    private fun startOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
