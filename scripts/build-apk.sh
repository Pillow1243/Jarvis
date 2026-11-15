#!/usr/bin/env bash
set -e
# Build JARVIS APK locally
# Usage: ./scripts/build-apk.sh [--release|--debug]

MODE="debug"
if [[ "$1" == "--release" ]]; then MODE="release"; fi

echo "🤖 Building JARVIS APK ($MODE)..."
cd "$(dirname "$0")/../android"

if [ ! -f gradlew ]; then
  echo "⚙️ Generating Gradle wrapper..."
  gradle wrapper --gradle-version 8.6
fi

chmod +x gradlew

if [[ "$MODE" == "release" ]]; then
  ./gradlew assembleRelease
  echo "✅ APK: app/build/outputs/apk/release/app-release.apk"
else
  ./gradlew assembleDebug
  echo "✅ APK: app/build/outputs/apk/debug/app-debug.apk"
fi

ls -lh app/build/outputs/apk/*/*.apk 2>/dev/null || true
