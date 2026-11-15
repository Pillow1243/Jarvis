from fastapi import APIRouter
from pydantic import BaseModel
from typing import List, Optional

router = APIRouter()

_memories: List[dict] = []

class RememberRequest(BaseModel):
    key: str
    value: str
    user_id: str = "default"

@router.post("/remember")
async def remember(req: RememberRequest):
    _memories.append({"key": req.key, "value": req.value, "user_id": req.user_id})
    return {"status": "saved", "key": req.key}

@router.get("/recall")
async def recall(query: str, user_id: str = "default"):
    # naive substring search; prod uses vector DB
    results = [m for m in _memories if query.lower() in m["key"].lower() or query.lower() in m["value"].lower()]
    return {"query": query, "results": results}

@router.get("/list")
async def list_memory(user_id: str = "default"):
    return {"memories": [m for m in _memories if m["user_id"] == user_id]}

@router.delete("/{key}")
async def forget(key: str):
    global _memories
    before = len(_memories)
    _memories = [m for m in _memories if m["key"] != key]
    return {"deleted": before - len(_memories)}
