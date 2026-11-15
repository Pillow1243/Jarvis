"""API smoke & contract tests — run without any LLM keys (mock path)."""
from fastapi.testclient import TestClient

from app.main import app

client = TestClient(app)


def test_health():
    r = client.get("/health")
    assert r.status_code == 200
    body = r.json()
    assert body["status"] == "ok"
    assert "version" in body and "timestamp" in body


def test_root():
    r = client.get("/")
    assert r.status_code == 200
    assert "version" in r.json()


def test_chat_completions_mock():
    r = client.post(
        "/v1/chat/completions",
        json={"model": "jarvis-auto", "messages": [{"role": "user", "content": "سلام"}]},
    )
    assert r.status_code == 200
    body = r.json()
    assert body["choices"][0]["message"]["role"] == "assistant"
    assert body["choices"][0]["message"]["content"].strip()
    assert body["jarvis"]["device_id"] == "default"


def test_ask_endpoint():
    r = client.post("/v1/ask", json={"message": "سلام جارویس"})
    assert r.status_code == 200
    body = r.json()
    assert body["choices"][0]["message"]["content"].strip()


def test_list_models():
    r = client.get("/v1/models")
    assert r.status_code == 200
    ids = [m["id"] for m in r.json()["data"]]
    assert "jarvis-auto" in ids


def test_device_pair_and_list():
    r = client.post("/v1/device/pair", json={"device_id": "test-device-1", "name": "Pixel", "platform": "android"})
    assert r.status_code == 200
    assert r.json()["status"] == "paired"
    listed = client.get("/v1/device/list")
    assert any(d["device_id"] == "test-device-1" for d in listed.json()["devices"])


def test_device_command_returns_structured_result():
    r = client.post("/v1/device/command", json={"tool": "open_app", "arguments": {"app": "telegram"}})
    assert r.status_code == 200
    body = r.json()
    assert body["tool"] == "open_app"
    assert body["status"] == "queued"


def test_memory_roundtrip():
    remember = client.post("/v1/memory/remember", json={"key": "pytest-color", "value": "blue", "user_id": "tester"})
    assert remember.status_code == 200
    recall = client.get("/v1/memory/recall", params={"query": "pytest-color", "user_id": "tester"})
    assert recall.status_code == 200
    assert any(m["value"] == "blue" for m in recall.json()["results"])


def test_automation_crud():
    created = client.post(
        "/v1/automation/create",
        json={
            "name": "pytest-morning",
            "trigger_type": "schedule",
            "trigger_config": {"time": "07:00"},
            "action": "tell weather",
        },
    )
    assert created.status_code == 200
    aid = created.json()["id"]
    listing = client.get("/v1/automation/list")
    assert any(a["id"] == aid for a in listing.json()["automations"])
    toggled = client.post(f"/v1/automation/{aid}/toggle")
    assert toggled.status_code == 200
    assert toggled.json()["enabled"] is False
    deleted = client.delete(f"/v1/automation/{aid}")
    assert deleted.status_code == 200


def test_toggle_missing_automation_is_404():
    r = client.post("/v1/automation/does-not-exist/toggle")
    assert r.status_code == 404


def test_skills_list_and_available():
    skills = client.get("/v1/skills/list")
    assert skills.status_code == 200
    available = client.get("/v1/skills/available")
    assert available.status_code == 200
    assert available.json()["catalog"]
