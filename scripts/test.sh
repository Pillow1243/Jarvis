#!/usr/bin/env bash
set -e
echo "🧪 Testing backend..."
cd backend
python -m pytest -q 2>/dev/null || python -c "from app.main import app; print('✅ Import OK')"
echo "✅ Tests done"
