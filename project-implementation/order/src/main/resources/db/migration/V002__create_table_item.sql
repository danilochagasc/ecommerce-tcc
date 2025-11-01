CREATE TABLE item (
id            UUID PRIMARY KEY,
order_id      UUID NOT NULL REFERENCES "order" (id) ON DELETE CASCADE,
name          TEXT NOT NULL,
price         DOUBLE PRECISION NOT NULL,
quantity      INT NOT NULL
);