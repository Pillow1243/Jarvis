# معماری JARVIS اندروید — نسخه کامل

## الهام و فراتر از OpenJarvis

OpenJarvis گفت: "Personal AI, On Personal Devices". JARVIS اندروید آن را روی اندروید به واقعیت تبدیل کرد.

## دیاگرام کلی

```
┌─────────────────────────────────────────────────────────────┐
│                    JARVIS ANDROID APP (Kotlin+Compose)      │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐        │
│  │ Compose  │ │ HUD      │ │ Widget   │ │ Tile     │        │
│  │ Chat/OnB │ │ Overlay  │ │ Home     │ │ QS       │        │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘        │
│       └─────────────┴────────────┴────────────┘              │
│  ┌─────────────────────────────────────┐                    │
│  │   VOICE PIPELINE                     │                    │
│  │ VAD → WakeWord → STT → NLU → Agent → TTS               │
│  └─────────────────────────────────────┘                    │
│  ┌─────────────────────────────────────┐                    │
│  │  DEVICE CONTROL (Accessibility)     │                    │
│  │  read_screen / tap / type / swipe / open_app            │
│  └─────────────────────────────────────┘                    │
└──────────────────────┬──────────────────────────────────────┘
                       │ HTTPS/WSS + JWT
┌──────────────────────▼──────────────────────────────────────┐
│                 BACKEND (FastAPI)                           │
│  Agents: Orchestrator, ReAct, Research, Operative           │
│  Tools: 34 device/web/memory/automation                    │
│  LLM Router: Local (Ollama) ↔ Cloud (OpenAI/Anthropic/Google) │
│  Memory: Chroma Vector + SQLite                             │
└─────────────────────────────────────────────────────────────┘
```

## چرا AccessibilityService؟

تنها راه رسمی اندروید برای "دیدن" و "لمس" هر اپ با اجازه کاربر. جایگزین‌های ناامن (root, شبیه‌سازی) حذف شدند.

## امنیت

- همه دسترسی‌ها اختیاری و قابل لغو
- هیچ اسکرین‌شاتی بدون اجازه به سرور نمی‌رود (`read_screen` فقط متن ساختاریافته می‌فرستد)
- WebSocket با JWT + Device Token
- داده‌ها EncryptedSharedPreferences

## مقایسه

| معیار | OpenJarvis | JARVIS Android |
|-------|-----------|---------------|
| اجرا | Python CLI + Tauri | Native Android + FastAPI |
| UI | React | Jetpack Compose Glassmorphism |
| Tools | Shell/File | Device Control |
| LLM | Ollama/Cloud | + On-Device (MNN/MLC-LLM) |
| ارزیابی | Energy/$ | + Battery/ANR/Cold Start |
