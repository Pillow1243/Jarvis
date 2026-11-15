# مستندات JARVIS اندروید

- [معماری کلی](architecture/overview.md)
- [راهنمای Backend](../backend/README.md)
- [مجوزها و حریم خصوصی](../README.md#مجوزها)
- [API Docs](http://localhost:8000/docs) — بعد از اجرای backend

## شروع سریع

```bash
# Backend
cd backend && pip install -r requirements.txt && uvicorn app.main:app --reload

# Android
cd android && ./gradlew assembleDebug
```
