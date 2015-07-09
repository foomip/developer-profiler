# Add index background image description data

# --- !Ups

INSERT INTO BackgroundImages(page, title, image_path, description)
VALUES (
  'index',
  'Anse Laszio beach, Seychelles March 2010',
  '/assets/images/backgrounds/index.jpg',
  "One of the best trips I have had. Anse Laszio is considered to be one of the most beautiful beaches in the world and it is an amazing place to go see. Put it on your bucket list if you haven't been there yet."
);

# --- !Downs

DELETE FROM BackgroundImages WHERE page = 'index';