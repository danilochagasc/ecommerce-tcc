CREATE TABLE address(
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL REFERENCES "user" (id),
    street      TEXT NOT NULL,
    number      TEXT NOT NULL,
    complement  TEXT,
    neighborhood  TEXT NOT NULL,
    city        TEXT NOT NULL,
    state       CHAR(2) NOT NULL,
    zip_code    TEXT NOT NULL
);