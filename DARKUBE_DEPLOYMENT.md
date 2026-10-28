# 🔧 Darkube Backend Deployment - راهنمای تکمیل

## ❌ مشکل: CrashLoopBackOff

**درخواست:** Database URL به `localhost` اشاره می‌کند
**سبب:** Darkube کانتینر localhost/local network ندارد
**راه‌حل:** تنظیم DATABASE_URL درست در Darkube Dashboard

---

## 📋 مراحل Darkube Deployment

### 1️⃣ وارد شدن به Darkube Dashboard
- نشانی: https://darkube.ir
- لاگین با حساب شما

### 2️⃣ Configuration Backend Service
- **نام Service:** hamsohbat-backend
- **Image Registry:** registry.hamdocker.ir/mohammadmahdiabrodi/hamsohbat-backend
- **Image Tag:** dddac0b5-540e3b (یا latest)
- **Port:** 8000

### 3️⃣ Environment Variables (CRITICAL)
در Darkube Dashboard، تنظیمات زیر را وارد کنید:

```
DATABASE_URL=postgresql://USERNAME:PASSWORD@POSTGRES_HOST:5432/hamsohbat
ADMIN_PHONE=09052893115
ADMIN_PASSWORD=138727127138727127
SMS_API_KEY=wbprpwbxQg
SMS_USERNAME=your_username
SMS_PASSWORD=your_password
SMS_ORIGINATOR=3000xxxxxx
SMS_PATTERN_CODE=your_pattern_code
```

### 4️⃣ Health Check Configuration
- **Path:** `/api/health`
- **Interval:** 30s
- **Timeout:** 5s
- **Initial Delay:** 10s

### 5️⃣ Port Mapping
- **Container Port:** 8000
- **Service Port:** 80 (یا 443 برای HTTPS)

### 6️⃣ Resource Limits
- **CPU:** 500m - 1000m
- **Memory:** 256Mi - 512Mi

---

## 🗄️ PostgreSQL Setup

### برای DATABASE_URL، شما نیاز دارید به:

**Option A: PostgreSQL در Darkube**
- تماس با Darkube Support برای PostgreSQL service

**Option B: PostgreSQL External Services**
- **Railway:** https://railway.app
- **Render:** https://render.com
- **Supabase:** https://supabase.com
- **AWS RDS:** https://aws.amazon.com/rds/

### Connection String Format
```
postgresql://username:password@host:port/database
```

مثال:
```
postgresql://admin:mypassword@postgres.railway.internal:5432/hamsohbat
```

---

## 🐳 Local Testing (Optional)

اگر می‌خواهید محلی Docker تست کنید:

### docker-compose.yml

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: hamsohbat
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: your_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  backend:
    build: .
    environment:
      DATABASE_URL: postgresql://admin:your_password@postgres:5432/hamsohbat
      ADMIN_PHONE: 09052893115
      ADMIN_PASSWORD: 138727127138727127
      SMS_API_KEY: wbprpwbxQg
    ports:
      - "8000:8000"
    depends_on:
      - postgres

volumes:
  postgres_data:
```

### دستورات اجرا:

```bash
# شروع services
docker-compose up -d

# چک logs
docker-compose logs -f backend

# تست health check
curl http://localhost:8000/api/health

# متوقف کردن
docker-compose down
```

---

## ✅ Verification

بعد از deployment در Darkube:

1. بررسی pod logs:
   ```bash
   kubectl logs -f pod/hamsohbat-backend-xxxxx -n your_namespace
   ```

2. تست health check:
   ```bash
   curl https://hamsohbat.darkube.ir/api/health
   ```

3. بررسی database connection:
   - اگر logs "Database tables created successfully" نشان دهد، اتصال درست است

---

## 🚨 Troubleshooting

### اگر هنوز CrashLoopBackOff است:

1. بررسی DATABASE_URL:
   - آیا host درست است؟
   - آیا username/password درست است؟
   - آیا port درست است (معمولاً 5432)؟

2. بررسی PostgreSQL:
   - آیا PostgreSQL service در حال اجرا است؟
   - آیا database "hamsohbat" ایجاد شده است؟

3. بررسی logs:
   ```bash
   kubectl describe pod hamsohbat-backend-xxxxx
   kubectl logs hamsohbat-backend-xxxxx --previous
   ```

---

## 📱 Landing Page Deployment

برای landing page در Darkube:

```bash
cd landing
npm run build
docker build -t landing .
docker tag landing registry.hamdocker.ir/mohammadmahdiabrodi/hamsohbat-landing:latest
docker push registry.hamdocker.ir/mohammadmahdiabrodi/hamsohbat-landing:latest
```

---

## 🎯 نکات مهم

✅ Database URL باید از external host/service باشد
✅ تمام environment variables باید در Darkube Dashboard تنظیم شوند
✅ Health check path `/api/health` را تست کنید
✅ Logs را مرتباً چک کنید
✅ Database migrations خودکار است (create_all)

---

## 📞 Support

اگر مشکل حل نشد:
- تماس با Darkube Support
- بررسی PostgreSQL credentials
- چک کنید که firewall/security groups اجازه اتصال دهند
