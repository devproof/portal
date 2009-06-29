INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('otherPage.view','Other Page: View as admin',{ts '2009-01-05 18:26:38.000'},'admin',{ts '2009-01-05 18:26:38.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('otherPage.view.guest','Other Page: View as guest',{ts '2009-01-05 18:27:52.000'},'admin',{ts '2009-01-05 18:27:52.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('otherPage.view.preview','Other Page: Preview',{ts '2009-01-05 18:28:54.000'},'admin',{ts '2009-01-05 18:28:54.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('otherPage.view.registered','Other Page: View as registered user',{ts '2009-01-05 18:28:19.000'},'admin',{ts '2009-01-05 18:28:19.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.OtherPagePage','Other Page Administration: Edit other pages',{ts '2009-01-05 20:28:31.000'},'admin',{ts '2009-01-10 21:33:42.000'},'admin');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'otherPage.view.preview');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'otherPage.view');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'otherPage.view.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'otherPage.view.registered');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.OtherPagePage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'otherPage.view.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'otherPage.view.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'otherPage.view.registered');

INSERT INTO other_page (id,created_at,created_by,modified_at,modified_by,content,content_id) VALUES (1,{ts '2009-01-06 19:40:23.000'},'admin',{ts '2009-01-06 19:40:23.000'},'admin','<p>Sample about page is an "Other Page"</p>','about');
INSERT INTO other_page_right_xref (other_id,right_id) VALUES (1,'otherPage.view.preview');
INSERT INTO other_page_right_xref (other_id,right_id) VALUES (1,'otherPage.view.guest');
INSERT INTO other_page_right_xref (other_id,right_id) VALUES (1,'otherPage.view.registered');
