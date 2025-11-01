CREATE TABLE "order"(
    id              UUID PRIMARY KEY,
    account_id      UUID NOT NULL,
    total           DOUBLE PRECISION NOT NULL,
    coupon          TEXT,
    status          TEXT NOT NULL,
    payment_type    TEXT NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL
);