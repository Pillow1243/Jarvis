"""OpenAI-compatible chat completions + JARVIS endpoints"""
from __future__ import annotations

import time
import uuid
from typing import Any, Dict, List, Optional

from fastapi import APIRouter, HTTPException
from fastapi.responses import StreamingResponse
from pydantic import BaseModel

from app.agents.orchestrator import OrchestratorAgent
from app.llm.router import get_llm_router
from app.tools.registry import get_all_tools

router = APIRouter()


class ChatMessage(BaseModel):
    role: str
    content: str
    tool_calls: Optional[List[Dict[str, Any]]] = None
    tool_call_id: Optional[str] = None


class ChatCompletionRequest(BaseModel):
    model: str = "jarvis-auto"
    messages: List[ChatMessage]
    tools: Optional[List[Dict[str, Any]]] = None
    tool_choice: Optional[str] = None
    stream: bool = False
    temperature: float = 0.7
    max_tokens: int = 2048
    device_id: str = "default"
    conversation_id: Optional[str] = None


class AskRequest(BaseModel):
    message: str
    model: str = "jarvis-auto"
    device_id: str = "default"
    conversation_id: Optional[str] = None
    stream: bool = False


@router.post("/chat/completions")
async def chat_completions(req: ChatCompletionRequest):
    """OpenAI-compatible endpoint — main JARVIS chat."""
    try:
        llm = get_llm_router()
        messages = [m.model_dump(exclude_none=True) for m in req.messages]

        # If tools not specified, inject JARVIS tools automatically for jarvis-* models
        tools = req.tools
        if tools is None and req.model.startswith("jarvis"):
            tools = get_all_tools()

        result = await llm.chat_completion(
            messages=messages,
            model=req.model,
            tools=tools,
            stream=False,
            temperature=req.temperature,
            max_tokens=req.max_tokens,
        )
        # Add JARVIS metadata
        result["jarvis"] = {"device_id": req.device_id, "conversation_id": req.conversation_id}
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/ask")
async def ask(req: AskRequest):
    """Simplified ask endpoint — orchestrator with device tools."""
    agent = OrchestratorAgent(device_id=req.device_id, conversation_id=req.conversation_id)

    if req.stream:
        async def gen():
            async for chunk in agent.stream_chat(req.message, model=req.model):
                yield chunk
        return StreamingResponse(gen(), media_type="text/event-stream")

    result = await agent.chat(req.message, model=req.model)
    return result


@router.get("/models")
async def list_models():
    return {
        "object": "list",
        "data": [
            {"id": "jarvis-auto", "object": "model", "owned_by": "jarvis", "description": "Auto routing (recommended)"},
            {"id": "jarvis-fast", "object": "model", "owned_by": "jarvis", "description": "Fast & cheap"},
            {"id": "jarvis-powerful", "object": "model", "owned_by": "jarvis", "description": "Most capable"},
            {"id": "jarvis-local", "object": "model", "owned_by": "jarvis", "description": "On-device (Ollama)"},
            {"id": "gpt-4o", "object": "model", "owned_by": "openai"},
            {"id": "gpt-4o-mini", "object": "model", "owned_by": "openai"},
            {"id": "claude-3-5-sonnet", "object": "model", "owned_by": "anthropic"},
            {"id": "gemini-1.5-pro", "object": "model", "owned_by": "google"},
        ],
    }


@router.get("/models/{model_id}")
async def get_model(model_id: str):
    return {"id": model_id, "object": "model", "owned_by": "jarvis"}
