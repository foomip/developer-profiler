# insert table page words

# PageWords schema

# --- !Ups

CREATE TABLE PageWords(
  id INTEGER PRIMARY KEY,
  indexablePageId INTEGER NOT NULL,
  word TEXT NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uidx_page_words ON PageWords (indexablePageId, word)

# --- !Downs

DROP TABLE PageWords;