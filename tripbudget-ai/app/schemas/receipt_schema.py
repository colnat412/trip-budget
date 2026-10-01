from typing import List, Optional
from pydantic import BaseModel, Field

    
class ReceiptItemSchema(BaseModel):
    name: str = Field()
    quantity: Optional[float] = Field(default=1.0)
    price: Optional[float] = Field(default=0.0)

class ScannedReceiptResponse(BaseModel):
    merchant_name: str = Field()
    amount: float = Field()
    currency: str = Field(default="VND")
    expense_date: Optional[str] = Field(default=None)
    category: str = Field()
    confidence: Optional[float] = Field(default=0.9, description="Độ tin cậy của AI từ 0.0 đến 1.0")
    items: List[ReceiptItemSchema] = Field(default=[])
    raw_text: Optional[str] = Field(default=None)