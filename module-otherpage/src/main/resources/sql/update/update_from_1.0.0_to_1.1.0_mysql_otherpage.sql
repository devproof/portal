alter table other_page change content content mediumtext;

CREATE TABLE `other_page_historized` (
  `id` int(11) NOT NULL auto_increment,
  `version_number` int(11) NOT NULL,
  `created_at` datetime default NULL,
  `created_by` varchar(30) default NULL,
  `modified_at` datetime default NULL,
  `modified_by` varchar(30) default NULL,
  `action` varchar(50) default NULL,
  `action_at` datetime default NULL,
  `restored_from_version` int(11) default NULL,
  `content` mediumtext,
  `content_id` varchar(255) default NULL,
  `rights` text default NULL,
  `other_page_id` int(11) NOT NULL,
  PRIMARY KEY  (`id`),
  CONSTRAINT `FK3DB056F97F3646` FOREIGN KEY (`other_page_id`) REFERENCES `other_page` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

SET FOREIGN_KEY_CHECKS=0;
UPDATE core_role_right_xref SET right_id = 'otherPage.author' WHERE right_id LIKE 'page.OtherPagePage';
UPDATE core_right SET right_id = 'otherPage.author', description = 'Other Page Author' WHERE right_id LIKE 'page.OtherPagePage';
SET FOREIGN_KEY_CHECKS=1;
