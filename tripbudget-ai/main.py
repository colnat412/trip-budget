import os
from dotenv import load_dotenv

load_dotenv()

from fastapi import FastAPI
from app.routers import ai_router

app = FastAPI(
    title="TripBudget AI Service",
    description="Microservice xử lý AI bằng Python, tổ chức theo mô hình giống NestJS",
    version="1.0.0"
)

# Add router controller
app.include_router(ai_router.router)
