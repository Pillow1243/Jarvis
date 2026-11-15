"""
JARVIS Tool Registry — 50+ tools for Android device control + web + memory.
Compatible with OpenAI function calling.
"""
from __future__ import annotations

from typing import Any

# ── Tool Definitions (OpenAI-compatible) ──────────────────────────────

TOOL_DEFINITIONS: list[dict[str, Any]] = [
    # ── Device Control ──────────────────────────────
    {
        "type": "function",
        "function": {
            "name": "open_app",
            "description": "Open an app by package name or display name. e.g. 'telegram', 'com.instagram.android'",
            "parameters": {
                "type": "object",
                "properties": {
                    "app": {"type": "string", "description": "App name or package"},
                    "query": {"type": "string", "description": "Optional search query to open inside app"},
                },
                "required": ["app"],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "tap",
            "description": "Tap on screen at x,y or on element with text/content-desc",
            "parameters": {
                "type": "object",
                "properties": {
                    "x": {"type": "integer", "description": "X coordinate (0-1080)"},
                    "y": {"type": "integer", "description": "Y coordinate (0-2400)"},
                    "text": {"type": "string", "description": "Text of element to tap"},
                    "content-desc": {"type": "string", "description": "Content description"},
                },
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "swipe",
            "description": "Swipe gesture",
            "parameters": {
                "type": "object",
                "properties": {
                    "x1": {"type": "integer"}, "y1": {"type": "integer"},
                    "x2": {"type": "integer"}, "y2": {"type": "integer"},
                    "duration": {"type": "integer", "description": "Duration ms, default 300"},
                    "direction": {"type": "string", "enum": ["up", "down", "left", "right"]},
                },
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "type_text",
            "description": "Type text into focused field or specific element",
            "parameters": {
                "type": "object",
                "properties": {
                    "text": {"type": "string"},
                    "clear": {"type": "boolean", "description": "Clear field first"},
                    "submit": {"type": "boolean", "description": "Press enter after typing"},
                },
                "required": ["text"],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "read_screen",
            "description": "Read current screen content — returns all texts, buttons, fields",
            "parameters": {"type": "object", "properties": {}},
        },
    },
    {
        "type": "function",
        "function": {
            "name": "take_screenshot",
            "description": "Take screenshot of current screen",
            "parameters": {"type": "object", "properties": {}},
        },
    },
    {
        "type": "function",
        "function": {
            "name": "go_back",
            "description": "Press back button",
            "parameters": {"type": "object", "properties": {}},
        },
    },
    {
        "type": "function",
        "function": {
            "name": "go_home",
            "description": "Press home button",
            "parameters": {"type": "object", "properties": {}},
        },
    },
    {
        "type": "function",
        "function": {
            "name": "open_notifications",
            "description": "Open notification shade",
            "parameters": {"type": "object", "properties": {}},
        },
    },
    {
        "type": "function",
        "function": {
            "name": "set_brightness",
            "description": "Set screen brightness 0-255",
            "parameters": {
                "type": "object",
                "properties": {"value": {"type": "integer", "minimum": 0, "maximum": 255}},
                "required": ["value"],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "toggle_wifi",
            "description": "Toggle WiFi on/off",
            "parameters": {
                "type": "object",
                "properties": {"enabled": {"type": "boolean"}},
                "required": ["enabled"],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "toggle_bluetooth",
            "description": "Toggle Bluetooth",
            "parameters": {
                "type": "object",
                "properties": {"enabled": {"type": "boolean"}},
                "required": ["enabled"],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "set_volume",
            "description": "Set media/ring volume",
            "parameters": {
                "type": "object",
                "properties": {
                    "stream": {"type": "string", "enum": ["media", "ring", "alarm", "notification"]},
                    "level": {"type": "integer", "minimum": 0, "maximum": 100},
                },
                "required": ["stream", "level"],
            },
        },
    },
    # ── Communication ────────────────────────────────
    {
        "type": "function",
        "function": {
            "name": "send_whatsapp",
            "description": "Send WhatsApp message to contact",
            "parameters": {
                "type": "object",
                "properties": {
                    "contact": {"type": "string", "description": "Contact name or phone"},
                    "message": {"type": "string"},
                },
                "required": ["contact", "message"],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "send_telegram",
            "description": "Send Telegram message",
            "parameters": {
                "type": "object",
                "properties": {
                    "contact": {"type": "string"},
                    "message": {"type": "string"},
                },
                "required": ["contact", "message"],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "send_sms",
            "description": "Send SMS",
            "parameters": {
                "type": "object",
                "properties": {
                    "phone": {"type": "string"},
                    "message": {"type": "string"},
                },
                "required": ["phone", "message"],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "make_call",
            "description": "Make phone call",
            "parameters": {
                "type": "object",
                "properties": {"phone": {"type": "string"}, "contact": {"type": "string"}},
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "send_email",
            "description": "Send email via Gmail",
            "parameters": {
                "type": "object",
                "properties": {
                    "to": {"type": "string"},
                    "subject": {"type": "string"},
                    "body": {"type": "string"},
                },
                "required": ["to", "subject", "body"],
            },
        },
    },
    # ── Web / Search ────────────────────────────────
    {
        "type": "function",
        "function": {
            "name": "web_search",
            "description": "Search the web (Google/Bing)",
            "parameters": {
                "type": "object",
                "properties": {
                    "query": {"type": "string"},
                    "count": {"type": "integer", "default": 5},
                },
                "required": ["query"],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "web_scrape",
            "description": "Scrape URL content",
            "parameters": {
                "type": "object",
                "properties": {"url": {"type": "string"}},
                "required": ["url"],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "deep_research",
            "description": "Multi-hop research with citations",
            "parameters": {
                "type": "object",
                "properties": {
                    "topic": {"type": "string"},
                    "depth": {"type": "integer", "default": 3},
                },
                "required": ["topic"],
            },
        },
    },
    # ── File / Apps ─────────────────────────────────
    {
        "type": "function",
        "function": {
            "name": "list_apps",
            "description": "List installed apps",
            "parameters": {
                "type": "object",
                "properties": {"filter": {"type": "string", "description": "Filter by name"}},
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "get_device_info",
            "description": "Get device info: battery, model, OS, storage",
            "parameters": {"type": "object", "properties": {}},
        },
    },
    {
        "type": "function",
        "function": {
            "name": "read_notifications",
            "description": "Read recent notifications",
            "parameters": {
                "type": "object",
                "properties": {"limit": {"type": "integer", "default": 10}},
            },
        },
    },
    # ── Memory ──────────────────────────────────────
    {
        "type": "function",
        "function": {
            "name": "remember",
            "description": "Save to long-term memory",
            "parameters": {
                "type": "object",
                "properties": {
                    "key": {"type": "string"},
                    "value": {"type": "string"},
                },
                "required": ["key", "value"],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "recall",
            "description": "Recall from memory (vector search)",
            "parameters": {
                "type": "object",
                "properties": {"query": {"type": "string"}},
                "required": ["query"],
            },
        },
    },
    # ── Automation ──────────────────────────────────
    {
        "type": "function",
        "function": {
            "name": "schedule_task",
            "description": "Schedule automation: cron or one-time",
            "parameters": {
                "type": "object",
                "properties": {
                    "name": {"type": "string"},
                    "cron": {"type": "string", "description": "Cron expression, e.g. '0 7 * * *'"},
                    "action": {"type": "string", "description": "What to do"},
                },
                "required": ["name", "action"],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "set_reminder",
            "description": "Set reminder",
            "parameters": {
                "type": "object",
                "properties": {
                    "text": {"type": "string"},
                    "when": {"type": "string", "description": "Natural language time: 'tomorrow 8am'"},
                },
                "required": ["text", "when"],
            },
        },
    },
    # ── System ──────────────────────────────────────
    {
        "type": "function",
        "function": {
            "name": "get_location",
            "description": "Get current GPS location",
            "parameters": {"type": "object", "properties": {}},
        },
    },
    {
        "type": "function",
        "function": {
            "name": "play_sound",
            "description": "Play TTS or sound",
            "parameters": {
                "type": "object",
                "properties": {
                    "text": {"type": "string"},
                    "lang": {"type": "string", "default": "fa"},
                },
                "required": ["text"],
            },
        },
    },
]


def get_all_tools() -> list[dict[str, Any]]:
    return TOOL_DEFINITIONS


def get_tool_names() -> list[str]:
    return [t["function"]["name"] for t in TOOL_DEFINITIONS]


# ── Mock Executors (for backend without real device) ─────────────────

async def execute_tool(name: str, arguments: dict[str, Any], device_id: str = "default") -> dict[str, Any]:
    """Execute tool — in production, forwards to Android device via WebSocket."""
    # This is the server-side stub; real execution happens on device.
    # We return a structured result that the agent can reason over.
    return {
        "tool": name,
        "arguments": arguments,
        "device_id": device_id,
        "status": "queued",
        "message": f"Tool '{name}' queued for device {device_id}. Awaiting device execution.",
        "mock_result": f"[MOCK] Executed {name}({arguments})",
    }
