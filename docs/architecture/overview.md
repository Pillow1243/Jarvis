# معماری JARVIS اندروید — نمای کلی

> الهام‌گرفته از [OpenJarvis Architecture](https://open-jarvis.github.io/OpenJarvis/architecture/overview/) — بازطراحی برای موبایل.

## ایده مرکزی: Local-First + Phone-First

OpenJarvis گفت: «Personal AI, On Personal Devices» — ما آن را روی اندروید به واقعیت تبدیل کردیم.

```
کاربر → صدا/لمس → اپ اندروید → Backend (FastAPI) → LLM Router → Tools → Device → کاربر
```

## لایه‌ها

### 1) اپ اندروید (Kotlin + Compose)

| ماژول | وظیفه |
|------|------|
| `ui/` | Compose Material 3 — چت، داشبورد، اتوماسیون، تنظیمات |
| `voice/` | STT (SpeechRecognizer/Vosk) + TTS + WakeWord |
| `service/` | Accessibility (کنترل صفحه)، Voice (فورگراند)، Overlay (HUD)، Notifications |
| `network/` | Retrofit + WebSocket به Backend |
| `automation/` | WorkManager برای اتوماسیون زمان‌بندی‌شده |
| `data/` | Room (پیام‌ها) + DataStore (تنظیمات) |

### 2) Backend (FastAPI)

| ماژول | معادل OpenJarvis | توضیح |
|------|----------------|------|
| `agents/orchestrator.py` | `agents/orchestrator.py` | مسیریابی ابزار، چندمرحله‌ای |
| `agents/react.py` | `agents/native_react.py` | ReAct loop |
| `tools/registry.py` | `tools/` (54 tools) | 30+ ابزار دستگاه + وب + حافظه |
| `llm/router.py` | `engine/` + `bench/energy.py` | مسیریابی هوشمند local/cloud + بهینه انرژی |
| `api/routes/` | `server/` | REST + WS |
| `memory/` | `memory/` | Vector store (Chroma) |

### 3) Device Control — قلب قدرت

با `AccessibilityService`:

- `read_screen()` → `AccessibilityNodeInfo` tree → dump متن/دکمه‌ها
- `tap(x,y)` / `tapOnText()` → `dispatchGesture()`
- `typeText()` → `ACTION_SET_TEXT`
- `swipe()` → `GestureDescription`
- `openApp()` → `PackageManager.getLaunchIntentForPackage()`

همه با اجازه صریح کاربر، قابل لغو در هر لحظه.

### 4) Voice Pipeline

```
Mic → VAD (Silero) → WakeWord (openWakeWord/"هی جارویس") → STT (Google/Vosk/Whisper) → NLU → Agent → TTS (Coqui/Google)
                                     ↓
                              WebSocket streaming → Backend → Tool execution via AccessibilityService
```

### 5) اتوماسیون

- کاربر می‌گوید: «هر روز ۷ صبح آب و هوا رو بگو»
- Agent → `schedule_task(cron="0 7 * * *", action="weather+tts")`
- Backend ذخیره → Android `WorkManager` زمان‌بندی → اجرا

## امنیت

- `RECORD_AUDIO` ضروری، بقیه اختیاری
- `SYSTEM_ALERT_WINDOW` و `BIND_ACCESSIBILITY_SERVICE` فقط با تایید کاربر
- هیچ اسکرین‌شاتی بدون اجازه به سرور نمی‌رود
- API با JWT + Device Token

## مقایسه با OpenJarvis

| جنبه | OpenJarvis | JARVIS Android |
|-----|-----------|---------------|
| اجرا | Python CLI + Tauri Desktop | Native Android + FastAPI |
| LLM | Ollama / Cloud | Ollama + Cloud + On-device (MNN) |
| Tools | Shell/File/Web | Device Control (tap/type/read) |
| UI | React frontend | Jetpack Compose + Overlay + Widget |
| ارزیابی | Energy/Latency/$ | + Battery/ANR/Cold Start |

## نمودار توالی (دستور صوتی)

```
User: "هی جارویس، تلگرام رو باز کن"
  → WakeWordDetector ✓
  → STT: "تلگرام رو باز کن"
  → WS /v1/ask → Backend Orchestrator
  → LLM: tool_calls=[{"name":"open_app","args":{"app":"telegram"}}]
  → Backend → WS → Device: {"type":"execute","tool":"open_app","args":{...}}
  → AccessibilityService.openApp("org.telegram.messenger")
  → Result → Backend → TTS: "تلگرام باز شد ✅"
  → Play via ExoPlayer
```
