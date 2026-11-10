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

        val aiModels = mutableListOf<SyncAiModelItem>()
        val aiModelsOutboxIds = mutableListOf<String>()

        val supportMessages = mutableListOf<SyncSupportMessageItem>()
        val supportOutboxIds = mutableListOf<String>()

        val banners = mutableListOf<SyncBannerItem>()
        val bannersOutboxIds = mutableListOf<String>()

        val announcements = mutableListOf<SyncAnnouncementItem>()
        val announcementsOutboxIds = mutableListOf<String>()

        val exportLogs = mutableListOf<SyncExportLogItem>()
        val exportLogsOutboxIds = mutableListOf<String>()

        val people = mutableListOf<SyncPersonItem>()
        val peopleOutboxIds = mutableListOf<String>()

        val slips = mutableListOf<SyncSlipItem>()
        val slipsOutboxIds = mutableListOf<String>()

        val tokenUsages = mutableListOf<TokenUsageSyncItem>()
        val tokenOutboxIds = mutableListOf<String>()

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
                                userId = json.optString("user_id").takeIf { it.isNotEmpty() },
                                body = json.getString("body"),
                                kind = json.optString("kind", "text"),
                                channel = json.optString("channel", "manual"),
                                occurredAt = json.optLong("occurred_at", System.currentTimeMillis()),
                                voicePath = json.optString("voice_path").takeIf { it.isNotEmpty() },
                                mediaPath = json.optString("media_path").takeIf { it.isNotEmpty() },
                                memberId = json.optString("member_id").takeIf { it.isNotEmpty() },
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

                    "ai_model" -> {
                        aiModels.add(
                            SyncAiModelItem(
                                id = json.getString("id"),
                                name = json.getString("name"),
                                endpoint = json.getString("endpoint"),
                                modelId = json.getString("model_id"),
                                apiKey = json.getString("api_key"),
                                inputCost = json.getDouble("input_cost").toFloat(),
                                outputCost = json.getDouble("output_cost").toFloat(),
                                active = json.getBoolean("active")
                            )
                        )
                        aiModelsOutboxIds.add(outboxId)
                    }

                    "support_message" -> {
                        supportMessages.add(
                            SyncSupportMessageItem(
                                id = json.getString("id"),
                                userId = json.getString("user_id"),
                                fromAdmin = json.getBoolean("from_admin"),
                                body = json.getString("body"),
                                kind = json.getString("kind"),
                                path = json.optString("path").takeIf { it.isNotEmpty() },
                                readAdmin = json.optBoolean("read_admin", false),
                                readUser = json.optBoolean("read_user", false),
                                createdAt = json.optLong("created_at", System.currentTimeMillis())
                            )
                        )
                        supportOutboxIds.add(outboxId)
                    }

                    "banner" -> {
                        banners.add(
                            SyncBannerItem(
                                id = json.getString("id"),
                                title = json.getString("title"),
                                body = json.getString("body"),
                                image = json.optString("image").takeIf { it.isNotEmpty() },
                                active = json.getBoolean("active"),
                                createdAt = json.optLong("created_at", System.currentTimeMillis())
                            )
                        )
                        bannersOutboxIds.add(outboxId)
                    }

                    "announcement" -> {
                        announcements.add(
                            SyncAnnouncementItem(
                                id = json.getString("id"),
                                title = json.getString("title"),
                                body = json.getString("body"),
                                createdAt = json.optLong("created_at", System.currentTimeMillis())
                            )
                        )
                        announcementsOutboxIds.add(outboxId)
                    }

                    "export_log" -> {
                        exportLogs.add(
                            SyncExportLogItem(
                                id = json.getString("id"),
                                userId = json.getString("user_id"),
                                timestamp = json.getLong("timestamp"),
                                fileType = json.getString("file_type"),
                                targetName = json.getString("target_name"),
                                recordsCount = json.getInt("records_count")
                            )
                        )
                        exportLogsOutboxIds.add(outboxId)
                    }

                    "person" -> {
                        people.add(
                            SyncPersonItem(
                                id = json.getString("id"),
                                userId = json.optString("user_id").takeIf { it.isNotEmpty() },
                                name = json.getString("name"),
                                phone = json.optString("phone").takeIf { it.isNotEmpty() },
                                role = json.optString("role").takeIf { it.isNotEmpty() },
                                pinned = json.optBoolean("pinned", false),
                                avatar = json.optString("avatar").takeIf { it.isNotEmpty() },
                                isGroup = json.optBoolean("is_group", false),
                                saved = json.optBoolean("saved", false),
                                createdAt = json.optLong("created_at", System.currentTimeMillis())
                            )
                        )
                        peopleOutboxIds.add(outboxId)
                    }

                    "slip" -> {
                        slips.add(
                            SyncSlipItem(
                                id = json.getString("id"),
                                kind = json.getString("kind"),
                                plan = json.getString("plan"),
                                amount = json.getInt("amount"),
                                receipt = json.optString("receipt").takeIf { it.isNotEmpty() },
                                refCode = json.optString("ref_code").takeIf { it.isNotEmpty() },
                                createdAt = json.optLong("created_at", System.currentTimeMillis())
                            )
                        )
                        slipsOutboxIds.add(outboxId)
                    }

                    "token_usage" -> {
                        tokenUsages.add(
                            TokenUsageSyncItem(
                                userId = json.getString("user_id"),
                                tokens = json.getInt("tokens"),
                                occurredAt = System.currentTimeMillis()
                            )
                        )
                        tokenOutboxIds.add(outboxId)
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

        // ۳. ارسال دسته‌ای داده‌ها

        // پیام‌های معمولی
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

        // پیام‌های هوش مصنوعی
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

        // مدل‌های هوش مصنوعی
        if (aiModels.isNotEmpty()) {
            try {
                val res = api.syncAiModels(SyncAiModelsRequest(aiModels = aiModels))
                if (res.isSuccessful && res.body()?.status == "success") {
                    for (id in aiModelsOutboxIds) {
                        db.writableDatabase.delete("outbox", "id = ?", arrayOf(id))
                    }
                } else {
                    hasFailures = true
                }
            } catch (e: Exception) {
                hasFailures = true
            }
        }

        // پیام‌های پشتیبانی
        if (supportMessages.isNotEmpty()) {
            try {
                val res = api.syncSupportMessages(SyncSupportMessagesRequest(supportMessages = supportMessages))
                if (res.isSuccessful && res.body()?.status == "success") {
                    for (id in supportOutboxIds) {
                        db.writableDatabase.delete("outbox", "id = ?", arrayOf(id))
                    }
                } else {
                    hasFailures = true
                }
            } catch (e: Exception) {
                hasFailures = true
            }
        }

        // بنرها
        if (banners.isNotEmpty()) {
            try {
                val res = api.syncBanners(SyncBannersRequest(banners = banners))
                if (res.isSuccessful && res.body()?.status == "success") {
                    for (id in bannersOutboxIds) {
                        db.writableDatabase.delete("outbox", "id = ?", arrayOf(id))
                    }
                } else {
                    hasFailures = true
                }
            } catch (e: Exception) {
                hasFailures = true
            }
        }

        // اطلاعیه‌ها
        if (announcements.isNotEmpty()) {
            try {
                val res = api.syncAnnouncements(SyncAnnouncementsRequest(announcements = announcements))
                if (res.isSuccessful && res.body()?.status == "success") {
                    for (id in announcementsOutboxIds) {
                        db.writableDatabase.delete("outbox", "id = ?", arrayOf(id))
                    }
                } else {
                    hasFailures = true
                }
            } catch (e: Exception) {
                hasFailures = true
            }
        }

        // لاگ‌های خروجی
        if (exportLogs.isNotEmpty()) {
            try {
                val res = api.syncExportLogs(SyncExportLogsRequest(exportLogs = exportLogs))
                if (res.isSuccessful && res.body()?.status == "success") {
                    for (id in exportLogsOutboxIds) {
                        db.writableDatabase.delete("outbox", "id = ?", arrayOf(id))
                    }
                } else {
                    hasFailures = true
                }
            } catch (e: Exception) {
                hasFailures = true
            }
        }

        // مخاطبین
        if (people.isNotEmpty()) {
            try {
                val res = api.syncPeople(SyncPeopleRequest(people = people))
                if (res.isSuccessful && res.body()?.status == "success") {
                    for (id in peopleOutboxIds) {
                        db.writableDatabase.delete("outbox", "id = ?", arrayOf(id))
                    }
                } else {
                    hasFailures = true
                }
            } catch (e: Exception) {
                hasFailures = true
            }
        }

        // فیش‌های پرداخت
        if (slips.isNotEmpty()) {
            try {
                val res = api.syncSlips(SyncSlipRequest(slips = slips))
                if (res.isSuccessful && res.body()?.status == "success") {
                    for (id in slipsOutboxIds) {
                        db.writableDatabase.delete("outbox", "id = ?", arrayOf(id))
                    }
                } else {
                    hasFailures = true
                }
            } catch (e: Exception) {
                hasFailures = true
            }
        }

        // توکن‌های مصرفی
        if (tokenUsages.isNotEmpty()) {
            try {
                val res = api.syncTokens(TokenUsageSyncPayload(usages = tokenUsages))
                if (res.isSuccessful && res.body()?.status == "success") {
                    for (id in tokenOutboxIds) {
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