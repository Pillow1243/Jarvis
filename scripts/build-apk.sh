#!/usr/bin/env bash
# Build JARVIS APK locally.
# Requires: JDK 17+ and Android SDK (ANDROID_HOME or android/local.properties).
# Usage: ./scripts/build-apk.sh [--release|--debug]
set -euo pipefail

MODE="debug"
if [[ "${1:-}" == "--release" ]]; then MODE="release"; fi

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT/android"

if [[ ! -x gradlew ]]; then chmod +x gradlew; fi

echo "🤖 Building JARVIS ($MODE)…"
if [[ "$MODE" == "release" ]]; then
  ./gradlew assembleRelease
  APK="app/build/outputs/apk/release/app-release.apk"
else
  ./gradlew assembleDebug
  APK="app/build/outputs/apk/debug/app-debug.apk"
fi

echo "✅ APK: $ROOT/android/$APK"
ls -lh "$APK"
