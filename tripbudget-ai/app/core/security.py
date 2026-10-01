import os
import secrets
from fastapi import Security, HTTPException, status
from fastapi.security import APIKeyHeader

INTERNAL_API_KEY_HEADER_NAME = "X-Internal-API-Key"
api_key_header = APIKeyHeader(name=INTERNAL_API_KEY_HEADER_NAME, auto_error=False)

def verify_internal_api_key(api_key: str = Security(api_key_header)) -> str:
    expected_api_key = os.getenv("INTERNAL_API_KEY", "").strip()
    if not expected_api_key:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Server configuration error",
        )

    if not api_key:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail=f"Missing required internal authentication header: '{INTERNAL_API_KEY_HEADER_NAME}'",
        )

    # Constant-time comparison
    if not secrets.compare_digest(api_key, expected_api_key):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid internal API key",
        )

    return api_key
