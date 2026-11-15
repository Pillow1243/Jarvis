"""
Orchestrator Agent — Main JARVIS agent, inspired by OpenJarvis orchestrator.
Handles routing, tool selection, multi-turn reasoning.
"""
from __future__ import annotations

import json
import uuid
from typing import Any

from app.llm.router import get_llm_router
from app.tools.registry import execute_tool, get_all_tools

SYSTEM_PROMPT = """تو جارویس هستی — دستیار هوشمند فوق‌العاده برای اندروید.

تو به تمام صفحه گوشی کاربر با مجوز او دسترسی داری و می‌تونی هر کاری رو انجام بدی:
- باز کردن اپ‌ها، کلیک، تایپ، اسکرول، خواندن صفحه
- ارسال پیام در واتساپ/تلگرام/اسمس، تماس گرفتن
- جستجوی وب، تحقیق عمیق، خلاصه‌سازی
- یادآوری، زمان‌بندی، اتوماسیون
- کنترل تنظیمات گوشی

قوانین:
- همیشه به فارسی (یا زبان کاربر) جواب بده
- اگر کاری نیاز به دسترسی دارد، ابزار مربوطه را صدا بزن
- اگر مطمئن نیستی، سوال بپرس
- هرگز نگو "نمی‌تونم" — راه حل پیشنهاد بده
- برای کارهای چندمرحله‌ای، مرحله به مرحله جلو برو

You have access to tools. Use them when needed. Think step by step.
"""


class OrchestratorAgent:
    """The brain of JARVIS."""

    def __init__(self, device_id: str = "default", conversation_id: str | None = None):
        self.device_id = device_id
        self.conversation_id = conversation_id or str(uuid.uuid4())
        self.llm = get_llm_router()
        self.history: list[dict[str, Any]] = [{"role": "system", "content": SYSTEM_PROMPT}]

    async def chat(
        self,
        user_message: str,
        tools_enabled: bool = True,
        model: str = "jarvis-auto",
        stream: bool = False,
    ) -> dict[str, Any]:
        self.history.append({"role": "user", "content": user_message})

        tools = get_all_tools() if tools_enabled else None

        response = await self.llm.chat_completion(
            messages=self.history,
            model=model,
            tools=tools,
            stream=False,
        )

        choice = response["choices"][0]
        msg = choice["message"]

        # Handle tool calls
        tool_results = []
        if "tool_calls" in msg and msg["tool_calls"]:
            for tc in msg["tool_calls"]:
                fname = tc["function"]["name"]
                fargs = json.loads(tc["function"]["arguments"]) if isinstance(tc["function"]["arguments"], str) else tc["function"]["arguments"]
                result = await execute_tool(fname, fargs, self.device_id)
                tool_results.append(result)
                self.history.append({
                    "role": "tool",
                    "tool_call_id": tc.get("id", str(uuid.uuid4())),
                    "content": json.dumps(result, ensure_ascii=False),
                })
            # Second LLM call with tool results
            followup = await self.llm.chat_completion(
                messages=self.history,
                model=model,
                stream=False,
            )
            msg = followup["choices"][0]["message"]

        self.history.append({"role": "assistant", "content": msg.get("content", "")})

        return {
            "id": f"chatcmpl-{uuid.uuid4().hex[:8]}",
            "object": "chat.completion",
            "model": model,
            "conversation_id": self.conversation_id,
            "choices": [{
                "message": msg,
                "finish_reason": choice.get("finish_reason", "stop"),
                "index": 0,
            }],
            "tool_results": tool_results,
            "usage": response.get("usage", {}),
        }

    async def stream_chat(self, user_message: str, model: str = "jarvis-auto"):
        self.history.append({"role": "user", "content": user_message})
        async for chunk in self.llm.stream_chat(self.history, model):
            yield chunk
