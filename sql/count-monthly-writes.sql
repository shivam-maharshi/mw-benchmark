use wiki;

SELECT count(*) FROM revision r, page p WHERE r.rev_page = p.page_id AND r.rev_timestamp like '201511%';