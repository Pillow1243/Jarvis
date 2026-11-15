"""
LLM Router — mimics OpenJarvis intelligence routing.
Routes to local (Ollama) or cloud (OpenAI/Anthropic/Gemini) based on task complexity.
"""
from __future__ import annotations

import os
from enum import Enum
from typing import AsyncIterator, List, Optional

import httpx
from loguru import logger

from app.core.config import get_settings


class ModelTier(str, Enum):
    FAST = "fast"          # cheap/fast — e.g. gpt-4o-mini, gemini-flash
    BALANCED = "balanced"  # default — gpt-4o, claude-3.5-sonnet
    POWERFUL = "powerful"  # reasoning — o1, claude-3.5-opus
    LOCAL = "local"        # on-device / Ollama


# Model mapping
MODEL_MAP = {
    "jarvis-auto": ModelTier.BALANCED,
    "jarvis-fast": ModelTier.FAST,
    "jarvis-powerful": ModelTier.POWERFUL,
    "jarvis-local": ModelTier.LOCAL,
    "gpt-4o": ModelTier.BALANCED,
    "gpt-4o-mini": ModelTier.FAST,
    "claude-3-5-sonnet": ModelTier.BALANCED,
    "gemini-1.5-pro": ModelTier.BALANCED,
    "ollama/llama3.2": ModelTier.LOCAL,
}


class LLMRouter:
    """Intelligent LLM router with fallback."""

    def __init__(self):
        self.settings = get_settings()
        self._openai_client = None
        self._anthropic_client = None

    def _get_openai(self):
        if self._openai_client is None and self.settings.openai_api_key:
            from openai import AsyncOpenAI
            self._openai_client = AsyncOpenAI(api_key=self.settings.openai_api_key)
        return self._openai_client

    def _get_anthropic(self):
        if self._anthropic_client is None and self.settings.anthropic_api_key:
            import anthropic
            self._anthropic_client = anthropic.AsyncAnthropic(api_key=self.settings.anthropic_api_key)
        return self._anthropic_client

    def resolve_model(self, requested: str) -> tuple[str, str]:
        """Resolve requested model to actual provider model."""
        tier = MODEL_MAP.get(requested, ModelTier.BALANCED)
        if tier == ModelTier.LOCAL:
            return "ollama/llama3.2:3b", "ollama"
        if tier == ModelTier.FAST:
            return "gpt-4o-mini", "openai"
        if tier == ModelTier.POWERFUL:
            return "gpt-4o", "openai"
        return "gpt-4o", "openai"

    async def chat_completion(
        self,
        messages: List[dict],
        model: str = "jarvis-auto",
        tools: Optional[List[dict]] = None,
        stream: bool = False,
        temperature: float = 0.7,
        max_tokens: int = 2048,
    ) -> dict:
        """Route and execute chat completion with fallback."""
        actual_model, provider = self.resolve_model(model)

        # Try Ollama first if local
        if provider == "ollama":
            try:
                return await self._call_ollama(messages, actual_model, tools, stream)
            except Exception as e:
                logger.warning(f"Ollama failed, falling back to cloud: {e}")
                actual_model, provider = "gpt-4o-mini", "openai"

        # Cloud providers
        if provider == "openai":
            client = self._get_openai()
            if client:
                return await self._call_openai(client, messages, actual_model, tools, stream, temperature, max_tokens)

        # Fallback: mock response for demo/dev without keys
        logger.warning("No LLM provider configured — returning mock response")
        return self._mock_response(messages)

    async def _call_openai(self, client, messages, model, tools, stream, temperature, max_tokens):
        kwargs = dict(model=model, messages=messages, temperature=temperature, max_tokens=max_tokens)
        if tools:
            kwargs["tools"] = tools
            kwargs["tool_choice"] = "auto"
        if stream:
            # For streaming, return async iterator wrapper
            resp = await client.chat.completions.create(**kwargs, stream=True)
            return {"stream": resp, "model": model}
        resp = await client.chat.completions.create(**kwargs)
        return resp.model_dump()

    async def _call_ollama(self, messages, model, tools, stream):
        async with httpx.AsyncClient(timeout=60) as client:
            resp = await client.post(
                f"{self.settings.ollama_base_url}/api/chat",
                json={"model": model, "messages": messages, "stream": False},
            )
            resp.raise_for_status()
            data = resp.json()
            return {
                "id": "ollama-" + data.get("created_at", ""),
                "object": "chat.completion",
                "model": model,
                "choices": [{
                    "message": {"role": "assistant", "content": data["message"]["content"]},
                    "finish_reason": "stop",
                    "index": 0,
                }],
                "usage": {"prompt_tokens": 0, "completion_tokens": 0, "total_tokens": 0},
            }

    def _mock_response(self, messages: List[dict]) -> dict:
        last_user = next((m["content"] for m in reversed(messages) if m["role"] == "user"), "سلام!")
        # Simple Persian-aware mock
        if any(kw in last_user for kw in ["سلام", "هی جارویس", "جارویس"]):
            content = "سلام! من جارویس هستم — دستیار هوشمند اندروید شما. چطور می‌تونم کمکت کنم؟ 🎙️\n\nمی‌تونی بگی:\n• «تلگرام رو باز کن»\n• «این صفحه رو بخون»\n• «به ... پیام بده»"
        elif "باز کن" in last_user or "open" in last_user.lower():
            content = f"✅ درخواست شما دریافت شد: «{last_user}»\n\nدر حالت واقعی، این دستور به AccessibilityService ارسال می‌شود و اپ مربوطه باز خواهد شد."
        else:
            content = f"پیام شما: «{last_user}»\n\nمن در حالت Mock هستم. برای هوش واقعی، کلید API خود را در backend/.env قرار دهید (OPENAI_API_KEY / ANTHROPIC_API_KEY / GOOGLE_API_KEY)."
        return {
            "id": "mock-001",
            "object": "chat.completion",
            "model": "jarvis-mock",
            "choices": [{
                "message": {"role": "assistant", "content": content},
                "finish_reason": "stop",
                "index": 0,
            }],
            "usage": {"prompt_tokens": 10, "completion_tokens": 50, "total_tokens": 60},
        }

    async def stream_chat(self, messages, model="jarvis-auto", tools=None) -> AsyncIterator[str]:
        """Yield SSE chunks for streaming."""
        result = await self.chat_completion(messages, model, tools, stream=False)
        # Simulate streaming by chunking
        content = result["choices"][0]["message"]["content"]
        for word in content.split():
            yield f"data: {word} \n\n"
        yield "data: [DONE]\n\n"


_router_instance: Optional[LLMRouter] = None

def get_llm_router() -> LLMRouter:
    global _router_instance
    if _router_instance is None:
        _router_instance = LLMRouter()
    return _router_instance
