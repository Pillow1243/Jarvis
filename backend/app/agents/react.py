"""ReAct Agent — Thought-Action-Observation loop"""
from __future__ import annotations

from typing import Any

from app.agents.orchestrator import OrchestratorAgent


class ReActAgent(OrchestratorAgent):
    """ReAct loop for complex tasks requiring multiple tool uses."""
    max_iterations = 10

    async def run(self, task: str, model: str = "jarvis-auto") -> dict[str, Any]:
        trace = []
        for i in range(self.max_iterations):
            result = await self.chat(task if i == 0 else "ادامه بده تا کار تموم شه.", model=model)
            trace.append(result)
            # Check if done (no tool calls = final answer)
            if not result.get("tool_results"):
                break
            task = f"نتیجه ابزار: {result['tool_results']}"
        return {"trace": trace, "final": trace[-1] if trace else None}
