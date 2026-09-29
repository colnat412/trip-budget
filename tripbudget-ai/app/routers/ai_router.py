from fastapi import APIRouter, HTTPException
from app.schemas.ai_schema import GeneratePlanRequest
from app.services.ai_service import AIService

router = APIRouter(
    prefix="/api/ai",
    tags=["AI Planner"]
)

ai_service = AIService()

@router.get("/models")
async def list_models():
    try:
        return ai_service.list_available_models()
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/generate-plan")
async def generate_plan(req: GeneratePlanRequest):
    try:
        return ai_service.generate_trip_plan(req)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
