"""Automation scheduler — cron triggers"""
from apscheduler.schedulers.asyncio import AsyncIOScheduler
from loguru import logger

scheduler = AsyncIOScheduler()

def start_scheduler():
    try:
        scheduler.start()
        logger.info("⏰ Scheduler started")
    except Exception as e:
        logger.warning(f"Scheduler not started: {e}")
