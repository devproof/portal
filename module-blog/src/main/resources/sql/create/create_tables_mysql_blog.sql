CREATE TABLE `blog` (
  `id` int(11) NOT NULL auto_increment,
  `created_at` datetime default NULL,
  `created_by` varchar(30) default NULL,
  `modified_at` datetime default NULL,
  `modified_by` varchar(30) default NULL,
  `content` text,
  `headline` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
CREATE TABLE `blog_right_xref` (
  `blog_id` int(11) NOT NULL,
  `right_id` varchar(50) NOT NULL,
  KEY `FK21039A1B6C6A8F31` (`blog_id`),
  KEY `FK21039A1BFEB2F584` (`right_id`),
  CONSTRAINT `FK21039A1BFEB2F584` FOREIGN KEY (`right_id`) REFERENCES `core_right` (`right_id`),
  CONSTRAINT `FK21039A1B6C6A8F31` FOREIGN KEY (`blog_id`) REFERENCES `blog` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
CREATE TABLE `blog_tag` (
  `tagname` varchar(255) NOT NULL,
  `created_at` datetime default NULL,
  `created_by` varchar(30) default NULL,
  `modified_at` datetime default NULL,
  `modified_by` varchar(30) default NULL,
  PRIMARY KEY  (`tagname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
CREATE TABLE `blog_tag_xref` (
  `blog_id` int(11) NOT NULL,
  `tagname` varchar(255) NOT NULL,
  KEY `FK3DB0669D6C6A8F31` (`blog_id`),
  KEY `FK3DB0669D97F1D10C` (`tagname`),
  CONSTRAINT `FK3DB0669D97F1D10C` FOREIGN KEY (`tagname`) REFERENCES `blog_tag` (`tagname`),
  CONSTRAINT `FK3DB0669D6C6A8F31` FOREIGN KEY (`blog_id`) REFERENCES `blog` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
