from fastapi import APIRouter
import time
router = APIRouter()

@router.get("/health")
async def health():
    return {"status": "ok", "timestamp": time.time()}

@router.get("/metrics")
async def metrics():
    return {"requests_total": 0, "avg_latency_ms": 0}
