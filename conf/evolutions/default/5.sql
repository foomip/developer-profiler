# Add index background image description data

# --- !Ups

INSERT INTO BackgroundImages(page, title, image_path, description)
VALUES (
  'about_me',
  'African Sunset - South Africa, Johannesburg October 2013',
  '/assets/images/backgrounds/about_me.jpg',
  "Not many things that beat an African sunset. I was fortunate enough to capture this sunset in my own back yard."
);

# --- !Downs

DELETE FROM BackgroundImages WHERE page = 'about_me';