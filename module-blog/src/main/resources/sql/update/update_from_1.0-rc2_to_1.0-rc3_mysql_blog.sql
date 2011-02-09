INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('blog_entries_in_feed','Blog entries in feed','Blog','java.lang.Integer','10');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('blog_feed_title','Blog feed title','Blog','java.lang.String','Blog');

INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('blog.comment.view','Blog: View comments',null,null,null,null);
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('blog.comment.write','Blog: Write comments',null,null,null,null);

INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'blog.comment.view');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'blog.comment.write');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'blog.comment.view');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'blog.comment.write');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'blog.comment.view');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'blog.comment.write');

ALTER TABLE blog_right_xref DROP FOREIGN KEY FK21039A1BFEB2F584;
ALTER TABLE blog_right_xref ADD CONSTRAINT FK21039A1BFEB2F584 FOREIGN KEY (right_id) REFERENCES core_right (right_id) ON DELETE CASCADE;
