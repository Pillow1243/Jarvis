"""
JARVIS Android Backend — FastAPI Application
Super-powered, OpenJarvis-inspired, Android-optimized.
"""
from __future__ import annotations

import time
from contextlib import asynccontextmanager

from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.gzip import GZipMiddleware
from fastapi.responses import JSONResponse, ORJSONResponse

from app.core.config import get_settings
from app.core.logging import setup_logging
from app.api.router import api_router
from app.api.websocket import ws_router


@asynccontextmanager
async def lifespan(app: FastAPI):
    settings = get_settings()
    setup_logging(settings.log_level)
    # --- Startup ---
    print(f"🚀 JARVIS Backend v{settings.app_version} starting on {settings.env} ...")
    # Init DB, vector store, etc. (lazy)
    try:
        from app.core.database import init_db
        await init_db()
    except Exception as e:
        print(f"⚠️ DB init skipped: {e}")
    yield
    # --- Shutdown ---
    print("👋 JARVIS Backend shutting down...")


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
    app.add_middleware(
        CORSMiddleware,
        allow_origins=settings.cors_origins_list,
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    @app.middleware("http")
    async def add_process_time(request: Request, call_next):
        start = time.perf_counter()
        response = await call_next(request)
        response.headers["X-Process-Time"] = str(time.perf_counter() - start)
        response.headers["X-Powered-By"] = "JARVIS-Android/2.0"
        return response

    # Routers
    app.include_router(api_router)
    app.include_router(ws_router)

    @app.get("/health", tags=["system"])
    async def health():
        return {
            "status": "ok",
            "version": settings.app_version,
            "env": settings.env,
            "timestamp": time.time(),
        }

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
        return JSONResponse(
            status_code=500,
            content={"detail": str(exc), "path": str(request.url)},
        )

    return app


app = create_app()
