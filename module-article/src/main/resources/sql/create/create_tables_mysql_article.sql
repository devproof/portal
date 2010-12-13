CREATE TABLE `article` (
  `id` int(11) NOT NULL auto_increment,
  `created_at` datetime default NULL,
  `created_by` varchar(30) default NULL,
  `modified_at` datetime default NULL,
  `modified_by` varchar(30) default NULL,
  `content_id` varchar(255) default NULL,
  `teaser` text,
  `title` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `content_id` (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
CREATE TABLE `article_page` (
  `content_id` varchar(255) NOT NULL,
  `page` int(11) NOT NULL,
  `content` mediumtext,
  `article_id` int(11) NOT NULL,
  PRIMARY KEY  (`content_id`,`page`),
  KEY `FKE87DF838B0BB873F` (`article_id`),
  CONSTRAINT `FKE87DF838B0BB873F` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
CREATE TABLE `article_right_xref` (
  `article_id` int(11) NOT NULL,
  `right_id` varchar(50) NOT NULL,
  KEY `FK674F2047B0BB873F` (`article_id`),
  KEY `FK674F2047FEB2F584` (`right_id`),
  CONSTRAINT `FK674F2047FEB2F584` FOREIGN KEY (`right_id`) REFERENCES `core_right` (`right_id`) ON DELETE CASCADE,
  CONSTRAINT `FK674F2047B0BB873F` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
CREATE TABLE `article_tag` (
  `tagname` varchar(255) NOT NULL,
  `created_at` datetime default NULL,
  `created_by` varchar(30) default NULL,
  `modified_at` datetime default NULL,
  `modified_by` varchar(30) default NULL,
  PRIMARY KEY  (`tagname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
CREATE TABLE `article_tag_xref` (
  `article_id` int(11) NOT NULL,
  `tagname` varchar(255) NOT NULL,
  KEY `FKAB3F87C9B0BB873F` (`article_id`),
  KEY `FKAB3F87C987D7E7EA` (`tagname`),
  CONSTRAINT `FKAB3F87C987D7E7EA` FOREIGN KEY (`tagname`) REFERENCES `article_tag` (`tagname`),
  CONSTRAINT `FKAB3F87C9B0BB873F` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
-- since 1.1
CREATE TABLE `article_historized` (
  `id` int(11) NOT NULL auto_increment,
  `version_number` int(11) NOT NULL,
  `created_at` datetime default NULL,
  `created_by` varchar(30) default NULL,
  `modified_at` datetime default NULL,
  `modified_by` varchar(30) default NULL,
  `action` varchar(50) default NULL,
  `action_at` datetime default NULL,
  `restored_from_version` int(11) default NULL,
  `content_id` varchar(255) default NULL,
  `teaser` text,
  `title` varchar(255) default NULL,
  `full_article` mediumtext,
  `tags` text default NULL,
  `rights` text default NULL,
  `article_id` int(11) NOT NULL,
  PRIMARY KEY  (`id`),
  CONSTRAINT `FK3DB0669D97F357` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
