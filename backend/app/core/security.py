"""JWT auth + API key validation"""
from __future__ import annotations

import hmac
import time

from fastapi import Header, HTTPException
from jose import JWTError, jwt

from app.core.config import get_settings

ALGORITHM = "HS256"


def create_access_token(data: dict, expires_minutes: int = 1440) -> str:
    settings = get_settings()
    to_encode = data.copy()
    to_encode["exp"] = int(time.time()) + expires_minutes * 60
    return jwt.encode(to_encode, settings.secret_key, algorithm=settings.jwt_algorithm)


def verify_token(token: str) -> dict:
    settings = get_settings()
    try:
        return jwt.decode(token, settings.secret_key, algorithms=[settings.jwt_algorithm])
    except JWTError as e:
        raise HTTPException(status_code=401, detail="Invalid token") from e


async def get_current_user(authorization: str | None = Header(None)):
    if not authorization:
        return {"user_id": "anonymous", "role": "user"}
    token = authorization.removeprefix("Bearer ").strip()
    return verify_token(token)


async def verify_device_token(x_device_token: str | None = Header(None)) -> bool:
    """Verify the shared device token.

    - In non-production environments a missing/invalid token is tolerated
      to keep local development friction low.
    - In production the token MUST match ``device_ws_token`` (constant-time
      comparison); otherwise the request is rejected with 401.
    """
    settings = get_settings()
    expected = settings.device_ws_token
    provided = x_device_token or ""
    if expected and hmac.compare_digest(provided, expected):
        return True
    if not settings.is_production:
        return True
    raise HTTPException(status_code=401, detail="Invalid device token")
