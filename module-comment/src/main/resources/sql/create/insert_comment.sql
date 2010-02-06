INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('comment_blamed_threshold','Number of blamed notifications to hide the comment automatically','Comments','java.lang.Integer','3');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('comment_show_real_author','Show real author name in comments','Comments','java.lang.Boolean','false');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('comment_number_per_page','Number of comments per page','Comments','java.lang.Integer','10');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('comment_number_per_page_admin','Number of comments per page for administration','Comments','java.lang.Integer','50');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('comment_show_only_reviewed','Show only reviewed comments','Comments','java.lang.Boolean','false');

INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.CommentAdminPage','Comment: Administrate comments',null,null,null,null);
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.CommentAdminPage');
