CREATE TABLE `core_box` (
  `id` int(11) NOT NULL auto_increment,
  `created_at` datetime default NULL,
  `created_by` varchar(30) default NULL,
  `modified_at` datetime default NULL,
  `modified_by` varchar(30) default NULL,
  `box_type` varchar(255) default NULL,
  `content` text,
  `sort` int(11) default NULL,
  `hide_title` bit(1) NOT NULL,
  `title` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
CREATE TABLE `core_configuration` (
  `conf_key` varchar(255) NOT NULL,
  `conf_description` varchar(255) default NULL,
  `conf_group` varchar(255) default NULL,
  `conf_type` varchar(255) default NULL,
  `conf_value` varchar(255) NOT NULL,
  PRIMARY KEY  (`conf_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
CREATE TABLE `core_email_tpl` (
  `id` int(11) NOT NULL auto_increment,
  `created_at` datetime default NULL,
  `created_by` varchar(30) default NULL,
  `modified_at` datetime default NULL,
  `modified_by` varchar(30) default NULL,
  `content` text,
  `subject` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
CREATE TABLE `core_right` (
  `right_id` varchar(50) NOT NULL,
  `created_at` datetime default NULL,
  `created_by` varchar(30) default NULL,
  `modified_at` datetime default NULL,
  `modified_by` varchar(30) default NULL,
  `description` varchar(255) default NULL,
  PRIMARY KEY  (`right_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
CREATE TABLE `core_role` (
  `id` int(11) NOT NULL auto_increment,
  `created_at` datetime default NULL,
  `created_by` varchar(30) default NULL,
  `modified_at` datetime default NULL,
  `modified_by` varchar(30) default NULL,
  `active` bit(1) NOT NULL,
  `description` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
CREATE TABLE `core_role_right_xref` (
  `role_id` int(11) NOT NULL,
  `right_id` varchar(50) NOT NULL,
  KEY `FK5AFA2427E35D2FF0` (`role_id`),
  KEY `FK5AFA2427FEB2F584` (`right_id`),
  CONSTRAINT `FK5AFA2427FEB2F584` FOREIGN KEY (`right_id`) REFERENCES `core_right` (`right_id`),
  CONSTRAINT `FK5AFA2427E35D2FF0` FOREIGN KEY (`role_id`) REFERENCES `core_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;
CREATE TABLE `core_user` (
  `id` int(11) NOT NULL auto_increment,
  `active` bit(1) NOT NULL,
  `birthday` datetime default NULL,
  `changed_at` datetime default NULL,
  `confirm_app_at` datetime default NULL,
  `confirmation_code` varchar(255) default NULL,
  `confirm_req_at` datetime default NULL,
  `confirmed` bit(1) NOT NULL,
  `email` varchar(100) NOT NULL,
  `enable_contact_form` bit(1) default NULL,
  `firstname` varchar(100) default NULL,
  `forgot_code` varchar(255) default NULL,
  `last_ip` varchar(39) default NULL,
  `last_login_at` datetime default NULL,
  `lastname` varchar(100) default NULL,
  `password` varchar(255) NOT NULL,
  `reg_date` datetime NOT NULL,
  `session_id` varchar(255) default NULL,
  `username` varchar(30) default NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `FKA16AE06BE35D2FF0` (`role_id`),
  CONSTRAINT `FKA16AE06BE35D2FF0` FOREIGN KEY (`role_id`) REFERENCES `core_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;


CREATE TABLE `core_module_link` (
  `link_type` int(11) NOT NULL,
  `page_name` varchar(255) NOT NULL,
  `created_at` datetime default NULL,
  `created_by` varchar(30) default NULL,
  `modified_at` datetime default NULL,
  `modified_by` varchar(30) default NULL,
  `module_name` varchar(255) NOT NULL,
  `sort` int(11) NOT NULL,
  `visible` bit(1) NOT NULL,
  PRIMARY KEY  (`link_type`,`page_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;


CREATE TABLE `core_mount_point` (
  `id` int(11) NOT NULL auto_increment,
  `mount_path` varchar(255) NOT NULL, -- index
  `related_content_id` varchar(255), -- index
  `handler_key` varchar(255) NOT NULL, -- index
  `sort` int(11) NOT NULL,
--   `created_at` datetime default NULL,
--   `created_by` varchar(30) default NULL,
--   `modified_at` datetime default NULL,
--   `modified_by` varchar(30) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `mount_path` (`mount_path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;