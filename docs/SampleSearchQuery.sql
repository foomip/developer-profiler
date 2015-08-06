SELECT 
	A.IndexablePageId,
	A.totalPages,
	B.hits,
	CASE WHEN B.hits IS NULL THEN 0 ELSE CAST(B.hits AS FLOAT) / A.totalPages END AS score
FROM (
   SELECT COUNT(indexablePageId) AS totalPages, indexablePageId
   FROM PageWords
   GROUP BY indexablePageId
) AS A LEFT JOIN (
  SELECT COUNT(indexablePageId) AS hits, indexablePageId 
  FROM PageWords WHERE word in ("tech", "artistic", "development") 
  GROUP BY indexablePageId
) AS B ON A.indexablePageId = B.indexablePageId