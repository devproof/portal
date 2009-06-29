INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.BlogEditPage','Blog Author: Edit blog entries',{ts '2009-01-10 21:30:01.000'},'admin',{ts '2009-01-10 21:30:01.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('blog.view','Blog: View as admin',null,null,null,null);
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('blog.view.guest','Blog: View as guest',null,null,null,null);
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('blog.view.preview','Blog: Preview',null,null,null,null);
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('blog.view.registered','Blog: View as registered user',null,null,null,null);
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.BlogPage','Blog: See the homepage/blog entries',{ts '2009-01-05 23:34:36.000'},'admin',{ts '2009-01-05 23:35:19.000'},'admin');

INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'blog.view.preview');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.BlogPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'blog.view');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'blog.view.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'blog.view.registered');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.BlogEditPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'blog.view.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'page.BlogPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'blog.view.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'blog.view.registered');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'page.BlogPage');


INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('blog_entries_per_page','Blog entries per page','Blog','java.lang.Integer','5');

INSERT INTO blog (id,created_at,created_by,modified_at,modified_by,content,headline) VALUES (1,{ts '2009-01-06 19:27:20.000'},'admin',{ts '2009-01-06 19:27:20.000'},'admin','<p>Welcome to the Devproof Portal,</p>
<p>this is a sample blog entry. You can use highlighted code:</p>
<p><textarea cols="50" rows="15" name="code" class="java">add(HeaderContributor.forJavaScript(TooltipLabel.class, "TooltipLabel.js"));
		add(HeaderContributor.forCss(TooltipLabel.class, "TooltipLabel.css"));
		tooltip.setMarkupId("tooltip");
		label.setMarkupId("label");
		WebMarkupContainer link = new WebMarkupContainer("link");</textarea></p>
<p>and [string2img size=14]string to image feature [/string2img].</p>
<p>The default users are:</p>
<ul>
<li>admin (password: admin)&nbsp; (admin rights)</li>
<li>testuser (password: testuser)&nbsp; (registered user rights)</li>
</ul>','Welcome to Devproof Portal');



INSERT INTO blog_right_xref (blog_id,right_id) VALUES (1,'blog.view.guest');
INSERT INTO blog_right_xref (blog_id,right_id) VALUES (1,'blog.view.registered');
INSERT INTO blog_right_xref (blog_id,right_id) VALUES (1,'blog.view.preview');