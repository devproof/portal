CREATE TABLE `download` (
  `id` int(11) NOT NULL auto_increment,
  `created_at` datetime default NULL,
  `created_by` varchar(30) default NULL,
  `modified_at` datetime default NULL,
  `modified_by` varchar(30) default NULL,
  `broken` bit(1) default NULL,
  `description` text,
  `hits` int(11) default NULL,
  `number_of_votes` int(11) default NULL,
  `sum_of_rating` int(11) default NULL,
  `title` varchar(255) default NULL,
  `url` varchar(255) default NULL,
  `download_size` varchar(255) default NULL,
  `licence` varchar(255) default NULL,
  `manufacturer` varchar(255) default NULL,
  `manufacturer_homepage` varchar(255) default NULL,
  `price` varchar(255) default NULL,
  `software_version` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
CREATE TABLE `download_right_xref` (
  `download_id` int(11) NOT NULL,
  `right_id` varchar(50) NOT NULL,
  KEY `FK24629575D8DA64D7` (`download_id`),
  KEY `FK24629575FEB2F584` (`right_id`),
  CONSTRAINT `FK24629575FEB2F584` FOREIGN KEY (`right_id`) REFERENCES `core_right` (`right_id`) ON DELETE CASCADE,
  CONSTRAINT `FK24629575D8DA64D7` FOREIGN KEY (`download_id`) REFERENCES `download` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
CREATE TABLE `download_tag` (
  `tagname` varchar(255) NOT NULL,
  `created_at` datetime default NULL,
  `created_by` varchar(30) default NULL,
  `modified_at` datetime default NULL,
  `modified_by` varchar(30) default NULL,
  PRIMARY KEY  (`tagname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
CREATE TABLE `download_tag_xref` (
  `download_id` int(11) NOT NULL,
  `tagname` varchar(255) NOT NULL,
  KEY `FKC76A7077D8DA64D7` (`download_id`),
  KEY `FKC76A7077544D7D40` (`tagname`),
  CONSTRAINT `FKC76A7077544D7D40` FOREIGN KEY (`tagname`) REFERENCES `download_tag` (`tagname`),
  CONSTRAINT `FKC76A7077D8DA64D7` FOREIGN KEY (`download_id`) REFERENCES `download` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
