use wiki;

DELETE FROM page; DELETE FROM text; DELETE FROM revision;
ALTER TABLE category ADD cat_hidden tinyint unsigned NOT NULL default 0;
ALTER TABLE site_stats ADD ss_admins int default '-1';
ALTER TABLE recentchanges ADD rc_moved_to_title varchar(255) binary NOT NULL default '';
ALTER TABLE recentchanges ADD rc_cur_time varbinary(14) NOT NULL default '';
ALTER TABLE site_stats ADD ss_total_views bigint unsigned default 0;
ALTER TABLE page ADD page_counter bigint unsigned NOT NULL default 0;