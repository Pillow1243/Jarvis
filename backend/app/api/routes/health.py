"""Health & metrics — real, dependency-free instrumentation."""
import time

from fastapi import APIRouter, Request

from app.core.config import get_settings

router = APIRouter()


@router.get("/health")
async def health():
    settings = get_settings()
    return {
        "status": "ok",
        "version": settings.app_version,
        "env": settings.env,
        "timestamp": time.time(),
    }


@router.get("/metrics")
async def metrics(request: Request):
    m = getattr(request.app.state, "metrics", None)
    if not m:
        return {"requests_total": 0, "avg_latency_ms": 0.0, "uptime_seconds": 0.0}
    total = m["requests_total"]
    avg = (m["latency_sum_ms"] / total) if total else 0.0
    return {
        "requests_total": total,
        "avg_latency_ms": round(avg, 2),
        "uptime_seconds": round(time.time() - m["started_at"], 2),
    }
