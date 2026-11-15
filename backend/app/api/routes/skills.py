from fastapi import APIRouter
from pydantic import BaseModel
from typing import List

router = APIRouter()

_skills: List[dict] = []

class InstallRequest(BaseModel):
    source: str  # e.g. hermes:arxiv, github:user/repo, openclaw:...
    category: str = "general"

@router.post("/install")
async def install_skill(req: InstallRequest):
    item = {"source": req.source, "category": req.category, "status": "installed"}
    _skills.append(item)
    return item

@router.get("/list")
async def list_skills():
    return {"skills": _skills, "count": len(_skills)}

@router.get("/available")
async def available_skills():
    # Sample catalog
    return {
        "catalog": [
            {"id": "arxiv", "name": "ArXiv Research", "source": "hermes:arxiv"},
            {"id": "gmail", "name": "Gmail Tools", "source": "hermes:gmail"},
            {"id": "calendar", "name": "Calendar Manager", "source": "hermes:calendar"},
            {"id": "web-scraper", "name": "Web Scraper Pro", "source": "openclaw:web-scraper"},
        ]
    }
