use wikimirror;

ALTER TABLE page
  CHANGE page_id page_id INTEGER UNSIGNED AUTO_INCREMENT,
  ADD UNIQUE INDEX name_title (page_namespace,page_title),
  ADD INDEX page_random (page_random),
  ADD INDEX page_len (page_len),
  ADD INDEX page_redirect_namespace_len (page_is_redirect, page_namespace, page_len),
  ADD PRIMARY KEY (page_id);

ALTER TABLE revision 
  CHANGE rev_id rev_id INTEGER UNSIGNED AUTO_INCREMENT,
  ADD UNIQUE INDEX rev_page_id (rev_page, rev_id),
  ADD INDEX rev_timestamp (rev_timestamp),
  ADD INDEX page_timestamp (rev_page,rev_timestamp),
  ADD INDEX user_timestamp (rev_user,rev_timestamp),
  ADD INDEX usertext_timestamp (rev_user_text,rev_timestamp),
  ADD INDEX page_user_timestamp (rev_page,rev_user,rev_timestamp),
  ADD PRIMARY KEY (rev_id);

ALTER TABLE text
  CHANGE old_id old_id INTEGER UNSIGNED AUTO_INCREMENT,
  ADD PRIMARY KEY (old_id);