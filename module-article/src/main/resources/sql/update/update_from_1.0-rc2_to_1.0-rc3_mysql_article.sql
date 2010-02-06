INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('article_entries_in_feed','Article entries in feed','Articles','java.lang.Integer','10');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('article_feed_title','Article feed title','Articles','java.lang.String','Articles');

ALTER TABLE article_right_xref DROP FOREIGN KEY FK674F2047FEB2F584;
ALTER TABLE article_right_xref ADD CONSTRAINT `FK674F2047FEB2F584` FOREIGN KEY (`right_id`) REFERENCES `core_right` (`right_id`) ON DELETE CASCADE;