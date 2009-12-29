CREATE TABLE `comments` (
  `id` int(11) NOT NULL auto_increment,
  `created_at` datetime default NULL,
  `created_by` varchar(30) default NULL,
  `modified_at` datetime default NULL,
  `modified_by` varchar(30) default NULL,
  `comment` text,
  `ip_address` varchar(15) default NULL,
  `number_of_blames` int(11) default NULL,
  `reviewed` bit(1) default NULL,
  `visible` bit(1) default NULL,
  `module_name` varchar(20) default NULL,
  `module_content_id` varchar(20) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

CREATE INDEX module_name_idx ON comments (module_name);
CREATE INDEX module_content_id_idx ON comments (module_content_id);
