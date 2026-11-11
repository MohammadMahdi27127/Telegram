# گزارش ممیزی مجدد معماری (Re-Audit) — پروژه «دفتر گفتگو / Recamender»

> **دامنه:** بک‌اند FastAPI/SQLAlchemy/PostgreSQL + اپ اندروید Kotlin/Compose/SQLite
> **مبنای مقایسه (Baseline):** سند [ARCHITECTURE_AUDIT.md](file:///c:/Users/moham/Desktop/Myproject/AP/ARCHITECTURE_AUDIT.md) — `HEAD = f2b98dc`
> **تاریخ ممیزی مجدد:** 2026-11-11
> **موضوع:** ارزیابی مجدد ۲۰ یافتهٔ ممیزی قبلی پس از اعمال ۶ پارت بازمهندسی «معماری Event-Driven / PTS»
> **نوع سند:** تحلیل فقط-خواندنی — هیچ تغییری در کد اپ یا بک‌اند اعمال نشده است.
> **نکتهٔ مهم:** تمام شماره خطوط این سند بر اساس **وضعیت فعلی درخت کاری** (پس از ۶ پارت) است، نه اسنپ‌شات `f2b98dc`.

---

## بخش ۱ — جدول وضعیت ۲۰ یافتهٔ قبلی

| # | عنوان باگ / گلوگاه | وضعیت فعلی | فایل و شماره خطوط |
|---|---|---|---|
| ۱ | هاردکد `user_id=None` در ثبت entries سرور | **برطرف‌شده** — مالکیت از payload و در نبود آن از `Person.user_id` استخراج می‌شود؛ کلاینت نیز `user_id` را در payload می‌فرستد | [sync.py:251-255](file:///c:/Users/moham/Desktop/Myproject/AP/backend/sync.py#L251-L255)، DTO [sync.py:25](file:///c:/Users/moham/Desktop/Myproject/AP/backend/sync.py#L25)، کلاینت [Db.kt:1190](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L1190)، [SyncWorker.kt:76](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/SyncWorker.kt#L76) |
| ۲ | نبود هر GET برای `entries`/`people`/`support_messages`/`ai_messages` | **برطرف‌شده (با معماری متفاوت)** — کانال Pull از طریق `POST /api/v1/sync/difference` ایجاد شد (اسنپ‌شات + دلتای PTS). **باقی‌مانده:** `ai_messages` تنها موجودیتی است که در پاسخ دلتا هیچ سهمی ندارد | [sync.py:136-238](file:///c:/Users/moham/Desktop/Myproject/AP/backend/sync.py#L136-L238) |
| ۳ | `POST /sync/people` فیلد `user_id` را نمی‌گیرد/ذخیره نمی‌کند | **برطرف‌شده** — فیلد در DTO اضافه و در هر دو مسیر (ایجاد/به‌روزرسانی) ذخیره می‌شود | [sync.py:41](file:///c:/Users/moham/Desktop/Myproject/AP/backend/sync.py#L41)، [sync.py:328](file:///c:/Users/moham/Desktop/Myproject/AP/backend/sync.py#L328)، [sync.py:332](file:///c:/Users/moham/Desktop/Myproject/AP/backend/sync.py#L332) |
| ۴ | payload نامعتبر در صف آفلاین (متن خام) و حذف بی‌صدای آن | **نیمه‌برطرف** — تولیدکننده کاملاً پاک شد (هیچ `enqueue(` با متن خام در `Screens.kt` باقی نمانده)، اما **مصرف‌کننده هنوز رکورد نامعتبر را بی‌صدا DELETE می‌کند** (بدون retry و بدون لاگ) | `Screens.kt` (grep `enqueue(` ⇒ صفر نتیجه)، [SyncWorker.kt:222-230](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/SyncWorker.kt#L222-L230) |
| ۵ | جدول `team_messages` در SQLite هرگز ساخته نمی‌شد | **برطرف‌شده** — در هر سه مسیر `onCreate` / `onUpgrade` / `onOpen` ساخته می‌شود | [Db.kt:194-195](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L194-L195)، [Db.kt:264-265](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L264-L265)، [Db.kt:281-284](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L281-L284) |
| ۶ | `getTeamMessages` هیچ فراخوان‌کننده‌ای ندارد | **دست‌نخورده و تشدیدشده** — هدف «خواندن پیام تیم از سرور» در عمل حذف نشد و هم‌زمان جدول لوکال `team_messages` نیز خواننده‌ای در UI ندارد ⇒ پیام سایر اعضا همچنان نمایش داده نمی‌شود | [ApiService.kt:108](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/ApiService.kt#L108)، [Db.kt:1952](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L1952) |
| ۷ | `askAboutChat` رکورد مخاطب را الزامی می‌دانست | **برطرف‌شده** — برای `AI_GENERAL_ASSISTANT_ID` یک `Person` مجازی ساخته می‌شود | [Screens.kt:5884-5901](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Screens.kt#L5884-L5901) |
| ۸ | نبود ایندکس روی `entries.user_id/person_id` و `people.user_id` (PostgreSQL) | **برطرف‌شده** — هم `index=True` در مدل‌ها و هم `CREATE INDEX IF NOT EXISTS` صریح در مایگریشن | مدل [main.py:83](file:///c:/Users/moham/Desktop/Myproject/AP/backend/main.py#L83)، [main.py:96-97](file:///c:/Users/moham/Desktop/Myproject/AP/backend/main.py#L96-L97)؛ DDL [main.py:293-298](file:///c:/Users/moham/Desktop/Myproject/AP/backend/main.py#L293-L298) |
| ۹ | نبود هر ایندکس در SQLite | **جزئی** — تنها `idx_team_messages_team` اضافه شد؛ ایندکس‌های `people(user_id)`، `entries(person_id)`، `entries(user_id)`، `support_messages(user_id)`، `ai_messages(user_id,person_id)` هنوز وجود ندارند | [Db.kt:195](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L195) |
| ۱۰ | `ORDER BY created_at ASC LIMIT 50` در پیام‌های تیم | **دست‌نخورده** — همچنان قدیمی‌ترین پیام‌ها برمی‌گردد، نه آخرین پیام‌ها (بر خلاف داک‌استرینگ) | [teams.py:483](file:///c:/Users/moham/Desktop/Myproject/AP/backend/teams.py#L483) |
| ۱۱ | N+1 در `people()` (۳ کوئری per مخاطب) و در `get_user_teams` (۱ کوئری per تیم) | **دست‌نخورده** | [Db.kt:882-920](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L882-L920)، [teams.py:227-229](file:///c:/Users/moham/Desktop/Myproject/AP/backend/teams.py#L227-L229) |
| ۱۲ | نبود `beginTransaction` در upsert (حالت نیمه‌نوشته) | **دست‌نخورده** — `upsertGroupTeam` همچنان هر عضو را با یک statement جدا می‌نویسد (اگرچه متد جدید `applyDifferenceLocally` **دارای تراکنش اتمیک است**) | [Db.kt:1711-1751](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L1711-L1751) در برابر تراکنش اتمیک [Db.kt:328-406](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L328-L406) |
| ۱۳ | نبود `ForeignKey` در کل PostgreSQL | **دست‌نخورده** — grep روی `backend/` ⇒ صفر مورد | کل `backend/` |
| ۱۴ | روت تکراری `/api/v1/users/check` در `main.py` و `teams.py` | **دست‌نخورده** — هر دو تعریف باقی است و نسخهٔ `main.py` ثبت می‌شود | [main.py:440](file:///c:/Users/moham/Desktop/Myproject/AP/backend/main.py#L440)، [teams.py:77](file:///c:/Users/moham/Desktop/Myproject/AP/backend/teams.py#L77) |
| ۱۵ | کد مردهٔ مدل موازی تیم در `Db.kt` | **دست‌نخورده** — هیچ فراخوان‌کننده‌ای برای این متدها یافت نشد | `addTeam` [Db.kt:1669](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L1669)، `addTeamMember` [Db.kt:1873](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L1873)، `addTeamMessage` [Db.kt:1937](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L1937)، `getTeamMessages` [Db.kt:1952](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L1952)، `clearTeamMessages` [Db.kt:1982](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L1982) |
| ۱۶ | `api_key` متن‌رو در سینک `ai_model` + مسیر مردهٔ `token_usage` | **دست‌نخورده** — `ai_model` با `api_key` خام enqueue و روی سرور ذخیره می‌شود؛ `incrementUserTokens` همچنان هیچ فراخوان‌کننده‌ای ندارد | [Db.kt:1535-1560](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L1535-L1560)، [Db.kt:2003-2018](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L2003-L2018) |
| ۱۷ | نبود واکنش‌پذیری دیتابیس → UI | **جزئی** — دو صفحهٔ `ChatsScreen` و `TeamsScreen` اکنون پس از سینک PTS لیست محلی را بازخوانی می‌کنند؛ اما `ThreadScreen` و `SupportChatRoom` همچنان دستی‌اند و نوشتن‌های `SyncWorker` هنوز به state نمی‌رسد | `ChatsScreen` [Screens.kt:2916-2921](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Screens.kt#L2916-L2921)، `TeamsScreen` [Screens.kt:746-750](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Screens.kt#L746-L750)، `ThreadScreen` [Screens.kt:4773](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Screens.kt#L4773) |
| ۱۸ | `updated_at`/`member_id`/`voice_path`/`media_path`/`target_recipient_id` در payload سینک نیامده‌اند | **جزئی** — مسیر Pull این فیلدها را برمی‌گرداند و `SyncWorker` نیز آن‌ها را از payload می‌خواند، اما **`addEntry` هنوز آن‌ها را در payload نمی‌نویسد** ⇒ رسانه‌ها در مسیر Push روی سرور گم می‌شوند | [Db.kt:1188-1196](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L1188-L1196) در برابر پشتیبانی DTO [SyncWorker.kt:82-84](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/SyncWorker.kt#L82-L84) و بازگشت در [sync.py:195](file:///c:/Users/moham/Desktop/Myproject/AP/backend/sync.py#L195) |
| ۱۹ | `init_db` با یک try/except برای چند DDL | **برطرف‌شده** — هر دستور در تراکنش مستقل با `logger.warning` اجرا می‌شود؛ بلوک رویدادهای PTS هم مستقل شد | [main.py:301-307](file:///c:/Users/moham/Desktop/Myproject/AP/backend/main.py#L301-L307)، [main.py:325-331](file:///c:/Users/moham/Desktop/Myproject/AP/backend/main.py#L325-L331) |
| ۲۰ | فیلتر گروه در `ThreadScreen` روی جدول `entries` | **دست‌نخورده و تبدیل به شکاف معماری** — لایهٔ PTS پیام‌های تیمی را در جدول `team_messages` درج می‌کند، اما `ThreadScreen` همچنان از `entries` می‌خواند ⇒ دادهٔ Pull‌شده بی‌اثر است | [Screens.kt:4773-4776](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Screens.kt#L4773-L4776)، درج PTS در [Db.kt:385-395](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L385-L395) |

### آمار خلاصه

| وضعیت | تعداد | یافته‌ها |
|---|---|---|
| برطرف‌شده | ۶ | #۱، #۲، #۳، #۵، #۷، #۸، #۱۹ (۷ مورد با احتساب #۲ به‌عنوان معماری جایگزین) |
| جزئی | ۴ | #۴، #۹، #۱۷، #۱۸ |
| دست‌نخورده | ۹ | #۶، #۱۰، #۱۱، #۱۲، #۱۳، #۱۴، #۱۵، #۱۶، #۲۰ |

> **جمع‌بندی عددی:** از ۲۰ یافته، ۷ مورد برطرف‌شده، ۴ مورد جزئی و ۹ مورد دست‌نخورده است.

### پیشرفت‌های تأییدشدهٔ معماری PTS (فراتر از رفع ۲۰ یافته)

- مدل `UserSyncEvent` با `id` به‌عنوان شمارهٔ ترتیبی PTS + ایندکس ترکیبی `(user_id, id)`: [main.py:245-254](file:///c:/Users/moham/Desktop/Myproject/AP/backend/main.py#L245-L254)، [main.py:310-331](file:///c:/Users/moham/Desktop/Myproject/AP/backend/main.py#L310-L331)
- تابع `record_sync_event` با گارد `None`/خالی: [sync.py:96-118](file:///c:/Users/moham/Desktop/Myproject/AP/backend/sync.py#L96-L118) — فقط یک تعریف (بدون تکرار).
- چهار هوک رویداد فعال: `entry` [sync.py:285-302](file:///c:/Users/moham/Desktop/Myproject/AP/backend/sync.py#L285-L302)، `dialog` [sync.py:343-361](file:///c:/Users/moham/Desktop/Myproject/AP/backend/sync.py#L343-L361)، `support` [sync.py:558-574](file:///c:/Users/moham/Desktop/Myproject/AP/backend/sync.py#L558-L574)، `team_message` [teams.py:536-559](file:///c:/Users/moham/Desktop/Myproject/AP/backend/teams.py#L536-L559)
- اندپوینت تجمیعی `POST /api/v1/sync/difference` با دو سناریو: `pts <= 0` ⇒ اسنپ‌شات کامل ([sync.py:161-199](file:///c:/Users/moham/Desktop/Myproject/AP/backend/sync.py#L161-L199)) و `pts > 0` ⇒ دلتای رویدادمحور با صفحه‌بندی `limit+1` ([sync.py:202-238](file:///c:/Users/moham/Desktop/Myproject/AP/backend/sync.py#L202-L238))
- کلاینت: `applyDifferenceLocally` با استراتژی Merge در تراکنش اتمیک ([Db.kt:289-313](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L289-L313) و [Db.kt:328-406](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L328-L406))، `fetchUpdatesDifferenceAsync` با حلقهٔ `has_more` ([App.kt:113-173](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/App.kt#L113-L173))، مدیریت PTS در SharedPreferences ([App.kt:88-111](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/App.kt#L88-L111))، و اتصال خودکار `ai_config` ([Db.kt:1585](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L1585))

---

## بخش ۲ — یافته‌های جدید (کشف‌شده در این دور ممیزی)

| شناسه | عنوان | شرح | ارجاع |
|---|---|---|---|
| **N1** | سقف اسنپ‌شات پیام تیم همراه با `has_more=False` | در سناریوی اسنپ‌شات، پیام‌های تیم با `order_by(created_at.asc()).limit(100)` خوانده می‌شوند و پاسخ `has_more=False` برمی‌گرداند. یعنی برای تیمی با بیش از ۱۰۰ پیام، **قدیمی‌ترین ۱۰۰ پیام** برگردانده می‌شود و چون صفحه‌بندی اعلام نمی‌شود، کلاینت هرگز پیام‌های جدیدتر را دریافت نمی‌کند (تضاد با مفروضات مسیر Pull) | [sync.py:180-183](file:///c:/Users/moham/Desktop/Myproject/AP/backend/sync.py#L180-L183)، [sync.py:193](file:///c:/Users/moham/Desktop/Myproject/AP/backend/sync.py#L193) |
| **N2** | ارسال پیام تیمی بدون outbox / retry / کشف خطا | `dispatchTeamMessage` یک درخواست fire-and-forget روی `Dispatchers.IO` است که در `runCatching` پیچیده شده و نتیجهٔ آن **هیچ‌گاه بررسی یا گزارش نمی‌شود**. پیام لوکال (`entries`) ذخیره می‌شود ولی اگر شبکه قطع باشد: روی سرور ثبت نمی‌شود، پس هوک `team_message` هم اجرا نمی‌شود و **هیچ رویداد PTS صادر نمی‌گردد** ⇒ پیام برای همیشه فقط روی همان دستگاه می‌ماند | [Screens.kt:4867-4880](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Screens.kt#L4867-L4880) |
| **N3** | حلقهٔ بی‌مصرف `team_messages` (نوشتن بدون خواندن) | `applyDifferenceLocally` پیام‌های تیمی دریافتی از دلتا را با تراکنش اتمیک در جدول `team_messages` درج می‌کند، اما هیچ کامپوزیبلی این جدول را نمی‌خواند (`ThreadScreen` از `entries` می‌خواند). نتیجه: هزینهٔ سینک پرداخت می‌شود ولی خروجی در UI صفر است — به‌هم‌پیوسته با یافته‌های #۶ و #۲۰ | درج [Db.kt:385-395](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L385-L395)؛ غیبت خواننده در [Screens.kt:4773-4776](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Screens.kt#L4773-L4776) |
| **N4** (کیفی) | شکست خاموش در ثبت رویدادهای تیمی و بی‌اثری `rollback` | پیام تیم در [teams.py:534](file:///c:/Users/moham/Desktop/Myproject/AP/backend/teams.py#L534) commit می‌شود و سپس حلقهٔ `record_sync_event` برای اعضا اجرا می‌شود. در صورت خطا فقط `logger.warning` ثبت و `db.rollback()` اجرا می‌شود. چون commit قبلی انجام شده، rollback صرفاً رویدادهای ثبت‌نشدهٔ PTS را دور می‌ریزد و **هیچ مکانیزم بازیابی/retryوجود ندارد** ⇒ اعضایی که رویدادشان ثبت نشده، آن پیام را هرگز از مسیر دلتا نمی‌بینند | [teams.py:536-559](file:///c:/Users/moham/Desktop/Myproject/AP/backend/teams.py#L536-L559) |

---

## بخش ۳ — بررسی یکپارچگی جریان داده (End-to-End Trace)

### مسیر A — پیام دونفره (کامل و سالم) ✅

```
[UI] ChatInputBar
  └─► Db.addEntry(user, personId, body, kind, channel, occurredAt, ...)          Db.kt:1173
        ├─ اعتبارسنجی سقف فایل رایگان (۲۰) و سقف پیام (quota)                    Db.kt:1174-1183
        ├─ ساخت payloadJson = {id, user_id, person_id, body, kind, channel,
        │                      occurred_at}                                      Db.kt:1188-1196
        ├─ enqueue("entry", payloadJson)                                          Db.kt:1198
        │     └─ INSERT INTO outbox + WorkManager.enqueue(OneTimeWork<SyncWorker>)
        └─ INSERT INTO entries (لوکال، منبع حقیقت)                                 Db.kt:1200-1215
              │
              ▼
[Outbox] جدول outbox (id, kind, payload)
              │  WorkManager (OneTime فوری + Periodic ۱۵ دقیقه‌ای)
              ▼
[SyncWorker.doWork()]  kind == "entry"                                        SyncWorker.kt:72-88
        ├─ JSONObject(payloadStr) ⇒ SyncEntryItem(id, userId, personId, body, kind,
        │                                        channel, occurredAt, voicePath,
        │                                        mediaPath, targetRecipientId)
        ├─ POST /api/v1/sync/entries  ⇒ SyncPayloadRequest
        └─ در صورت status=="success" ⇒ DELETE FROM outbox، وگرنه hasFailures=true
              │
              ▼
[Backend] POST /api/v1/sync/entries  →  sync_entries()                        sync.py:242
        ├─ owner_id = item.user_id  (fallback: Person.user_id)                    sync.py:251-255
        ├─ upsert بر اساس Entry.id (+ updated_at = now)                           sync.py:256-283
        ├─ record_sync_event(db, owner_id, "entry", item.id, {...})               sync.py:285-302
        └─ db.commit()   ⇒ رویداد با id جدید در user_sync_events (PTS نهفته)       sync.py:304
              │
              ▼  (دستگاه مقصد)
[Client] App.fetchUpdatesDifferenceAsync(userId)                                App.kt:113-173
        ├─ isLocalDataEmpty() ? pts = 0 : pts = getLocalPts(userId)               App.kt:127-128
        ├─ POST /sync/difference {user_id, pts, limit:200}                        App.kt:135-137
        ├─ db.applyDifferenceLocally(...)  ← تراکنش اتمیک + استراتژی Merge         Db.kt:328-406
        ├─ diff.aiConfig?.let { db.ensureServerAiModel(...) }                     App.kt:150-153
        ├─ saveLocalPts(userId, diff.newPts) + markSyncCompleted()                App.kt:155-160
        └─ withContext(Dispatchers.Main) { onComplete(true) }                     App.kt:164
              │
              ▼
[UI] TeamsScreen / ChatsScreen reload после onComplete                        Screens.kt:746-750 / 2916-2921
```

**نتیجه: مسیر A کامل است** (Push → رویداد PTS → Pull → درج Merge → بازخوانی UI).

### مسیر B — پیام تیمی از یک عضو دیگر (نیمه‌کاره) ⚠️

```
[Device S] POST /api/v1/teams/{team_id}/messages                                teams.py:498
   └─ INSERT INTO team_messages + db.commit()                                teams.py:533-534
   └─ حلقه روی اعضای تیم: record_sync_event(uid, "team_message", ...)        teams.py:536-559
         └─ رویداد PTS به ازای هر عضو
              │
              ▼  (Device R)
[Client R] fetchUpdatesDifferenceAsync ⇒ delta ⇒ applyDifferenceLocally
   └─ mergeRow(db, "team_messages", ...)  ⇒ درج موفق در SQLite                 Db.kt:385-395
              │
              ▼
         ✗ توقف — هیچ کامپوزیبلی جدول team_messages را نمی‌خواند              Screens.kt:4773-4776
           (ThreadScreen گروه همچنان از entries و مسیر قدیمی نمایش می‌دهد)
```

**نتیجه: مسیر B نیمه‌کاره است** — داده تا SQLite می‌رسد ولی هرگز به UI نمی‌رسد (یافته‌های #۲۰ + N3).

### مسیر C — پیام تیمی آفلاین از سمت فرستنده (شکست بی‌صدا) ❌

```
[Device S] ThreadScreen ⇒ dispatchTeamMessage(content)                          Screens.kt:4867
   └─ scope.launch { runCatching { withContext(IO) { api.sendTeamMessage(...) } } }  Screens.kt:4870-4879
         ├─ نقش runCatching: استثنا بلعیده می‌شود، هیچ لاگ/نمایش خطایی وجود ندارد
         ├─ هیچ INSERT در outbox انجام نمی‌شود ⇒ بدون retry
         └─ چون POST به سرور نرسیده، هوک record_sync_event اجرا نمی‌شود
              ⇒ هیچ رویداد PTS صادر نمی‌شود (یافتهٔ N2)
```

**نتیجه: مسیر C شکست بی‌صدا است** — تنها یک INSERT محلی در `entries` باقی می‌ماند.

### تفکیک ۲۰ یافته در قالب جریان

| گام | یافته‌های مرتبط | وضعیت گام |
|---|---|---|
| نوشتن لوکال | #۱۸ (نبود media/member در payload) | جزئی |
| صف آفلاین (outbox) | #۴ (حذف بی‌صدای رکورد نامعتبر)، N2 (نبود صف برای پیام تیم) | جزئی |
| ارسال به سرور (Push) | #۱، #۳ (اصلاح مالکیت) | ✅ برطرف |
| صدور رویداد PTS | #۱۹ (init_db)، N4 (شکست خاموش هوک تیمی) | ✅ / ⚠️ |
| دریافت (Pull) | #۲ (کانال جدید)، N1 (سقف ۱۰۰ پیام) | جزئی |
| درج لوکال | #۱۲ (تراکنش)، #۱۸ | ✅ در PTS / ❌ در upsertGroupTeam |
| بازخوانی UI | #۶، #۱۷، #۲۰، N3 | جزئی |

---

## بخش ۴ — خطاهای باقی‌مانده و ریسک‌های زمان کامپایل

### ۴.۱ بک‌اند (Python / FastAPI)

| بررسی | نتیجه |
|---|---|
| سینتکس و importها | ✅ `python -m py_compile backend/main.py backend/sync.py backend/teams.py` بدون خطا |
| `text()` برای DDL | ✅ در `main.py` import شده و در `init_db` استفاده می‌شود — [main.py:303](file:///c:/Users/moham/Desktop/Myproject/AP/backend/main.py#L303)، [main.py:328](file:///c:/Users/moham/Desktop/Myproject/AP/backend/main.py#L328) |
| `or_` در `sync.py` | ✅ `from sqlalchemy import or_` — [sync.py:6](file:///c:/Users/moham/Desktop/Myproject/AP/backend/sync.py#L6) |
| `JSON` در مدل رویداد | ✅ در `main.py` import شده، `payload = Column(JSON, nullable=False)` — [main.py:253](file:///c:/Users/moham/Desktop/Myproject/AP/backend/main.py#L253) |
| فراخوانی `init_db` | ✅ از `lifespan` — [main.py:377-378](file:///c:/Users/moham/Desktop/Myproject/AP/backend/main.py#L377-L378) |
| وابستگی‌ها | ✅ `backend/requirements.txt` شامل `psycopg2-binary>=2.9.9`، `sqlalchemy>=2.0.0`، `python-jose[cryptography]>=3.3.0`، `pydantic>=2.0.0` |
| تطابق ستون `member_id` در رویداد entry | ⚠️ فیلد `member_id` در payload رویداد `entry` **ارسال نمی‌شود** ([sync.py:290-301](file:///c:/Users/moham/Desktop/Myproject/AP/backend/sync.py#L290-L301)) در حالی که اسنپ‌شات آن را برمی‌گرداند ([sync.py:195](file:///c:/Users/moham/Desktop/Myproject/AP/backend/sync.py#L195)) — این عدم تقارن با محافظت null در Merge بی‌خطر شده است ولی توصیه می‌شود یکدست شود |
| `db.rollback()` بعد از commit در `teams.py` | ⚠️ بی‌اثر است و صرفاً رویدادهای ثبت‌نشدهٔ PTS را دور می‌ریزد (N4) — خطای سینتکسی نیست |

**نتیجه: بک‌اند با `py_compile` سالم است؛ هیچ متغیر تعریف‌نشده یا import نامعتبری یافت نشد.**

### ۴.۲ اندروید (Kotlin / Compose)

بررسی ایستا (بدون اجرای Gradle، طبق دستور):

| بررسی | نتیجه |
|---|---|
| `Dispatchers.Main` در `App.kt` | ✅ وابستگی `kotlinx-coroutines-android` در `app/build.gradle.kts` موجود است |
| `LifecycleEventEffect` / `Lifecycle.Event` در `Screens.kt` | ✅ وابستگی `lifecycle-runtime-compose` موجود و ایمپورت‌ها در ابتدای فایل تأیید شد |
| `SyncDifferenceRequest` / `SyncDifferenceResponse` / `SyncTeamMessageItem` / `SyncAiConfigItem` / `SyncSupportMessageItem` | ✅ در [ApiService.kt:494-524](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/ApiService.kt#L494-L524) تعریف شده‌اند (هم‌پکیج با `Db.kt` ⇒ بدون نیاز به import) |
| فیلد `member_id` در `SyncEntryItem` (استفاده‌شده در `applyDifferenceLocally`) | ✅ موجود است |
| فیلدهای `AiModel` (`modelId`, `active`, `endpoint`, `apiKey`) در `ensureServerAiModel` | ✅ منطبق با کلاس و کوئری `models()` — [Db.kt:1466](file:///c:/Users/moham/Desktop/Myproject/AP/app/src/main/java/ir/daftar/goftogu/Db.kt#L1466) |
| `NetworkModule.getApiService()` از `App.kt` | ✅ قبلاً نیز در `syncUserProfile` استفاده می‌شده است |
| `BuildConfig.OPENROUTER_KEY` در `ensureServerAiModel` | ✅ در `ensureOpenRouter` نیز استفاده شده و `buildConfigField` آن تعریف شده است |
| `ContentValues(ContentValues)` و `putAll` در `mergeRow` | ✅ هر دو متد API استاندارد `ContentValues` هستند |
| نماد تعریف‌نشده/تکراری | ✅ یافت نشد — `MAX_SYNC_PAGES`، `markSyncCompleted`، `isLocalDataEmpty`، `rowExists`، `mergeRow`، `ensureServerAiModel` همگی یک‌بار تعریف شده‌اند |

**نتیجه: هیچ خطای کامپایلی شناسایی نشد.** کامپایل واقعی Gradle طبق دستور کاربر اجرا نشد؛ در صورت نیاز پیش‌نیاز محیطی، تنظیم `JAVA_HOME` (مثلاً `C:\Users\moham\.jdks\jbr-21.0.11`) لازم است.

---

## بخش ۵ — جمع‌بندی و اولویت‌بندی باقی‌مانده

### ۵.۱ جمع‌بندی

۱. **لایهٔ نوشتن و مالکیت داده‌ها اصلاح شد.** حذف `user_id=None`، افزودن `user_id` به DTOها، و ایندکس‌گذاری PostgreSQL (یافته‌های #۱، #۳، #۸، #۱۹) باعث شد بک‌اند از حالت «لاگ فقط-نوشتنی بدون مالک» خارج شود.
۲. **کانال Pull ساخته شد ولی ناقص است.** `POST /sync/difference` هم اسنپ‌شات و هم دلتای PTS را سرو می‌کند و در سمت کلاینت با تراکنش اتمیک و استراتژی Merge اعمال می‌شود — این بزرگ‌ترین پیشرفت این دور است. اما سقف ۱۰۰ پیام تیم بدون اعلام `has_more` (N1) و نبود کانال برای `ai_messages` آن را ناقص می‌کند.
۳. **پیام‌های تیمی همچنان مسیر دوگانه و ناسالم دارند.** Push بدون outbox (N2)، Pull درج‌شده ولی خوانده‌نشده در UI (N3)، و `ThreadScreen` که از جدول `entries` می‌خواند نه `team_messages` (#۲۰ و #۶) — سه یافته‌ای که در عمل یک مسئلهٔ واحدند.
۴. **موارد باقی‌ماندهٔ عملکردی و ساختاری.** ایندکس‌های SQLite (#۹)، N+1 دو طرفه (#۱۱)، تراکنش `upsertGroupTeam` (#۱۲)، نبود FK (#۱۳)، روت تکراری (#۱۴) و کد مردهٔ تیم (#۱۵) همگی دست‌نخورده‌اند.
۵. **امنیت کلید AI و مسیر توکن بلااستفاده.** `api_key` همچنان متن‌رو در payload و دیتابیس سرور ذخیره می‌شود و `token_usage` عملاً مرده است (#۱۶).

### ۵.۲ اولویت پیشنهادی برای دور بعدی

| اولویت | اقدام | یافته‌های مرتبط |
|---|---|---|
| بحرانی | اتصال `team_messages` لوکال به UI: خواندن پیام‌های تیم در `ThreadScreen` (گروه) به‌جای/در کنار `entries` | #۶، #۲۰، N3 |
| بحرانی | انتقال پیام تیم به outbox (حذف `dispatchTeamMessage` fire-and-forget) تا آفلاین هم retry شود | N2، #۴ |
| بحرانی | اصلاح ترتیب و صفحه‌بندی پیام‌های تیم: `ORDER BY DESC` + اعلام `has_more` در اسنپ‌شات | #۱۰، N1 |
| بالا | افزودن ایندکس‌های گم‌شدهٔ SQLite و حذف N+1 در `people()` | #۹، #۱۱ |
| بالا | افزودن `beginTransaction` به `upsertGroupTeam` | #۱۲ |
| بالا | تکمیل payload سینک `addEntry` با `voice_path`/`media_path`/`member_id`/`target_recipient_id` | #۱۸ |
| متوسط | حذف کد مردهٔ تیم (`addTeam`, `addTeamMember`, `addTeamMessage`, `getTeamMessages`, `clearTeamMessages`) و روت تکراری `/users/check` | #۱۵، #۱۴ |
| متوسط | الزام `ForeignKey` روی روابط کلیدی PostgreSQL | #۱۳ |
| متوسط | واکنش‌پذیری `ThreadScreen` و `SupportChatRoom` به سینک | #۱۷ |
| پایین (امنیت) | رمزنگاری/انتقال امن `api_key` و فعال‌سازی یا حذف مسیر `token_usage` | #۱۶ |
| پایین | بررسی مجدد `db.rollback()` بی‌اثر در هوک پیام تیمی سرور | N4 |
