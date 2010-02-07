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

INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('comment_blamed_threshold','Number of blamed notifications to hide the comment automatically','Comments','java.lang.Integer','3');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('comment_show_real_author','Show real author name in comments','Comments','java.lang.Boolean','false');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('comment_number_per_page','Number of comments per page','Comments','java.lang.Integer','10');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('comment_number_per_page_admin','Number of comments per page for administration','Comments','java.lang.Integer','50');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('comment_show_only_reviewed','Show only reviewed comments','Comments','java.lang.Boolean','false');

INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.CommentAdminPage','Comment: Administrate comments',null,null,null,null);
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.CommentAdminPage');



INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailTemplateDao.findAll.subject.id.newcommentnotification','Notification email for new comments','Comments','java.lang.Integer','15');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailTemplateDao.findAll.subject.id.autoblockednotification','Notification email for automatic blocked comments','Comments','java.lang.Integer','17');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailTemplateDao.findAll.subject.id.violationnotification','Notification email for reported violations on comments','Comments','java.lang.Integer','16');

INSERT INTO core_email_tpl (id,created_at,created_by,modified_at,modified_by,content,subject) VALUES (15,null,null,{ts '2008-07-31 22:35:07.000'},'admin','<p>Hi,</p>
\n
<p>there is a new comment on #PAGENAME#!</p>
\n
<p>#COMMENT#</p>
\n
<p>by #USERNAME#</p>
\n
<p>Goto: #COMMENTURL#</p>
\n
<p>Kind regards</p>
\n
<p>Your #PAGENAME# Team</p>','Admin: New comment');

INSERT INTO core_email_tpl (id,created_at,created_by,modified_at,modified_by,content,subject) VALUES (16,null,null,{ts '2008-07-31 22:35:07.000'},'admin','<p>Hi,</p>
\n
<p>someone reported a violation on #PAGENAME#!</p>
\n
<p>#COMMENT#</p>
\n
<p>by #USERNAME#</p>
\n
<p>Reporter IP: {REPORTER_IP} Reporting Time: {REPORTING_TIME}</p>
\n
<p>Goto: #COMMENTURL#</p>
\n
<p>Kind regards</p>
\n
<p>Your #PAGENAME# Team</p>','Admin: Comment - Reported violation');

INSERT INTO core_email_tpl (id,created_at,created_by,modified_at,modified_by,content,subject) VALUES (17,null,null,{ts '2008-07-31 22:35:07.000'},'admin','<p>Hi,</p>
\n
<p>a comment was automatic blocked on #PAGENAME#! It reached the maximum number of reported violations.</p>
\n
<p>#COMMENT#</p>
\n
<p>by #USERNAME#</p>
\n
<p>Goto: #COMMENTURL#</p>
\n
<p>Kind regards</p>
\n
<p>Your #PAGENAME# Team</p>','Admin: Comment - Automatic blocked comment');