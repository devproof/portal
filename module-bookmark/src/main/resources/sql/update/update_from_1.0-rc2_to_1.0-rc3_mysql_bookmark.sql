INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('bookmark_entries_in_feed','Bookmark entries in feed','Bookmarks','java.lang.Integer','10');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('bookmark_feed_title','Bookmark feed title','Bookmarks','java.lang.String','Bookmarks');

ALTER TABLE bookmark_right_xref DROP FOREIGN KEY FK6B52A007FEB2F584;
ALTER TABLE bookmark_right_xref ADD CONSTRAINT `FK6B52A007FEB2F584` FOREIGN KEY (`right_id`) REFERENCES `core_right` (`right_id`) ON DELETE CASCADE;