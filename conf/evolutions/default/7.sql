# IndexablePages schema

# --- !Ups

CREATE TABLE IndexablePages(
  id INTEGER PRIMARY KEY,
  description TEXT NOT NULL,
  path TEXT NOT NULL,
  active BOOLEAN DEFAULT 1
);

# --- !Downs

DROP TABLE IndexablePages;