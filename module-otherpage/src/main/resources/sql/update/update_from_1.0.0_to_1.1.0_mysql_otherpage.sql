alter table other_page change content content mediumtext;

-- since 1.1
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
  `rights` text default NULL,
  `other_page_id` int(11) NOT NULL,
  PRIMARY KEY  (`id`),
  CONSTRAINT `FK3DB056F97F3646` FOREIGN KEY (`other_page_id`) REFERENCES `other_page` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

-- since 1.1
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.OtherPageHistoryPage','Other Page Administration: History',null,null,null,null);
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.OtherPageHistoryPage');