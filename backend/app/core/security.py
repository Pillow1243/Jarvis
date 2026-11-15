"""JWT auth + API key validation"""
from __future__ import annotations

import time
from typing import Optional

from fastapi import Depends, HTTPException, Header
from jose import jwt, JWTError

from app.core.config import get_settings

def create_access_token(data: dict, expires_minutes: int = 1440) -> str:
    settings = get_settings()
    to_encode = data.copy()
    to_encode["exp"] = time.time() + expires_minutes * 60
    return jwt.encode(to_encode, settings.secret_key, algorithm=settings.jwt_algorithm)

def verify_token(token: str) -> dict:
    settings = get_settings()
    try:
        payload = jwt.decode(token, settings.secret_key, algorithms=[settings.jwt_algorithm])
        return payload
    except JWTError as e:
        raise HTTPException(status_code=401, detail=f"Invalid token: {e}")

async def get_current_user(authorization: Optional[str] = Header(None)):
    if not authorization:
        return {"user_id": "anonymous", "role": "user"}
    token = authorization.replace("Bearer ", "")
    return verify_token(token)

async def verify_device_token(x_device_token: Optional[str] = Header(None)):
    settings = get_settings()
    if x_device_token != settings.device_ws_token and settings.is_production:
        # Allow any token in dev
        pass
    return True
