package ir.daftar.goftogu

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.Calendar
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
)

data class Entry(
    val id: String,
    val body: String,
    val kind: String,
    val channel: String,
    val occurredAt: Long,
    val pinned: Boolean = false,
    val voicePath: String? = null,
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
)

data class Notice(val id: String, val title: String, val body: String, val createdAt: Long, val read: Boolean)
data class Banner(val id: String, val title: String, val body: String, val image: String? = null)

class Db(context: Context) : SQLiteOpenHelper(context, "daftar.db", null, 9) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE users (
                id TEXT PRIMARY KEY, phone TEXT UNIQUE, salt TEXT, password_hash TEXT,
                name TEXT, plan TEXT, balance INTEGER, plan_expires_at INTEGER, suspended INTEGER,
                banner_seen INTEGER NOT NULL DEFAULT 0, chat_bg TEXT, chat_color TEXT, avatar TEXT, created_at INTEGER
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
                occurred_at INTEGER, created_at INTEGER, updated_at INTEGER
            )""",
        )
        db.execSQL("CREATE TABLE notices (id TEXT PRIMARY KEY, user_id TEXT, title TEXT, body TEXT, read INTEGER, created_at INTEGER)")
        db.execSQL("CREATE TABLE banners (id TEXT PRIMARY KEY, title TEXT NOT NULL, body TEXT NOT NULL, image TEXT, active INTEGER NOT NULL DEFAULT 1, created_at INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE ai_models (id TEXT PRIMARY KEY, name TEXT, endpoint TEXT, model_id TEXT, api_key TEXT, input_cost REAL, output_cost REAL, created_at INTEGER)")
        db.execSQL("CREATE TABLE announcements (id TEXT PRIMARY KEY, title TEXT, body TEXT, created_at INTEGER)")
        db.execSQL("CREATE TABLE slips (id TEXT PRIMARY KEY, user_id TEXT, kind TEXT, plan TEXT, amount INTEGER, status TEXT, receipt TEXT, ref_code TEXT, created_at INTEGER)")
        db.execSQL("ALTER TABLE entries ADD COLUMN pinned INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE entries ADD COLUMN voice_path TEXT")
        db.execSQL("CREATE TABLE wallpapers (id TEXT PRIMARY KEY, title TEXT, category TEXT, path TEXT, created_at INTEGER)")
        db.execSQL("CREATE TABLE outbox (id TEXT PRIMARY KEY, kind TEXT, payload TEXT, created_at INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
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
        db.execSQL("CREATE TABLE IF NOT EXISTS ai_models (id TEXT PRIMARY KEY, name TEXT, endpoint TEXT, model_id TEXT, api_key TEXT, input_cost REAL, output_cost REAL, created_at INTEGER)")
        db.execSQL("CREATE TABLE IF NOT EXISTS announcements (id TEXT PRIMARY KEY, title TEXT, body TEXT, created_at INTEGER)")
        db.execSQL("CREATE TABLE IF NOT EXISTS slips (id TEXT PRIMARY KEY, user_id TEXT, kind TEXT, plan TEXT, amount INTEGER, status TEXT, receipt TEXT, ref_code TEXT, created_at INTEGER)")
        val entryColumns = db.rawQuery("PRAGMA table_info(entries)", null).use {
            val names = mutableListOf<String>()
            while (it.moveToNext()) names += it.getString(1)
            names
        }
        if ("pinned" !in entryColumns) db.execSQL("ALTER TABLE entries ADD COLUMN pinned INTEGER NOT NULL DEFAULT 0")
        if ("voice_path" !in entryColumns) db.execSQL("ALTER TABLE entries ADD COLUMN voice_path TEXT")
        db.execSQL("CREATE TABLE IF NOT EXISTS wallpapers (id TEXT PRIMARY KEY, title TEXT, category TEXT, path TEXT, created_at INTEGER)")
        db.execSQL("CREATE TABLE IF NOT EXISTS outbox (id TEXT PRIMARY KEY, kind TEXT, payload TEXT, created_at INTEGER)")
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

    fun ensureAdmin() {
        val phone = "09012345678"
        val password = "11111111"
        if (findUser(phone) == null) signup(phone, "ادمین", password) else reset(phone, password)
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
            "SELECT id, phone, name, plan, balance, plan_expires_at, suspended, banner_seen, chat_bg, chat_color, avatar FROM users WHERE id=?",
            arrayOf(id),
        )
        cursor.use {
            if (!it.moveToFirst()) return null
            val expires = if (it.isNull(5)) null else it.getLong(5)
            val plan = if (expires != null && expires < System.currentTimeMillis()) "free" else it.getString(3)
            return User(
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
            )
        }
    }

    fun setAvatar(userId: String, path: String?) {
        writableDatabase.update("users", ContentValues().apply {
            if (path == null) putNull("avatar") else put("avatar", path)
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

    fun people(userId: String): List<Person> {
        val cursor = readableDatabase.rawQuery("SELECT id, name, phone, role, pinned, created_at FROM people WHERE user_id=? ORDER BY pinned DESC, created_at DESC", arrayOf(userId))
        val list = mutableListOf<Person>()
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
                list += Person(id, it.getString(1), it.getString(2) ?: "", it.getString(3) ?: "", it.getInt(4) == 1, body, at, kind, channel, hasNote)
            }
        }
        return list.sortedWith(compareByDescending<Person> { it.pinned }.thenByDescending { it.lastAt })
    }

    fun addPerson(userId: String, name: String, phone: String, role: String): String {
        if (name.isBlank() || role.isBlank()) error("لطفا همه فیلدها را پر کنید")
        val taken = readableDatabase.rawQuery("SELECT 1 FROM people WHERE user_id=? AND phone=? LIMIT 1", arrayOf(userId, phone)).use { it.moveToFirst() }
        if (taken) error("این شماره قبلاً ثبت شده")
        val id = UUID.randomUUID().toString()
        writableDatabase.insert("people", null, ContentValues().apply {
            put("id", id)
            put("user_id", userId)
            put("name", name.trim())
            put("phone", phone.trim())
            put("role", role.trim())
            put("pinned", 0)
            put("created_at", System.currentTimeMillis())
        })
        return id
    }

    fun updatePerson(id: String, name: String, phone: String, role: String) {
        if (name.isBlank() || role.isBlank()) error("لطفا همه فیلدها را پر کنید")
        val mobile = normalizePhone(phone) ?: error("شماره موبایل درست نیست")
        val taken = readableDatabase.rawQuery("SELECT 1 FROM people WHERE user_id=(SELECT user_id FROM people WHERE id=?) AND phone=? AND id<>? LIMIT 1", arrayOf(id, mobile, id)).use { it.moveToFirst() }
        if (taken) error("این شماره قبلاً ثبت شده")
        writableDatabase.update("people", ContentValues().apply {
            put("name", name.trim())
            put("phone", mobile)
            put("role", role.trim())
        }, "id=?", arrayOf(id))
    }

    fun setPinned(id: String, pinned: Boolean) {
        writableDatabase.update("people", ContentValues().apply { put("pinned", if (pinned) 1 else 0) }, "id=?", arrayOf(id))
    }

    fun entries(personId: String): List<Entry> {
        val cursor = readableDatabase.rawQuery(
            "SELECT id, body, kind, channel, occurred_at, pinned, voice_path FROM entries WHERE person_id=? ORDER BY pinned DESC, occurred_at",
            arrayOf(personId),
        )
        return cursor.use {
            val list = mutableListOf<Entry>()
            while (it.moveToNext()) list += Entry(it.getString(0), it.getString(1), it.getString(2), it.getString(3), it.getLong(4), it.getInt(5) == 1, if (it.isNull(6)) null else it.getString(6))
            list
        }
    }

    fun personName(id: String): String {
        val cursor = readableDatabase.rawQuery("SELECT name FROM people WHERE id=?", arrayOf(id))
        return cursor.use { if (it.moveToFirst()) it.getString(0) else "" }
    }

    fun quota(user: User): Pair<Int, Int> {
        val limit = if (user.plan == "free") 5 else 100
        val start = startOfDay()
        val cursor = readableDatabase.rawQuery("SELECT COUNT(*) FROM entries WHERE user_id=? AND created_at>=?", arrayOf(user.id, start.toString()))
        val used = cursor.use { if (it.moveToFirst()) it.getInt(0) else 0 }
        return used to limit
    }

    fun setEntryPinned(id: String, pinned: Boolean) {
        writableDatabase.update("entries", ContentValues().apply { put("pinned", if (pinned) 1 else 0) }, "id=?", arrayOf(id))
    }

    fun addEntry(user: User, personId: String, body: String, kind: String, channel: String, occurredAt: Long, voicePath: String? = null) {
        val (used, limit) = quota(user)
        if (used >= limit) error("سقف یادداشت امروز تمام شده")
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

    fun slips(): List<Slip> = readableDatabase.rawQuery("SELECT id, user_id, kind, plan, amount, status, receipt, ref_code FROM slips ORDER BY created_at DESC", null).use {
        val list = mutableListOf<Slip>()
        while (it.moveToNext()) {
            val user = user(it.getString(1))
            list += Slip(it.getString(0), it.getString(1), user?.name ?: "", it.getString(2), if (it.isNull(3)) null else it.getString(3), it.getInt(4), it.getString(5), if (it.isNull(6)) null else it.getString(6), if (it.isNull(7)) null else it.getString(7))
        }
        list
    }

    fun reviewSlip(id: String, status: String) {
        val slip = slips().find { it.id == id } ?: error("پرداخت پیدا نشد")
        if (slip.status != "pending") error("این پرداخت بررسی شده")
        writableDatabase.update("slips", ContentValues().apply { put("status", status) }, "id=?", arrayOf(id))
        if (status == "approved") topUp(slip.userId, slip.amount)
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
        val price = if (plan == "advanced") 1_000_000 else 500_000
        if (user.balance < price) error("موجودی کیف پول کافی نیست")
        val expires = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000
        writableDatabase.execSQL(
            "UPDATE users SET balance = balance - ?, plan=?, plan_expires_at=? WHERE id=?",
            arrayOf<Any>(price.toLong(), plan, expires, user.id),
        )
        addNotice(user.id, "پلن فعال شد", "پلن برای ۳۰ روز فعال است.")
        return if (plan == "advanced") "پیشرفته" else "پایه"
    }

    fun users(): List<User> = readableDatabase.rawQuery("SELECT id FROM users ORDER BY created_at DESC", null).use {
        val list = mutableListOf<User>()
        while (it.moveToNext()) user(it.getString(0))?.let(list::add)
        list
    }

    fun setSuspended(id: String, suspended: Boolean) {
        writableDatabase.update("users", ContentValues().apply { put("suspended", if (suspended) 1 else 0) }, "id=?", arrayOf(id))
    }

    data class AiModel(val id: String, val name: String, val endpoint: String, val modelId: String, val apiKey: String, val inputCost: Double, val outputCost: Double)

    fun models(): List<AiModel> = readableDatabase.rawQuery("SELECT id, name, endpoint, model_id, api_key, input_cost, output_cost FROM ai_models ORDER BY created_at DESC", null).use {
        val list = mutableListOf<AiModel>()
        while (it.moveToNext()) list += AiModel(it.getString(0), it.getString(1), it.getString(2), it.getString(3), it.getString(4), it.getDouble(5), it.getDouble(6))
        list
    }

    fun addModel(name: String, endpoint: String, modelId: String, apiKey: String, inputCost: Double, outputCost: Double) {
        if (name.isBlank() || modelId.isBlank()) error("نام و شناسه مدل لازم است")
        writableDatabase.insert("ai_models", null, ContentValues().apply {
            put("id", UUID.randomUUID().toString())
            put("name", name.trim())
            put("endpoint", endpoint.trim().ifBlank { "https://api.openai.com/v1" })
            put("model_id", modelId.trim())
            put("api_key", apiKey.trim())
            put("input_cost", inputCost)
            put("output_cost", outputCost)
            put("created_at", System.currentTimeMillis())
        })
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
