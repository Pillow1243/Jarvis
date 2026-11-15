"""
WebSocket — real-time voice + device bridge
"""
from __future__ import annotations

import json
import uuid

from fastapi import APIRouter, WebSocket, WebSocketDisconnect

ws_router = APIRouter()

# Connected clients
device_sockets: dict[str, WebSocket] = {}
voice_sockets: dict[str, WebSocket] = {}

@ws_router.websocket("/ws/device/{device_id}")
async def device_ws(websocket: WebSocket, device_id: str):
    await websocket.accept()
    device_sockets[device_id] = websocket
    print(f"📱 Device connected: {device_id}")
    try:
        while True:
            data = await websocket.receive_text()
            msg = json.loads(data)
            # Device sends screen data, results, etc.
            # Broadcast to any listening voice clients
            for vs in voice_sockets.values():
                try:
                    await vs.send_text(json.dumps({"from": device_id, **msg}))
                except Exception:
                    pass
            # Echo ack
            await websocket.send_text(json.dumps({"type": "ack", "id": msg.get("id")}))
    except WebSocketDisconnect:
        device_sockets.pop(device_id, None)
        print(f"📱 Device disconnected: {device_id}")

@ws_router.websocket("/ws/voice")
async def voice_ws(websocket: WebSocket):
    await websocket.accept()
    sid = str(uuid.uuid4())[:6]
    voice_sockets[sid] = websocket
    print(f"🎙️ Voice client connected: {sid}")
    try:
        while True:
            data = await websocket.receive_text()
            msg = json.loads(data)
            mtype = msg.get("type")

            if mtype == "audio_chunk":
                # In prod: forward to STT pipeline, then to agent
                await websocket.send_text(json.dumps({
                    "type": "partial_transcript",
                    "text": "[در حال گوش دادن...]",
                }))

            elif mtype == "transcript":
                text = msg.get("text", "")
                # Simple agent echo for demo
                await websocket.send_text(json.dumps({
                    "type": "agent_thinking",
                    "text": f"در حال پردازش: {text}",
                }))
                # Forward command to device if connected
                for ds in device_sockets.values():
                    try:
                        await ds.send_text(json.dumps({"type": "execute", "text": text}))
                    except Exception:
                        pass
                await websocket.send_text(json.dumps({
                    "type": "agent_response",
                    "text": f"دستور دریافت شد: «{text}» — در حال اجرا...",
                }))

            elif mtype == "ping":
                await websocket.send_text(json.dumps({"type": "pong"}))

            else:
                await websocket.send_text(json.dumps({"type": "echo", "data": msg}))

    except WebSocketDisconnect:
        voice_sockets.pop(sid, None)
        print(f"🎙️ Voice client disconnected: {sid}")

@ws_router.websocket("/ws/chat/{conversation_id}")
async def chat_ws(websocket: WebSocket, conversation_id: str):
    await websocket.accept()
    try:
        while True:
            data = await websocket.receive_json()
            user_msg = data.get("message", "")
            # Stream via orchestrator
            from app.agents.orchestrator import OrchestratorAgent
            agent = OrchestratorAgent(conversation_id=conversation_id)
            # Send thinking
            await websocket.send_json({"type": "thinking", "text": "جارویس داره فکر می‌کنه..."})
            result = await agent.chat(user_msg)
            content = result["choices"][0]["message"]["content"]
            # Stream word by word
            for word in content.split():
                await websocket.send_json({"type": "token", "token": word + " "})
            await websocket.send_json({"type": "done", "full": content, "tool_results": result.get("tool_results", [])})
    except WebSocketDisconnect:
        pass
