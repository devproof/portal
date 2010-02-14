CREATE TABLE `comment` (
  `id` int(11) NOT NULL auto_increment,
  `created_at` datetime  not null,
  `created_by` varchar(30)  not null,
  `modified_at` datetime  not null,
  `modified_by` varchar(30)  not null,
  `guest_name` varchar(50) default NULL,
  `guest_email` varchar(50) default NULL,
  `comment` text  not null,
  `ip_address` varchar(39)  not null,
  `number_of_blames` int(11) default 0,
  `accepted` bit(1)  not null,
  `reviewed` bit(1)  not null,
  `automatic_blocked` bit(1) not null,
  `module_name` varchar(20) not null,
  `module_content_id` varchar(20) not null,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

CREATE INDEX module_name_idx ON comment (module_name);
CREATE INDEX module_content_id_idx ON comment (module_content_id);
CREATE INDEX module_accepted_idx ON comment (accepted);
CREATE INDEX module_reviewed_idx ON comment (reviewed);
CREATE INDEX module_automatic_blocked_idx ON comment (automatic_blocked);
