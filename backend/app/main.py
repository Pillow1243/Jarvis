"""
JARVIS Android Backend — FastAPI Application
Super-powered, OpenJarvis-inspired, Android-optimized.
"""
from __future__ import annotations

import logging
import time
from contextlib import asynccontextmanager

from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.gzip import GZipMiddleware
from fastapi.responses import JSONResponse, ORJSONResponse

from app.api.router import api_router
from app.api.websocket import ws_router
from app.core.config import get_settings
from app.core.logging import setup_logging


@asynccontextmanager
async def lifespan(app: FastAPI):
    settings = get_settings()
    setup_logging(settings.log_level)
    logger = logging.getLogger("jarvis")
    # --- Startup ---
    logger.info("🚀 JARVIS Backend v%s starting on env=%s", settings.app_version, settings.env)
    if settings.is_production and settings.secret_key.startswith("change-me"):
        logger.warning("⚠️  SECRET_KEY is the default development value — set a strong SECRET_KEY in production!")
    try:
        from app.core.database import init_db
        await init_db()
        logger.info("✅ Database initialized")
    except Exception as e:
        logger.warning("⚠️ DB init skipped: %s", e)
    yield
    # --- Shutdown ---
    logger.info("👋 JARVIS Backend shutting down...")


def create_app() -> FastAPI:
    settings = get_settings()
    app = FastAPI(
        title=settings.app_name,
        version=settings.app_version,
        description="""
# 🤖 JARVIS Android Backend API

**Super-powered Personal AI for Android — Inspired by OpenJarvis, built for mobile.**

- OpenAI-compatible Chat Completions (`/v1/chat/completions`)
- Device Control (open app, tap, type, read screen)
- Voice Pipeline (STT/TTS streaming via WebSocket)
- Memory & Vector Search
- Automation & Scheduling
- Skills (agentskills.io compatible)

📚 See [GitHub](https://github.com/Pillow1243/Jarvis) for full docs.
        """,
        lifespan=lifespan,
        default_response_class=ORJSONResponse,
        docs_url="/docs",
        redoc_url="/redoc",
        openapi_url="/openapi.json",
    )

    # Middleware
    app.add_middleware(GZipMiddleware, minimum_size=500)
    cors_origins = settings.cors_origins_list
    # "*" with allow_credentials=True is rejected by browsers — disable credentials then.
    allow_credentials = cors_origins != ["*"]
    app.add_middleware(
        CORSMiddleware,
        allow_origins=cors_origins,
        allow_credentials=allow_credentials,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    app.state.metrics = {"requests_total": 0, "latency_sum_ms": 0.0, "started_at": time.time()}

    @app.middleware("http")
    async def add_process_time(request: Request, call_next):
        start = time.perf_counter()
        response = await call_next(request)
        elapsed_ms = (time.perf_counter() - start) * 1000
        metrics = request.app.state.metrics
        metrics["requests_total"] += 1
        metrics["latency_sum_ms"] += elapsed_ms
        response.headers["X-Process-Time"] = f"{elapsed_ms / 1000:.4f}"
        response.headers["X-Powered-By"] = "JARVIS-Android/2.1"
        return response

    # Routers
    app.include_router(api_router)
    app.include_router(ws_router)

    @app.get("/", tags=["system"])
    async def root():
        return {
            "name": settings.app_name,
            "version": settings.app_version,
            "docs": "/docs",
            "health": "/health",
            "message": "🤖 JARVIS is online. Say 'Hey Jarvis'!",
        }

    @app.exception_handler(Exception)
    async def global_exception_handler(request: Request, exc: Exception):
        # Never leak internals (paths, secrets, traces) to clients.
        logging.getLogger("jarvis").exception("Unhandled error on %s %s", request.method, request.url.path)
        return JSONResponse(
            status_code=500,
            content={"detail": "Internal server error"},
        )

    return app


def main() -> None:
    """Console entry point: `jarvis-server`."""
    import uvicorn

    settings = get_settings()
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=not settings.is_production)


app = create_app()
