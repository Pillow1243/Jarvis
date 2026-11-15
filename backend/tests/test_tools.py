"""Tool registry contract tests."""
from app.tools.registry import TOOL_DEFINITIONS, execute_tool, get_all_tools, get_tool_names


def test_all_tools_are_openai_shaped():
    tools = get_all_tools()
    assert isinstance(tools, list) and len(tools) > 0
    for t in tools:
        assert t["type"] == "function"
        fn = t["function"]
        assert fn["name"] and isinstance(fn["name"], str)
        assert fn["description"] and isinstance(fn["description"], str)
        params = fn["parameters"]
        assert params["type"] == "object"
        assert "properties" in params


def test_tool_names_unique():
    names = get_tool_names()
    assert len(names) == len(set(names))


def test_required_fields_exist_in_properties():
    """Every "required" entry must be declared in properties — a broken schema
    would make OpenAI reject the whole tools array."""
    for t in TOOL_DEFINITIONS:
        props = t["function"]["parameters"].get("properties", {})
        for req in t["function"]["parameters"].get("required", []):
            assert req in props, f"{t['function']['name']}: required '{req}' missing from properties"


def test_execute_tool_returns_structured_queue_result():
    import asyncio

    result = asyncio.run(execute_tool("open_app", {"app": "telegram"}, "device-9"))
    assert result["tool"] == "open_app"
    assert result["device_id"] == "device-9"
    assert result["status"] == "queued"
    assert "open_app" in result["message"]
