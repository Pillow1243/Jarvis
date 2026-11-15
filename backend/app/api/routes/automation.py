from fastapi import APIRouter
from pydantic import BaseModel
from typing import Optional, List
import uuid, time

router = APIRouter()

_automations: List[dict] = []

class AutomationCreate(BaseModel):
    name: str
    trigger_type: str  # schedule/location/event/voice
    trigger_config: dict
    action: str
    enabled: bool = True

@router.post("/create")
async def create_automation(req: AutomationCreate):
    item = {"id": str(uuid.uuid4())[:8], **req.model_dump(), "created_at": time.time()}
    _automations.append(item)
    return item

@router.get("/list")
async def list_automations():
    return {"automations": _automations}

@router.delete("/{automation_id}")
async def delete_automation(automation_id: str):
    global _automations
    _automations = [a for a in _automations if a["id"] != automation_id]
    return {"deleted": automation_id}

@router.post("/{automation_id}/toggle")
async def toggle_automation(automation_id: str):
    for a in _automations:
        if a["id"] == automation_id:
            a["enabled"] = not a["enabled"]
            return a
    return {"error": "not found"}
