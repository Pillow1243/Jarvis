"""Speech pipeline — STT/TTS stub for server-side processing"""

class SpeechPipeline:
    def transcribe(self, audio_bytes: bytes, lang: str = "fa") -> str:
        # In prod: Whisper / Deepgram
        return "[transcript placeholder]"

    def synthesize(self, text: str, lang: str = "fa") -> bytes:
        # In prod: Coqui XTTS / ElevenLabs
        return b"[audio placeholder]"
