# Users schema

# --- !Ups

CREATE TABLE User (
    id INTEGER PRIMARY KEY,
    email TEXT NOT NULL,
    password TEXT NOT NULL,
    fullname TEXT NOT NULL,
    isAdmin BOOLEAN NOT NULL
);

# --- !Downs

DROP TABLE User;