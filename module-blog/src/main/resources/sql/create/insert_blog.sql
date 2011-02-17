INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('blog.author','Blog Author',{ts '2009-01-10 21:30:01.000'},'admin',{ts '2009-01-10 21:30:01.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('blog.view','Blog: View as admin',null,null,null,null);
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('blog.view.guest','Blog: View as guest',null,null,null,null);
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('blog.view.preview','Blog: Preview',null,null,null,null);
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('blog.view.registered','Blog: View as registered user',null,null,null,null);

INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'blog.view.preview');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'blog.view');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'blog.view.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'blog.view.registered');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'blog.author');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'blog.view.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'blog.view.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'blog.view.registered');


INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('blog_entries_per_page','Blog entries per page','Blog','java.lang.Integer','5');

INSERT INTO blog (id,created_at,created_by,modified_at,modified_by,content,headline) VALUES (1,{ts '2011-02-18 19:27:20.000'},'admin',{ts '2011-02-18 19:27:20.000'},'admin','<p>Congratulation. Devproof Portal is running.</p>
<p>The default users are:</p>
<ul>
<li>admin (password: admin)&nbsp; (admin rights)</li>
<li>testuser (password: testuser)&nbsp; (registered user rights)</li>
</ul>
<p>This is a sample blog entry. You can use highlighted code:</p>
<pre class="brush: java;">DevproofPortal portal = new DevproofPortal();
portal.release();
</pre><p/>
<p>[string2img size=14] and the string to image feature [/string2img].</p>
','Welcome to Devproof Portal');

INSERT INTO blog_tag (tagname,created_at, created_by, modified_at, modified_by) VALUES ('devproof',{ts '2011-02-26 19:27:20.000'},'admin',{ts '2011-02-26 19:27:20.000'},'admin');
INSERT INTO blog_tag (tagname,created_at, created_by, modified_at, modified_by) VALUES ('portal',{ts '2011-02-26 19:27:20.000'},'admin',{ts '2011-02-26 19:27:20.000'},'admin');
INSERT INTO blog_tag (tagname,created_at, created_by, modified_at, modified_by) VALUES ('welcome',{ts '2011-02-26 19:27:20.000'},'admin',{ts '2011-02-26 19:27:20.000'},'admin');

INSERT INTO blog_tag_xref (blog_id, tagname) VALUES (1, 'devproof');
INSERT INTO blog_tag_xref (blog_id, tagname) VALUES (1, 'portal');
INSERT INTO blog_tag_xref (blog_id, tagname) VALUES (1, 'welcome');

INSERT INTO blog_right_xref (blog_id,right_id) VALUES (1,'blog.view.guest');
INSERT INTO blog_right_xref (blog_id,right_id) VALUES (1,'blog.view.registered');
INSERT INTO blog_right_xref (blog_id,right_id) VALUES (1,'blog.view.preview');

-- since 1.0-rc3
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

