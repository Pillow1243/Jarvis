#!/usr/bin/env bash
set -e
echo "🔍 Linting..."
pip install ruff -q || true
ruff check backend/app --exit-zero || true
echo "✅ Lint done"
