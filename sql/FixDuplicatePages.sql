use wiki;

CREATE TABLE bad_page AS 
  SELECT page_namespace, page_title, COUNT(*) AS count
  FROM page GROUP BY page_namespace, page_title
  HAVING count > 1;
  
UPDATE page, bad_page
  SET page.page_title = CONCAT(page.page_title, page.page_id)
  WHERE page.page_namespace = bad_page.page_namespace AND page.page_title = bad_page.page_title;
DROP TABLE bad_page;