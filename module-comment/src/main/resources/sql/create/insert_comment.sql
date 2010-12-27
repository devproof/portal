INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('comment_blamed_threshold','Number of blamed notifications to hide the comment automatically','Comments','java.lang.Integer','3');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('comment_show_real_author','Show real author name in comments','Comments','java.lang.Boolean','false');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('comment_number_per_page','Number of comments per page','Comments','java.lang.Integer','10');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('comment_number_per_page_admin','Number of comments per page for administration','Comments','java.lang.Integer','50');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('comment_show_only_reviewed','Show only reviewed comments','Comments','java.lang.Boolean','false');

INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('comment.admin','Comment Administration',null,null,null,null);
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'comment.admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('comment.notify.autoblocked','Comment: Automatic blocked notification email',null,null,null,null);
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'comment.notify.autoblocked');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('comment.notify.violation','Comment: Violation notication email',null,null,null,null);
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'comment.notify.violation');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('comment.notify.newcomment','Comment: New comment notification email',null,null,null,null);
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'comment.notify.newcomment');

INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailService.findAll.subject.id.newcommentnotification','Notification email for new comments','Comments','java.lang.Integer','7');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailService.findAll.subject.id.autoblockednotification','Notification email for automatic blocked comments','Comments','java.lang.Integer','9');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailService.findAll.subject.id.violationnotification','Notification email for reported violations on comments','Comments','java.lang.Integer','8');

INSERT INTO core_email_tpl (id,created_at,created_by,modified_at,modified_by,content,subject) VALUES (7,null,null,{ts '2008-07-31 22:35:07.000'},'admin','<p>Hi,</p>
<p>there is a new comment on #PAGENAME#!</p>
<p>#COMMENT#</p>
<p>by #USERNAME#</p>
<p>Goto: #COMMENT_URL#</p>
<p>Kind regards</p>
<p>Your #PAGENAME# Team</p>','Admin: New comment');

INSERT INTO core_email_tpl (id,created_at,created_by,modified_at,modified_by,content,subject) VALUES (8,null,null,{ts '2008-07-31 22:35:07.000'},'admin','<p>Hi,</p>
<p>someone reported a violation on #PAGENAME#!</p>
<p>#COMMENT#</p>
<p>by #USERNAME#</p>
<p>Reporter IP: #REPORTER_IP# Reporting Time: #REPORTING_TIME#</p>
<p>Goto: #COMMENT_URL#</p>
<p>Kind regards</p>
<p>Your #PAGENAME# Team</p>','Admin: Comment - Reported violation');

INSERT INTO core_email_tpl (id,created_at,created_by,modified_at,modified_by,content,subject) VALUES (9,null,null,{ts '2008-07-31 22:35:07.000'},'admin','<p>Hi,</p>
<p>a comment was automatic blocked on #PAGENAME#! It reached the maximum number of reported violations.</p>
<p>#COMMENT#</p>
<p>by #USERNAME#</p>
<p>Goto: #COMMENT_URL#</p>
<p>Kind regards</p>
<p>Your #PAGENAME# Team</p>','Admin: Comment - Automatic blocked comment');