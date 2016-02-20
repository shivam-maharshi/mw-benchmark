use wiki;

SELECT rev_page, rev_len, rev_timestamp FROM revision r WHERE r.rev_timestamp like '201510%' ORDER BY r.rev_page ASC, r.rev_timestamp ASC INTO OUTFILE '/tmp/rev_data_size.txt' FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n';