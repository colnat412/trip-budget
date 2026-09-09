package com.tripbudget.tripbudget_core.expense.enums;

public enum ExpenseStatus {
    CONFIRMED,  // Đã xác nhận chính thức, tính vào sổ cái
    DRAFT,      // Bản nháp (từ OCR hoặc tạo trước chưa chốt)
    DELETED     // Đã xóa mềm
}

