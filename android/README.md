# JARVIS Android App

Native Android app — Jetpack Compose + Voice + Accessibility.

## Structure

- `app/src/main/java/com/jarvis/android/`
  - `MainActivity.kt` — entry, sets Compose NavHost
  - `JarvisApp.kt` — Hilt application
  - `ui/theme/` — Material 3 theming (dark/light/dynamic)
  - `ui/screens/` — Onboarding, Chat, PermissionsHub, Dashboard, Automations, Settings, VoiceSetup
  - `ui/components/` — JarvisOrb etc.
  - `service/` — AccessibilityService, VoiceService, OverlayService, NotificationListener, TileService
  - `voice/` — STT, TTS, WakeWord
  - `network/` — Retrofit API + WebSocket
  - `automation/` — WorkManager engine
  - `data/local/` — Room DB
  - `widget/` — Home widget

## Build

```bash
./gradlew assembleDebug     # debug APK
./gradlew assembleRelease   # release APK (needs signing)
```

APKs: `app/build/outputs/apk/{debug,release}/`

## Permissions (all optional except MIC)

See `AndroidManifest.xml` + `ui/screens/PermissionsHubScreen.kt` for user-friendly hub.
