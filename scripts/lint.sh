#!/usr/bin/env bash
# Lint backend (ruff) and Android sources (basic checks).
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

echo "🔍 Ruff…"
cd "$ROOT/backend"
ruff check app tests

echo "🔍 Gradle wrapper sanity…"
test -f "$ROOT/android/gradle/wrapper/gradle-wrapper.jar" && echo "wrapper jar: OK"
echo "✅ Lint done"
