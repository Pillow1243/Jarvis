from __future__ import annotations

from functools import lru_cache

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8", extra="allow")

    app_name: str = "JARVIS Android Backend"
    app_version: str = "2.1.0"
    env: str = "production"
    log_level: str = "INFO"

    secret_key: str = "change-me-32chars-secret-key-j4rv1s"
    jwt_algorithm: str = "HS256"
    access_token_expire_minutes: int = 1440

    # LLM
    openai_api_key: str | None = None
    anthropic_api_key: str | None = None
    google_api_key: str | None = None
    ollama_base_url: str = "http://localhost:11434"
    default_model: str = "jarvis-auto"

    # DB
    database_url: str = "sqlite+aiosqlite:///./jarvis.db"
    redis_url: str = "redis://localhost:6379/0"

    # CORS
    cors_origins: str = "*"

    # Speech
    stt_provider: str = "whisper"
    tts_provider: str = "coqui"

    # Device
    device_ws_token: str = "jarvis-device-token"
    max_ws_connections: int = 100

    @property
    def cors_origins_list(self) -> list[str]:
        if self.cors_origins == "*":
            return ["*"]
        return [o.strip() for o in self.cors_origins.split(",")]

    @property
    def is_production(self) -> bool:
        return self.env == "production"


@lru_cache
def get_settings() -> Settings:
    return Settings()
