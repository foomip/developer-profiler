# insert default indexable pages

# --- !Ups

INSERT INTO IndexablePages
      SELECT 0 AS id, 'Home page' AS description, '' AS path, 1 AS active
UNION SELECT 1, 'About developer', 'about-me', 1
UNION SELECT 2, 'This site', 'about-site', 1

# --- !Downs

DELETE FROM IndexablePages WHERE path IN ('', 'about-me', 'about-site')