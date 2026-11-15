"""Main API router — aggregates all endpoints"""
from __future__ import annotations

from fastapi import APIRouter

from app.api.routes.automation import router as automation_router
from app.api.routes.chat import router as chat_router
from app.api.routes.device import router as device_router
from app.api.routes.health import router as health_router
from app.api.routes.memory import router as memory_router
from app.api.routes.skills import router as skills_router

api_router = APIRouter()

# OpenAI-compatible
api_router.include_router(chat_router, prefix="/v1", tags=["chat"])
# JARVIS native
api_router.include_router(device_router, prefix="/v1/device", tags=["device"])
api_router.include_router(memory_router, prefix="/v1/memory", tags=["memory"])
api_router.include_router(automation_router, prefix="/v1/automation", tags=["automation"])
api_router.include_router(skills_router, prefix="/v1/skills", tags=["skills"])
api_router.include_router(health_router, tags=["health"])
