"""LLM router tests — no network calls; mock fallback is exercised."""
import pytest

from app.llm.router import MODEL_MAP, LLMRouter, get_llm_router


@pytest.fixture()
def router():
    return LLMRouter()


def test_resolve_local_model_maps_to_ollama(router):
    model, provider = router.resolve_model("jarvis-local")
    assert provider == "ollama"
    assert model.startswith("ollama/")


def test_resolve_fast_maps_to_openai_mini(router):
    model, provider = router.resolve_model("jarvis-fast")
    assert provider == "openai"
    assert model == "gpt-4o-mini"


def test_unknown_model_falls_back_to_balanced(router):
    model, provider = router.resolve_model("some-random-model")
    assert provider == "openai"
    assert model == "gpt-4o"


def test_all_jarvis_aliases_mapped():
    for name in ["jarvis-auto", "jarvis-fast", "jarvis-powerful", "jarvis-local"]:
        assert name in MODEL_MAP


async def test_chat_completion_mock_when_no_keys(router, monkeypatch):
    router.settings.openai_api_key = None
    router.settings.anthropic_api_key = None
    result = await router.chat_completion([{"role": "user", "content": "سلام"}], model="jarvis-auto")
    assert result["choices"][0]["message"]["role"] == "assistant"
    assert result["choices"][0]["message"]["content"]
    assert result["usage"]["total_tokens"] > 0


async def test_stream_chat_yields_done_marker(router):
    chunks = [c async for c in router.stream_chat([{"role": "user", "content": "سلام"}], "jarvis-auto")]
    assert chunks[-1].strip() == "data: [DONE]"


def test_get_llm_router_singleton():
    assert get_llm_router() is get_llm_router()
