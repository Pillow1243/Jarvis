#!/usr/bin/env bash
# Run backend test suite.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT/backend"

if [[ ! -d .venv ]]; then
  python3 -m venv .venv
  ./.venv/bin/pip install -q -r requirements-dev.txt
fi
./.venv/bin/python -m pytest -v
