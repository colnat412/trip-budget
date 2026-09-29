import os
import json
import requests
import time
from app.schemas.ai_schema import GeneratePlanRequest

class AIService:
    def __init__(self):
        self.api_key = os.getenv("GEMINI_API_KEY", "").strip()

    def list_available_models(self):
        if not self.api_key:
            raise ValueError("Thiếu GEMINI_API_KEY")
        
        url = f"https://generativelanguage.googleapis.com/v1beta/models?key={self.api_key}"
        resp = requests.get(url)
        if resp.status_code != 200:
            return {"error": resp.json()}
        return {"models": [m["name"] for m in resp.json().get("models", [])]}

    def generate_trip_plan(self, req: GeneratePlanRequest):
        if not self.api_key:
            raise ValueError("Thiếu GEMINI_API_KEY trong file .env")

        prompt = f"""
        Bạn là một chuyên gia thiết kế lịch trình du lịch tối ưu chi phí.
        Hãy lập lịch trình chi tiết cho chuyến đi đến {req.destination} trong {req.days} ngày cho {req.people} người.
        Tổng ngân sách dự kiến (cho tất cả mọi người): {req.budget:,.0f} VNĐ.
        Yêu cầu bổ sung từ người dùng: {req.preferences if req.preferences else "Không có"}

        Lưu ý QUAN TRỌNG:
        - Phân bổ chi phí hợp lý để tổng estimatedCost của tất cả hoạt động không vượt quá tổng ngân sách.
        - category CHỈ ĐƯỢC CHỌN 1 TRONG: FLIGHT, HOTEL, FOOD, TRANSPORT, SIGHTSEEING, ENTERTAINMENT, SHOPPING, OTHER.
        - Trả về 100% bằng tiếng Việt.
        - BẠN PHẢI TRẢ VỀ ĐÚNG FORMAT JSON DƯỚI ĐÂY, KHÔNG ĐƯỢC CÓ TEXT NÀO KHÁC BÊN NGOÀI:
        """

        prompt += """
        {
          "days": [
            {
              "dayNumber": 1,
              "activities": [
                {
                  "title": "Tên hoạt động",
                  "startTime": "08:00",
                  "endTime": "10:00",
                  "location": "Địa điểm",
                  "category": "SIGHTSEEING",
                  "estimatedCost": 150000,
                  "note": "Ghi chú ngắn"
                }
              ]
            }
          ]
        }
        """

        candidate_models = [
            "models/gemini-flash-lite-latest",
            "models/gemini-3.5-flash-lite",
            "models/gemini-3.1-flash-lite",
            "models/gemini-flash-latest",
            "models/gemini-3.8-flash"
        ]
        
        payload = {
            "contents": [{"parts": [{"text": prompt}]}],
            "generationConfig": {
                "temperature": 0.7,
                "responseMimeType": "application/json"
            }
        }
        headers = {"Content-Type": "application/json"}
        
        last_error = None
        for model in candidate_models:
            url = f"https://generativelanguage.googleapis.com/v1beta/{model}:generateContent?key={self.api_key}"
            
            resp = requests.post(url, json=payload, headers=headers)
            resp_data = resp.json()
            
            if resp.status_code == 200:
                raw_text = resp_data["candidates"][0]["content"]["parts"][0]["text"]
                return json.loads(raw_text)
                
            elif resp.status_code == 503:
                time.sleep(2)
                resp = requests.post(url, json=payload, headers=headers)
                if resp.status_code == 200:
                    raw_text = resp.json()["candidates"][0]["content"]["parts"][0]["text"]
                    return json.loads(raw_text)
                resp_data = resp.json()

            last_error = resp_data
            continue
            
        raise Exception(f"Đã thử toàn bộ danh sách model Gemini nhưng đều thất bại! Lỗi cuối: {last_error}")
