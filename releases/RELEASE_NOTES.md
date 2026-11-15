# 🤖 JARVIS v2.1.0

**نسخه‌ای که واقعاً بیلد می‌شود و روی دستگاه نصب می‌شود.**

> از این نسخه، APKهای ریلیز مستقیماً توسط GitHub Actions و Gradle واقعی ساخته می‌شوند؛
> هیچ فایل placeholder یا ساختگی در این ریلیز وجود ندارد و هر APK با checksum تأیید شده است.

## ✨ چه چیزهایی عوض شد؟

### 🏗️ بیلد و انتشار
- ✅ `gradle-wrapper.jar` به مخزن اضافه شد — بیلد محلی و CI با `./gradlew` کار می‌کند
- ✅ Hilt Gradle Plugin فعال شد (قبلش فقط KSP بود → کرش در اجرا)
- ✅ وابستگی نامعتبر `androidx.speech:speech` حذف و `work-runtime-ktx` اضافه شد
- ✅ APK جعلی از `releases/` حذف شد؛ CI دیگر APK ساختگی نمی‌سازد — بیلد واقعی یا شکست
- ✅ Verification مرحله‌ای: هر APK باید `classes.dex` و حجم معقول داشته باشد
- ✅ امضای release با secrets سفارشی (در صورت تنظیم `JARVIS_KEYSTORE_BASE64`) — وگرنه debug-signed
- ✅ ProGuard/R8 rules کامل‌تر برای Retrofit/Gson/Room/Hilt
- ✅ SHA-256 checksum برای هر APK در این ریلیز

### 📱 اپلیکیشن
- 🐞 رفع خطای کامپایل Kotlin (تایپ Brush/Color در چت، Cap در VoiceWave، import گم‌شده `border`)
- ✨ حباب پیام کاربر/دستیار در چت بازطراحی شد + اسکرول خودکار به پیام جدید
- ✨ دکمه میکروفون با درخواست واقعی `RECORD_AUDIO` و نمایش وضعیت مجوز
- ✨ AccessibilityService: ریسایکل صحیح nodeها (نشت حافظه برطرف شد)
- ✨ ویجت: کلیک روی ویجت حالا واقعاً اپ را باز می‌کند
- ✨ دکمه‌های محتوایی (contentDescription) برای دسترسی‌پذیری بهتر
- 🏷️ versionName 2.1.0 (versionCode 3)

### 🧠 Backend
- ✅ وابستگی‌ها slim شد (chromadb/sentence-transformers/celery/redis حذف — ~۲GB سبک‌تر)
- ✅ `apscheduler` اضافه شد (کد قبلی بدون آن کرش می‌کرد)
- ✅ `/metrics` حالا متریک واقعی درخواست‌ها و latency را برمی‌گرداند
- ✅ `verify_device_token` واقعاً توکن را بررسی می‌کند (در production با مقایسه constant-time)
- ✅ Exception handler خطاهای داخلی را لو نمی‌دهد؛ CORS با `*` دیگر نامعتبر نیست
- ✅ ۲۶ تست pytest + ruff تمیز
- 🐳 Dockerfile: کاربر non-root + healthcheck

## 📥 نصب
1. `jarvis-v2.1.0-release.apk` را دانلود کن (SHA-256 کنارش است)
2. «نصب از منابع ناشناخته» را برای مرورگر/فایل‌منیجر فعال کن
3. اپ را باز کن → مجوزها را تأیید کن
4. بگو: «هی جارویس، سلام» 🎙️

> ⚠️ این APK با کلید debug امضا شده (وقتی keystore در secrets تنظیم نشده باشد). برای نصب OTA کافی است؛ برای Google Play باید با release keystore امضا شود.

## 🔒 حریم خصوصی
هیچ داده‌ای بدون اجازه صریح شما از دستگاه خارج نمی‌شود. دسترسی‌ها اختیاری و قابل لغو هستند.

## 🏗️ ساخت محلی
```bash
./scripts/build-apk.sh          # debug
./scripts/build-apk.sh --release
```
