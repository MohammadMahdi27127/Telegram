package ir.daftar.goftogu

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val app = applicationContext as? App ?: return@withContext Result.failure()
        val db = app.db

        // ۱. خواندن ۳۰ آیتم اول صف outbox به همراه شناسه، نوع و پی‌لود
        val cursor = db.readableDatabase.rawQuery("SELECT id, kind, payload FROM outbox LIMIT 30", null)
        val rawItems = mutableListOf<Triple<String, String, String>>() // id, kind, payload
        cursor.use {
            while (it.moveToNext()) {
                rawItems.add(Triple(it.getString(0), it.getString(1), it.getString(2)))
            }
        }

        if (rawItems.isEmpty()) {
            return@withContext Result.success()
        }

        val entries = mutableListOf<SyncEntryItem>()
        val entryOutboxIds = mutableListOf<String>()

        val aiMessages = mutableListOf<SyncAiMessageItem>()
        val aiOutboxIds = mutableListOf<String>()

        // ۲. تفکیک و پارس داده‌ها بر اساس kind
        for (item in rawItems) {
            val outboxId = item.first
            val kind = item.second
            val payloadStr = item.third

            try {
                val json = JSONObject(payloadStr)
                when (kind) {
                    "entry" -> {
                        entries.add(
                            SyncEntryItem(
                                id = json.getString("id"),
                                personId = json.getString("person_id"),
                                body = json.getString("body"),
                                kind = json.optString("kind", "text"),
                                channel = json.optString("channel", "manual"),
                                occurredAt = json.optLong("occurred_at", System.currentTimeMillis()),
                                voicePath = json.optString("voice_path").takeIf { it.isNotEmpty() },
                                mediaPath = json.optString("media_path").takeIf { it.isNotEmpty() },
                                targetRecipientId = json.optString("target_recipient_id").takeIf { it.isNotEmpty() }
                            )
                        )
                        entryOutboxIds.add(outboxId)
                    }

                    "ai_message" -> {
                        aiMessages.add(
                            SyncAiMessageItem(
                                id = json.getString("id"),
                                userId = json.optString("user_id").takeIf { it.isNotEmpty() },
                                personId = json.optString("person_id").takeIf { it.isNotEmpty() },
                                role = json.getString("role"),
                                body = json.getString("body"),
                                imagePath = json.optString("image_path").takeIf { it.isNotEmpty() },
                                createdAt = json.optLong("created_at", System.currentTimeMillis())
                            )
                        )
                        aiOutboxIds.add(outboxId)
                    }

                    else -> {
                        // اگر رکورد نامشخصی در صف بود حذف می‌شود تا صف مسدود نشود
                        db.writableDatabase.delete("outbox", "id = ?", arrayOf(outboxId))
                    }
                }
            } catch (e: Exception) {
                // حذف داده‌های با فرمت اشتباه
                db.writableDatabase.delete("outbox", "id = ?", arrayOf(outboxId))
            }
        }

        val api = NetworkModule.getApiService()
        var hasFailures = false

        // ۳. ارسال دسته‌ای پیام‌های معمولی
        if (entries.isNotEmpty()) {
            try {
                val res = api.syncEntries(SyncPayloadRequest(entries = entries))
                if (res.isSuccessful && res.body()?.status == "success") {
                    for (id in entryOutboxIds) {
                        db.writableDatabase.delete("outbox", "id = ?", arrayOf(id))
                    }
                } else {
                    hasFailures = true
                }
            } catch (e: Exception) {
                hasFailures = true
            }
        }

        // ۴. ارسال دسته‌ای پیام‌های هوش مصنوعی
        if (aiMessages.isNotEmpty()) {
            try {
                val res = api.syncAiMessages(SyncAiMessagesRequest(messages = aiMessages))
                if (res.isSuccessful && res.body()?.status == "success") {
                    for (id in aiOutboxIds) {
                        db.writableDatabase.delete("outbox", "id = ?", arrayOf(id))
                    }
                } else {
                    hasFailures = true
                }
            } catch (e: Exception) {
                hasFailures = true
            }
        }

        if (hasFailures) {
            Result.retry()
        } else {
            Result.success()
        }
    }
}