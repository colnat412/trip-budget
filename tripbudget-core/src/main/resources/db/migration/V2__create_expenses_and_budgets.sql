-- ==========================================================
-- V2__create_expenses_and_budgets.sql
-- Migration: Bổ sung is_del cho bảng cũ và tạo các bảng Chi tiêu & Ngân sách
-- Schema: trip_core
-- ==========================================================

-- 1. Bổ sung cột is_del cho các bảng hiện hữu nếu chưa có
ALTER TABLE trip_core.trips ADD COLUMN IF NOT EXISTS is_del BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE trip_core.trip_members ADD COLUMN IF NOT EXISTS is_del BOOLEAN NOT NULL DEFAULT FALSE;

-- 2. Bảng Hạn mức Ngân sách của Chuyến đi (trip_core.budgets)
CREATE TABLE IF NOT EXISTS trip_core.budgets (
    id BIGSERIAL PRIMARY KEY,
    trip_id BIGINT NOT NULL UNIQUE REFERENCES trip_core.trips(id) ON DELETE CASCADE,
    total_budget NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(3) NOT NULL DEFAULT 'VND',
    is_del BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 3. Bảng Hạn mức theo từng Danh mục (trip_core.category_budgets)
CREATE TABLE IF NOT EXISTS trip_core.category_budgets (
    id BIGSERIAL PRIMARY KEY,
    budget_id BIGINT NOT NULL REFERENCES trip_core.budgets(id) ON DELETE CASCADE,
    category VARCHAR(50) NOT NULL, -- ACCOMMODATION, TRANSPORTATION, FOOD_BEVERAGE, SIGHTSEEING, SHOPPING, ENTERTAINMENT, OTHER
    limit_amount NUMERIC(15, 2) NOT NULL,
    is_del BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_category_budgets UNIQUE (budget_id, category)
);

-- 4. Bảng Sổ cái Chi tiêu Thực tế (trip_core.expenses)
CREATE TABLE IF NOT EXISTS trip_core.expenses (
    id BIGSERIAL PRIMARY KEY,
    trip_id BIGINT NOT NULL REFERENCES trip_core.trips(id) ON DELETE CASCADE,
    payer_id BIGINT NOT NULL, -- ID người chi tiền
    title VARCHAR(255) NOT NULL, -- Tên khoản chi
    category VARCHAR(50) NOT NULL, -- ACCOMMODATION, TRANSPORTATION, FOOD_BEVERAGE, SIGHTSEEING, SHOPPING, ENTERTAINMENT, OTHER
    amount NUMERIC(15, 2) NOT NULL, -- Tổng tiền
    currency VARCHAR(3) NOT NULL DEFAULT 'VND',
    expense_date DATE NOT NULL, -- Ngày phát sinh chi tiêu
    split_type VARCHAR(30) NOT NULL DEFAULT 'EQUAL', -- EQUAL, EXACT_AMOUNT, PERCENTAGE, SHARE
    status VARCHAR(30) NOT NULL DEFAULT 'CONFIRMED', -- CONFIRMED, DRAFT, DELETED
    note TEXT,
    receipt_url VARCHAR(500),
    version BIGINT NOT NULL DEFAULT 0,
    is_del BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_expenses_trip_date ON trip_core.expenses (trip_id, expense_date DESC);
CREATE INDEX IF NOT EXISTS idx_expenses_payer ON trip_core.expenses (payer_id);

-- 5. Bảng Phân bổ Chia tiền cho từng Thành viên (trip_core.expense_splits)
CREATE TABLE IF NOT EXISTS trip_core.expense_splits (
    id BIGSERIAL PRIMARY KEY,
    expense_id BIGINT NOT NULL REFERENCES trip_core.expenses(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL, -- ID thành viên tham gia
    allocated_amount NUMERIC(15, 2) NOT NULL, -- Số tiền thực tế người này phải chịu
    split_value NUMERIC(10, 4), -- Tỷ lệ % hoặc số suất ăn ban đầu
    settled BOOLEAN NOT NULL DEFAULT FALSE, -- Đã quyết toán/trả lại chưa
    is_del BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_expense_user_split UNIQUE (expense_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_splits_user ON trip_core.expense_splits (user_id);

