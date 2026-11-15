# مشارکت در JARVIS اندروید

خوش آمدی! این راهنمای مشارکت به فارسی + انگلیسی است.

## شروع سریع

```bash
git clone https://github.com/Pillow1243/Jarvis.git
cd Jarvis
git checkout -b feat/your-feature

# Backend
cd backend
python -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
pip install -r requirements-dev.txt
pytest -q

# Android
cd ../android
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

## استاندارد کدنویسی

- **Kotlin:** ktlint + detekt (اجرای `./gradlew ktlintCheck`)
- **Python:** ruff + mypy
- **Commit:** Conventional Commits (`feat:`, `fix:`, `docs:` ...)

## Pull Request

1. Issue را بخوان / بساز
2. Branch جدید از `main`
3. تست بنویس
4. PR با توضیح فارسی/انگلیسی
5. CI سبز → Review → Merge

## گزارش باگ

از Template های GitHub Issues استفاده کن: Bug Report / Feature Request

## مجوز

با ارسال PR، با Apache 2.0 موافقت می‌کنی.
