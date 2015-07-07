# BackgroundImages schema

# --- !Ups

CREATE TABLE BackgroundImages(
  id INTEGER PRIMARY KEY,
  page TEXT NOT NULL,
  image_path TEXT NOT NULL,
  title TEXT NOT NULL,
  description TEXT NOT NULL
);

# --- !Downs

DROP TABLE BackgroundImages;