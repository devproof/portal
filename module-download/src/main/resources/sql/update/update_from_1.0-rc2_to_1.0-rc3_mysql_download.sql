INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('download_entries_in_feed','Download entries in feed','Downloads','java.lang.Integer','10');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('download_feed_title','Download feed title','Downloads','java.lang.String','Downloads');

ALTER TABLE download_right_xref DROP FOREIGN KEY FK24629575FEB2F584;
ALTER TABLE download_right_xref ADD CONSTRAINT `FK24629575FEB2F584` FOREIGN KEY (`right_id`) REFERENCES `core_right` (`right_id`) ON DELETE CASCADE;