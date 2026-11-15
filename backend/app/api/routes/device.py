"""Device control & pairing endpoints"""
from __future__ import annotations

from typing import Any

from fastapi import APIRouter
from pydantic import BaseModel, Field

from app.tools.registry import execute_tool

router = APIRouter()

class DeviceCommand(BaseModel):
    tool: str
    arguments: dict[str, Any] = Field(default_factory=dict)
    device_id: str = "default"

class PairRequest(BaseModel):
    device_id: str
    name: str
    platform: str = "android"
    fcm_token: str | None = None

# In-memory device registry (replace with DB in prod)
_devices: dict[str, dict[str, Any]] = {}
_pending_commands: dict[str, list[dict[str, Any]]] = {}

@router.post("/pair")
async def pair_device(req: PairRequest):
    _devices[req.device_id] = {
        "device_id": req.device_id,
        "name": req.name,
        "platform": req.platform,
        "fcm_token": req.fcm_token,
        "paired_at": str(__import__("datetime").datetime.utcnow()),
        "online": True,
    }
    return {"status": "paired", "device": _devices[req.device_id]}

@router.get("/list")
async def list_devices():
    return {"devices": list(_devices.values())}

@router.get("/{device_id}/status")
async def device_status(device_id: str):
    dev = _devices.get(device_id)
    if not dev:
        return {"device_id": device_id, "online": False, "paired": False}
    return dev

@router.post("/command")
async def send_command(cmd: DeviceCommand):
    """Queue command for device to execute via WebSocket polling."""
    result = await execute_tool(cmd.tool, cmd.arguments, cmd.device_id)
    if cmd.device_id not in _pending_commands:
        _pending_commands[cmd.device_id] = []
    _pending_commands[cmd.device_id].append(result)
    return result

@router.get("/{device_id}/pending")
async def get_pending(device_id: str):
    cmds = _pending_commands.get(device_id, [])
    _pending_commands[device_id] = []
    return {"commands": cmds}

@router.post("/{device_id}/result")
async def post_result(device_id: str, body: dict[str, Any]):
    # Device reports execution result
    return {"status": "received", "device_id": device_id, "result": body}
