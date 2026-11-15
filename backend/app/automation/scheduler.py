"""Automation scheduler — cron triggers."""
from loguru import logger

try:
    from apscheduler.schedulers.asyncio import AsyncIOScheduler
    scheduler = AsyncIOScheduler()
    _AVAILABLE = True
except Exception as e:  # pragma: no cover - apscheduler is an optional runtime dep
    scheduler = None
    _AVAILABLE = False
    logger.warning(f"apscheduler not available: {e}")


def start_scheduler():
    if not _AVAILABLE:
        logger.warning("Scheduler not started (apscheduler missing)")
        return False
    try:
        scheduler.start()
        logger.info("⏰ Scheduler started")
        return True
    except Exception as e:
        logger.warning(f"Scheduler not started: {e}")
        return False
