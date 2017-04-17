use wiki;

SELECT p.page_title FROM revision r, page p WHERE r.rev_page = p.page_id AND r.rev_timestamp like '201511%' GROUP BY p.page_title ORDER BY count(p.page_title) DESC INTO OUTFILE '/tmp/writetrace.txt';