"""Security primitives: JWT roundtrip + device token enforcement."""
import pytest
from fastapi import HTTPException

from app.core import security
from app.core.config import get_settings


def test_jwt_roundtrip():
    token = security.create_access_token({"user_id": "u-1"}, expires_minutes=5)
    payload = security.verify_token(token)
    assert payload["user_id"] == "u-1"
    assert int(payload["exp"]) > 0


def test_verify_token_rejects_garbage():
    with pytest.raises(HTTPException) as ei:
        security.verify_token("not-a-token")
    assert ei.value.status_code == 401


async def test_device_token_allowed_in_dev(monkeypatch):
    monkeypatch.setenv("ENV", "development")
    get_settings.cache_clear()
    assert await security.verify_device_token(None) is True
    get_settings.cache_clear()


async def test_device_token_enforced_in_production(monkeypatch):
    monkeypatch.setenv("ENV", "production")
    monkeypatch.setenv("DEVICE_WS_TOKEN", "super-secret-device-token")
    get_settings.cache_clear()
    assert await security.verify_device_token("super-secret-device-token") is True
    with pytest.raises(HTTPException) as ei:
        await security.verify_device_token("wrong-token")
    assert ei.value.status_code == 401
    with pytest.raises(HTTPException):
        await security.verify_device_token(None)
    get_settings.cache_clear()
