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
    "category"    INTEGER NOT NULL,
    FOREIGN KEY (category) references category (id)
);

CREATE TABLE "product_quantity"
(
    "quantity" INTEGER NOT NULL,
    "size"     INTEGER NOT NULL,
    "product"  INTEGER NOT NULL,
    FOREIGN KEY (product) references product (id)
);

CREATE TABLE "image"
(
    "id"      INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "url"     TEXT    NOT NULL,
    "product" INTEGER NOT NULL,
    FOREIGN KEY (product) references product (id)
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
    "address" INTEGER NOT NULL,
    FOREIGN KEY (address) references address (id)
);

CREATE TABLE "user"
(
    "id"       INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "address"  INTEGER NOT NULL,
    FOREIGN KEY (address) references address (id),
    "name"     TEXT    NOT NULL,
    "password" TEXT    NOT NULL,
    "admin"    BOOLEAN NOT NULL
);

CREATE TABLE "order_data"
(
    "id"      INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "address" INTEGER NOT NULL,
    FOREIGN KEY (address) references address (id),
    "user"    INTEGER NOT NULL,
    FOREIGN KEY (user) references user (id),
    "total"   REAL    NOT NULL,
    "date"    TEXT    NOT NULL

)

CREATE TABLE "order_status"
(
    "order_data" INTEGER NOT NULL,
    FOREIGN KEY (order_data) references order_data (id),
    "status"     TEXT    NOT NULL
)


CREATE TABLE "order_item"
(
    "order_data" INTEGER NOT NULL,
    FOREIGN KEY (order_data) references order_data (id),
    "product"    INTEGER NOT NULL,
    FOREIGN KEY (product) references product (id),
    "quantity"   INTEGER NOT NULL,
    "size"       INTEGER NOT NULL
)
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