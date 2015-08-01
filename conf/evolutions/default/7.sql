# IndexablePages schema

# --- !Ups

CREATE TABLE IndexablePages(
  id INTEGER PRIMARY KEY,
  description TEXT NOT NULL,
  path TEXT NOT NULL,
  active BOOLEAN DEFAULT 1,
  indexedTitle TEXT DEFAULT NULL,
  indexedDescription TEXT DEFAULT NULL
);

# --- !Downs

DROP TABLE IndexablePages;