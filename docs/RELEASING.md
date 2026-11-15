# 🚢 راهنمای انتشار (Releasing) JARVIS Android

این سند توضیح می‌دهد چطور یک نسخه رسمی APK منتشر می‌شود.
**هیچ APK ساختگی/placeholder پذیرفته نیست — بیلد واقعی یا شکست.**

## پیش‌نیاز یک‌بار مصرف (برای امضای release رسمی)

ساخت keystore release (محلی، یک‌بار):

```bash
keytool -genkeypair -v \
  -keystore jarvis-release.keystore \
  -alias jarvis \
  -keyalg RSA -keysize 4096 -validity 10000
```

سپس در GitHub → Settings → Secrets and variables → Actions این secrets را ثبت کن:

| Secret | مقدار |
|---|---|
| `JARVIS_KEYSTORE_BASE64` | `base64 -w0 jarvis-release.keystore` |
| `JARVIS_KEYSTORE_PASSWORD` | رمز keystore |
| `JARVIS_KEY_ALIAS` | `jarvis` |
| `JARVIS_KEY_PASSWORD` | رمز کلید |

بدون این secrets، workflow همچنان APK می‌سازد ولی با کلید debug امضا می‌شود
(برای تست قابل نصب است، برای Play Store قابل قبول نیست).

## انتشار نسخه جدید — ۵ دقیقه

```bash
# 1) مطمئن شو تست‌ها و بیلد سبز هستند
cd backend && python -m pytest && ruff check app tests && cd ..

# 2) نسخه را در این فایل‌ها بالا ببر (versionCode +1 الزامی است):
#    - android/app/build.gradle.kts        → versionCode / versionName
#    - backend/app/core/config.py          → app_version
#    - backend/pyproject.toml              → version
#    - android/.../ui/screens/SplashScreen.kt → برچسب نسخه
#    - releases/RELEASE_NOTES.md           → یادداشت‌های همان نسخه

# 3) commit + PR + merge روی main
git checkout -b chore/release-vX.Y.Z
git commit -am "chore(release): vX.Y.Z"
git push -u origin chore/release-vX.Y.Z
gh pr create --fill
# merge بعد از سبز شدن CI

# 4) تگ و push — workflow خودش Release می‌سازد
git checkout main && git pull
git tag vX.Y.Z
git push origin vX.Y.Z
```

## چه اتفاقی می‌افتد؟

1. `backend-test` — ruff + pytest (اجباری؛ قرمز یعنی ریلیز نمی‌شود)
2. `android-build` — `./gradlew assembleDebug` + `assembleRelease` با Gradle واقعی؛
   بعدش یک اسکریپت APK را بافت می‌کشد: وجود `AndroidManifest.xml`، حداقل یک `classes*.dex`
   و حجم > ۱ مگابایت — وگرنه بیلد fail می‌شود و ریلیز ساخته نمی‌شود
3. `release` (فقط روی تگ `v*`) — APKها با نام `jarvis-vX.Y.Z-{debug,release}.apk`
   + فایل `.sha256` روی GitHub Release بارگذاری می‌شوند

## بررسی APK دانلودشده

```bash
sha256sum -c jarvis-v2.1.0-release.apk.sha256
unzip -l jarvis-v2.1.0-release.apk | head
```

## Play Store (بعد از بسته‌بندی)

- `./gradlew bundleRelease` → `app-release.aab`
- اسکرین‌شات‌ها و متادیتا در `fastlane/metadata/`
- برای Play Console: `fastlane supply` (اختیاری، نیازمند service account JSON در secrets)
