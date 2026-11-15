# JARVIS API — OpenAI-Compatible

Base URL: `https://api.jarvis.example.com` (یا `http://localhost:8000` محلی)

## Chat Completions (سازگار با OpenAI)

```bash
curl -X POST http://localhost:8000/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "model": "jarvis-auto",
    "messages": [{"role":"user","content":"هی جارویس تلگرام رو باز کن"}],
    "stream": false
  }'
```

## Ask (ساده)

```bash
curl -X POST http://localhost:8000/v1/ask \
  -d '{"message":"این صفحه رو بخون","device_id":"android123"}'
```

## Device

- `POST /v1/device/pair` — جفت‌سازی گوشی
- `POST /v1/device/command` — ارسال دستور
- `GET /v1/device/{id}/status` — وضعیت

## WebSocket

```js
ws://localhost:8000/ws/voice        // استریم صوتی
ws://localhost:8000/ws/device/{id}  // کنترل دستگاه
ws://localhost:8000/ws/chat/{cid}   // چت زنده
```

مستندات تعاملی: `http://localhost:8000/docs` (Swagger)
