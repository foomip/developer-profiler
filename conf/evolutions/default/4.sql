# Unique index for page column on BackgroundImages

# --- !Ups

CREATE UNIQUE INDEX IF NOT EXISTS uidx_bgimg_page ON BackgroundImages (page)

# --- !Downs

DROP INDEX IF EXISTS uidx_bgimg_page