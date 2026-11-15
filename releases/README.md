# Releases — APK ها

APKها در این پوشه commit نمی‌شوند؛ هر تگ `v*` یک GitHub Release رسمی با APK واقعی می‌سازد.

- هر push به `main` → workflow «Android Build» بیلد و تست واقعی اجرا می‌کند
- هر تگ `v*` → علاوه بر بیلد، یک [GitHub Release](../../releases) با APK امضاشده + SHA-256 ساخته می‌شود

باینری‌های APK در Releases روی GitHub منتشر می‌شوند (تهیه‌شده توسط CI با Gradle واقعی).
فایل‌های placeholder در این مخزن دیگر وجود ندارند.

## دانلود آخرین نسخه

از تب [Releases](../../releases) یا ویزارد Actions دانلود کنید.

## ساخت دستی

```bash
./scripts/build-apk.sh --release
# خروجی: android/app/build/outputs/apk/release/app-release.apk
```

## انتشار نسخه جدید

جزئیات کامل در [docs/RELEASING.md](../docs/RELEASING.md).
