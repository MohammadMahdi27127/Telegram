# 📋 Test Plan: Direct AI Chat & Teams System

## **فیچر 1: Direct AI Chat (ai-direct)**

### ✅ Test Cases

#### **TC-1.1: Access Control - Free Plan**
- **Precondition**: کاربر با `plan = "free"`
- **Action**: دکمه "چت با هوش مصنوعی" در صفحه اصلی کلیک کن
- **Expected**: 
  - صفحه upgrade نمایش داده شود
  - پیغام: "چت نامحدود با هوش مصنوعی فقط برای کاربران پلن پلاس و بالاتر"
  - دکمه "بازگشت" فعال باشد

#### **TC-1.2: Access Control - Plus Plan**
- **Precondition**: کاربر با `plan = "plus"`
- **Action**: دکمه "چت با هوش مصنوعی" کلیک کن
- **Expected**: 
  - صفحه `DirectAiChatScreen` باز شود
  - دستیار "دستیار هوش مصنوعی" نمایش داده شود
  - چت تاریخچه خالی باشد

#### **TC-1.3: Access Control - Smart Plan**
- **Precondition**: کاربر با `plan = "smart"`
- **Action**: دکمه "چت با هوش مصنوعی" کلیک کن
- **Expected**: دسترسی موفق (همانند TC-1.2)

#### **TC-1.4: Access Control - Advanced Plan**
- **Precondition**: کاربر با `plan = "advanced"`
- **Action**: دکمه "چت با هوش مصنوعی" کلیک کن
- **Expected**: دسترسی موفق (همانند TC-1.2)

#### **TC-1.5: Access Control - Admin User**
- **Precondition**: کاربر با `phone = "09052893115"` (هر plan)
- **Action**: دکمه "چت با هوش مصنوعی" کلیک کن
- **Expected**: دسترسی موفق حتی اگر `plan = "free"`

#### **TC-1.6: Send Message - Success**
- **Precondition**: کاربر plus plan در `DirectAiChatScreen`
- **Action**: 
  - متن "سلام" بنویس
  - دکمه ارسال کلیک کن
- **Expected**: 
  - پیام کاربر فوراً نمایش داده شود
  - لودینگ نمایش داده شود
  - پاسخ AI دریافت و نمایش داده شود
  - پیام در `ai_messages` جدول ذخیره شود

#### **TC-1.7: Message Persistence**
- **Precondition**: کاربر چند پیام ارسال کرده
- **Action**: صفحه بند و دوباره باز کن
- **Expected**: تمام پیام‌های قبلی نمایش داده شوند

#### **TC-1.8: Quota Enforcement - Free Plan**
- **Precondition**: کاربر free plan (5 استفاده مصرف شده)
- **Action**: سعی کن پیام ۶ام ارسال کن
- **Expected**: 
  - خطا: "سقف خلاصه‌ساز: شما به سقف مجاز ۵ خلاصه‌سازی در ماه رسیده‌اید"
  - دکمه ارسال غیرفعال شود

#### **TC-1.9: No Quota for Paid Plans**
- **Precondition**: کاربر plus plan
- **Action**: ۱۰۰ پیام پی‌در‌پی ارسال کن
- **Expected**: 
  - تمام پیام‌ها موفقیت‌آمیز ارسال شوند
  - بدون محدودیت

---

## **فیچر 2: Teams System**

### ✅ Test Cases

#### **TC-2.1: Teams Tab Access - Free Plan**
- **Precondition**: کاربر `plan = "free"`
- **Action**: روی تب "تیم‌ها" در نوار پایینی کلیک کن
- **Expected**: 
  - صفحه upgrade نمایش داده شود
  - پیغام: "فقط کاربران با پلن پلاس یا بالاتر می‌توانند تیم بسازند"

#### **TC-2.2: Teams Tab Access - Plus Plan**
- **Precondition**: کاربر `plan = "plus"`
- **Action**: روی تب "تیم‌ها" کلیک کن
- **Expected**: 
  - `TeamsScreen` باز شود
  - لیست تیم‌ها نمایش داده شود (احتمالاً خالی)

#### **TC-2.3: Teams Tab Access - Smart Plan**
- **Precondition**: کاربر `plan = "smart"`
- **Action**: روی تب "تیم‌ها" کلیک کن
- **Expected**: دسترسی موفق (همانند TC-2.2)

#### **TC-2.4: Teams Tab Access - Advanced Plan**
- **Precondition**: کاربر `plan = "advanced"`
- **Action**: روی تب "تیم‌ها" کلیک کن
- **Expected**: دسترسی موفق (همانند TC-2.2)

#### **TC-2.5: Teams Tab Access - Admin**
- **Precondition**: کاربر `phone = "09052893115"` (هر plan)
- **Action**: روی تب "تیم‌ها" کلیک کن
- **Expected**: دسترسی موفق

#### **TC-2.6: Create Team - Success**
- **Precondition**: کاربر plus plan
- **Action**: 
  - دکمه "ایجاد تیم جدید" کلیک کن
  - نام: "تیم پروژه A"
  - توضیح: "تیم توسعه"
  - اضافه کردن 2 عضو: user2, user3
  - دکمه "ایجاد" کلیک کن
- **Expected**: 
  - تیم در DB ایجاد شود
  - تیم در لیست نمایش داده شود
  - اعضا به تیم اضافه شوند

#### **TC-2.7: Get Team Details Endpoint**
- **Precondition**: تیم ایجاد شده با 3 عضو
- **Request**: `GET /api/v1/teams/{team_id}`
- **Expected Response**:
  ```json
  {
    "id": "team-uuid",
    "name": "تیم پروژه A",
    "description": "تیم توسعه",
    "creator_id": "user-123",
    "created_at": 1698765432000,
    "member_count": 3,
    "members": [
      {"user_id": "user-123", "role": "creator", "joined_at": ...},
      {"user_id": "user-2", "role": "member", "joined_at": ...},
      {"user_id": "user-3", "role": "member", "joined_at": ...}
    ]
  }
  ```

#### **TC-2.8: Add Member to Team**
- **Precondition**: تیم موجود با 2 عضو
- **Request**: `POST /api/v1/teams/{team_id}/members`
  ```json
  {"user_id": "user-4", "role": "member"}
  ```
- **Expected**: 
  - عضو اضافه شود
  - `member_count = 3`
  - Response موفق

#### **TC-2.9: Remove Member from Team**
- **Precondition**: تیم با 3 عضو
- **Request**: `DELETE /api/v1/teams/{team_id}/members/user-3`
- **Expected**: 
  - عضو حذف شود
  - `member_count = 2`

#### **TC-2.10: Update Team Details**
- **Precondition**: تیم موجود
- **Request**: `PUT /api/v1/teams/{team_id}`
  ```json
  {"name": "تیم پروژه B", "description": "تیم جدید"}
  ```
- **Expected**: 
  - تیم بروزرسانی شود
  - لیست نمایش نام جدید

#### **TC-2.11: Send Team Message**
- **Precondition**: کاربر عضو تیم
- **Request**: `POST /api/v1/teams/{team_id}/messages`
  ```json
  {"sender_id": "user-123", "content": "سلام تیم!"}
  ```
- **Expected**: 
  - پیام در `team_messages` ذخیره شود
  - Response شامل `message_id`

#### **TC-2.12: Get Team Messages**
- **Precondition**: 3 پیام در تیم
- **Request**: `GET /api/v1/teams/{team_id}/messages`
- **Expected**: 
  - آخرین 50 پیام بازگردانده شوند
  - ترتیب: `created_at ASC`
  - JSON array با TeamMessageDto

#### **TC-2.13: Delete Team**
- **Precondition**: تیم با پیام‌ها و اعضا
- **Request**: `DELETE /api/v1/teams/{team_id}`
- **Expected**: 
  - تیم از DB حذف شود
  - تمام اعضا حذف شوند
  - تمام پیام‌ها حذف شوند

#### **TC-2.14: Non-Member Cannot Send Message**
- **Precondition**: کاربر عضو تیم نیست
- **Request**: `POST /api/v1/teams/{team_id}/messages` با `sender_id` کاربر غیر عضو
- **Expected**: 
  - Error 403: "شما عضو این تیم نیستید"

---

## **دستورالعمل اجرای تست‌ها**

### **Backend Testing (Python/FastAPI)**

```bash
# ۱. Backend را شروع کن
cd backend
python main.py

# ۲. Tests را اجرا کن (curl یا Postman)

# Create Team
curl -X POST http://localhost:8000/api/v1/teams \
  -H "Content-Type: application/json" \
  -d '{
    "name": "تیم تست",
    "creator_id": "user-123",
    "member_user_ids": ["user-456"]
  }'

# Get Team Details
curl http://localhost:8000/api/v1/teams/team-uuid-123

# Send Team Message
curl -X POST http://localhost:8000/api/v1/teams/team-uuid-123/messages \
  -H "Content-Type: application/json" \
  -d '{"sender_id": "user-123", "content": "سلام"}'

# Get Team Messages
curl http://localhost:8000/api/v1/teams/team-uuid-123/messages
```

### **Android Testing (Kotlin)**

```kotlin
// DirectAiChatScreen Test
@Test
fun testDirectAiChatAccessControl() {
    val user = User(id = "user-1", phone = "09120000000", plan = "free", ...)
    
    // Free plan should show upgrade
    // Plus plan should show chat
    // Admin should bypass
}

// Teams Access Test
@Test
fun testTeamsTabAccess() {
    val user = User(plan = "plus", ...)
    
    // Should allow access to TeamsScreen
    // Should disallow for free plan
}

// Team Message Test
@Test
fun testSendTeamMessage() {
    val teamId = "team-123"
    val message = TeamMessage(
        id = "msg-1",
        teamId = teamId,
        senderId = "user-1",
        content = "سلام",
        createdAt = System.currentTimeMillis()
    )
    
    db.addTeamMessage(teamId, "user-1", "سلام")
    val messages = db.getTeamMessages(teamId)
    
    assert(messages.size == 1)
    assert(messages[0].content == "سلام")
}
```

---

## **Regression Testing**

✅ **Existing Features to Verify:**
- [ ] Regular user chat (نباید تاثیر بپذیری)
- [ ] User AI chat with persons (نباید تاثیر بپذیری)
- [ ] Admin panel (AdminDashboard همچنان فعال)
- [ ] Profile & Settings
- [ ] Payment history
- [ ] Export functionality

---

## **Status Checklist**

| Feature | Test Case | Status | Notes |
|---------|-----------|--------|-------|
| Direct AI | TC-1.1 to TC-1.9 | ⏳ Pending | Build required |
| Teams | TC-2.1 to TC-2.14 | ⏳ Pending | Build required |
| Regression | All | ⏳ Pending | After build success |

---

## **Expected Outcomes**

✅ **Direct AI Chat:**
- کاربر plus/smart/advanced می‌تواند با AI چت کند
- ادمین بدون plan محدودیت می‌تواند چت کند
- Free plan محدود به 5 استفاده

✅ **Teams:**
- کاربران plus+ می‌توانند تیم بسازند
- اعضا می‌توانند پیام‌ها ارسال کنند
- تمام CRUD operations فعال

---

