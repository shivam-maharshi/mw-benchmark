// Don't drop indexes for edit apis to work!

use wiki;

ALTER TABLE page
  CHANGE page_id page_id INTEGER UNSIGNED NOT NULL,
  DROP INDEX name_title,
  DROP INDEX page_random,
  DROP INDEX page_len,
  DROP INDEX page_redirect_namespace_len,
  DROP PRIMARY KEY;

ALTER TABLE revision 
  CHANGE rev_id rev_id INTEGER UNSIGNED NOT NULL,
  DROP INDEX rev_page_id,
  DROP INDEX rev_timestamp,
  DROP INDEX page_timestamp,
  DROP INDEX user_timestamp,
  DROP INDEX usertext_timestamp,
  DROP INDEX page_user_timestamp,
  DROP PRIMARY KEY;

ALTER TABLE text
  CHANGE old_id old_id INTEGER UNSIGNED NOT NULL,
  DROP PRIMARY KEY;
