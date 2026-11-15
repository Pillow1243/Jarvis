from app.main import app
from fastapi.testclient import TestClient

def test_health():
    c = TestClient(app)
    r = c.get("/health")
    assert r.status_code == 200
    assert r.json()["status"] == "ok"

def test_chat():
    c = TestClient(app)
    r = c.post("/v1/chat/completions", json={"model":"jarvis-auto","messages":[{"role":"user","content":"سلام"}]})
    assert r.status_code == 200
