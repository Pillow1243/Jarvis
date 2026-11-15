"""Async SQLite + SQLAlchemy setup"""
from __future__ import annotations

from sqlalchemy.ext.asyncio import AsyncSession, async_sessionmaker, create_async_engine
from sqlalchemy.orm import DeclarativeBase

from app.core.config import get_settings

engine = None
async_session = None

class Base(DeclarativeBase):
    pass

async def init_db():
    global engine, async_session
    settings = get_settings()
    engine = create_async_engine(settings.database_url, echo=False)
    async_session = async_sessionmaker(engine, class_=AsyncSession, expire_on_commit=False)
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)

async def get_session():
    assert async_session is not None, "DB not initialized"
    async with async_session() as session:
        yield session
