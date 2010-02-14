CREATE TABLE `other_page` (
  `id` int(11) NOT NULL auto_increment,
  `created_at` datetime default NULL,
  `created_by` varchar(30) default NULL,
  `modified_at` datetime default NULL,
  `modified_by` varchar(30) default NULL,
  `content` text,
  `content_id` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
CREATE TABLE `other_page_right_xref` (
  `other_id` int(11) NOT NULL,
  `right_id` varchar(50) NOT NULL,
  KEY `FKFA33F5DF61F54065` (`other_id`),
  KEY `FKFA33F5DFFEB2F584` (`right_id`),
  CONSTRAINT `FKFA33F5DFFEB2F584` FOREIGN KEY (`right_id`) REFERENCES `core_right` (`right_id`) ON DELETE CASCADE,
  CONSTRAINT `FKFA33F5DF61F54065` FOREIGN KEY (`other_id`) REFERENCES `other_page` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
