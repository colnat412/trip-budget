from fastapi import APIRouter, HTTPException, Security, UploadFile, File
from app.schemas.ai_schema import GeneratePlanRequest
from app.services.ai_service import AIService
from app.core.security import verify_internal_api_key
from app.schemas.receipt_schema import ScannedReceiptResponse


router = APIRouter(
    prefix="/api/ai",
    tags=["AI Planner"]
)

ai_service = AIService()

@router.get("/models", dependencies=[Security(verify_internal_api_key)])
async def list_models():
    try:
        return ai_service.list_available_models()
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/generate-plan", dependencies=[Security(verify_internal_api_key)])
async def generate_plan(req: GeneratePlanRequest):
    try:
        return ai_service.generate_trip_plan(req)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post(
    "/scan-receipt",
    response_model=ScannedReceiptResponse,
    dependencies=[Security(verify_internal_api_key)]
)
async def scan_receipt(file: UploadFile = File(...)):
    try:
        image_bytes = await file.read()
        return ai_service.scan_receipt_image(
            image_bytes=image_bytes, 
            mime_type=file.content_type or "image/jpeg"
        )
    except ValueError as val_err:
        raise HTTPException(status_code=400, detail=str(val_err))
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.get("/health")
async def health_check():
    return {"status": "healthy", "service": "tripbudget-ai"}

@router.get("/test")
async def test():
    return {"message": "API is working!"}
