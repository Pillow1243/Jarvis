# Security Policy

## Reporting a Vulnerability

Please email security@jarvis.example.com or open a private security advisory on GitHub.

## Principles

- No screen data leaves device without explicit user consent
- AccessibilityService runs only on-device gestures; no cloud screenshot upload by default
- API keys stored in EncryptedSharedPreferences / Keystore
- All WebSocket connections use WSS + token auth

## Permissions

See README permission table. All powerful permissions are optional and revocable.
