CREATE TABLE product (
     id             UUID PRIMARY KEY,
     name           TEXT NOT NULL,
     description    TEXT NOT NULL,
     image_url      TEXT NOT NULL,
     price          DOUBLE PRECISION NOT NULL,
     quantity       INT NOT NULL,
     category_id    UUID NOT NULL REFERENCES category (id)
);
