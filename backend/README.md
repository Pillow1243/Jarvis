# JARVIS Backend

FastAPI backend powering JARVIS Android.

## Run

```bash
pip install -r requirements.txt
cp .env.example .env
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

Docs: http://localhost:8000/docs
Health: http://localhost:8000/health
Metrics: http://localhost:8000/metrics

## Structure

- `app/main.py` — FastAPI app factory
- `app/core/` — config, security, db
- `app/agents/` — orchestrator, ReAct, research, operative
- `app/tools/` — 50+ device/web/memory tools
- `app/llm/` — router (local/cloud)
- `app/api/` — REST + WS routes
- `app/speech/` — STT/TTS pipeline
- `app/automation/` — scheduler, triggers
