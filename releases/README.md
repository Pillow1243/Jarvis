# Releases — APK ها

این پوشه محل APK های منتشر شده است.

- هر push به `main` یک APK Debug/Release می‌سازد (Actions → Artifacts)
- هر تگ `v*` یک GitHub Release با APK می‌سازد

## دانلود آخرین نسخه

از تب [Releases](../../releases) یا Actions Artifacts دانلود کنید.

## ساخت دستی

```bash
./scripts/build-apk.sh --release
# خروجی: android/app/build/outputs/apk/release/app-release.apk
```
