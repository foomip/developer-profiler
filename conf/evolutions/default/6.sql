# Add index background image description data

# --- !Ups

INSERT INTO BackgroundImages(page, title, image_path, description)
VALUES (
  'about_site',
  'Mosteiro da Batalha, in district of Leiria, in the Centro Region region of Portugal - July 2011',
  '/assets/images/backgrounds/about_site.jpg',
  "The name literally translates to Monastery of the Battle. Construction was started in 1385 and work continued into the reign of John III of Portugal with the addition of the fine Renaissance tribune (1532) by João de Castilho. Construction was never completed."
);

# --- !Downs

DELETE FROM BackgroundImages WHERE page = 'index';