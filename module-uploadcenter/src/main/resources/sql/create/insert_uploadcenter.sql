INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('uploadcenter.author','Upload Center Author',{ts '2008-12-15 17:58:02.000'},'admin',{ts '2009-01-10 21:26:21.000'},'admin');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'uploadcenter.author');

INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('uploadcenter_maxfiles','Maximum number of files','Upload Center','java.lang.Integer','10');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('uploadcenter_maxsize','Maximum size of file (in KB)','Upload Center','java.lang.Integer','100000');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('uploadcenter_folder','Folder to store files','Upload Center','java.lang.String','java.io.tmpdir');
