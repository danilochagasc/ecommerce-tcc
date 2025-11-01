CREATE TABLE "user"(
    id          UUID PRIMARY KEY,
    name        TEXT NOT NULL,
    last_name   TEXT NOT NULL,
    cpf         TEXT NOT NULL UNIQUE,
    email       TEXT NOT NULL UNIQUE,
    password    TEXT NOT NULL,
    phone       VARCHAR(15) NOT NULL,
    birth_date  DATE NOT NULL,
    role        TEXT NOT NULL
);