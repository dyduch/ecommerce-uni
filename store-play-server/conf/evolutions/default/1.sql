# --- !Ups

CREATE TABLE "category"
(
    "id"   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name" VARCHAR NOT NULL
);

CREATE TABLE "product"
(
    "id"          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name"        VARCHAR NOT NULL,
    "color"       VARCHAR NOT NULL,
    "price"       REAL    NOT NULL,
    "description" TEXT    NOT NULL,
    "category_id"    INTEGER NOT NULL
);

CREATE TABLE "product_quantity"
(
    "id"          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "quantity" INTEGER NOT NULL,
    "size"     INTEGER NOT NULL,
    "product_id"  INTEGER NOT NULL
);

CREATE TABLE "image"
(
    "id"      INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "url"     TEXT    NOT NULL,
    "product_id" INTEGER NOT NULL
);

CREATE TABLE "address"
(
    "id"      INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "street"  TEXT    NOT NULL,
    "number"  TEXT    NOT NULL,
    "city"    TEXT    NOT NULL,
    "zipcode" TEXT    NOT NULL,
    "county"  TEXT    NOT NULL
);

CREATE TABLE "supplier"
(
    "id"      INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name"    TEXT    NOT NULL,
    "address_id" INTEGER NOT NULL
);

CREATE TABLE "user"
(
    "id"       INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "address_id"  INTEGER NOT NULL,
    "name"     TEXT    NOT NULL,
    "password" TEXT    NOT NULL,
    "admin"    BOOLEAN NOT NULL
);

CREATE TABLE "order_data"
(
    "id"      INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "address_id" INTEGER NOT NULL,
    "user_id"    INTEGER NOT NULL,
    "total"   REAL    NOT NULL,
    "date"    TEXT    NOT NULL
);

CREATE TABLE "order_status"
(
    "id"          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "order_id" INTEGER NOT NULL,
    "status"     TEXT    NOT NULL
);


CREATE TABLE "order_item"
(
    "id"          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "order_id" INTEGER NOT NULL,
    "product_id"    INTEGER NOT NULL,
    "quantity"   INTEGER NOT NULL,
    "size"       INTEGER NOT NULL
);
    # --- !Downs

DROP TABLE "category";
DROP TABLE "product";
DROP TABLE "product_quantity";
DROP TABLE "image";
DROP TABLE "address";
DROP TABLE "supplier";
DROP TABLE "user";
DROP TABLE "order_data";
DROP TABLE "order_status";
DROP TABLE "order_item";