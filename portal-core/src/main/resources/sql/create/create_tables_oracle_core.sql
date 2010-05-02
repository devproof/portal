CREATE SEQUENCE HIBERNATE_SEQUENCE START WITH 10000;

CREATE TABLE "CORE_BOX"
 (	"ID" NUMBER(10,0) NOT NULL ENABLE,
  "CREATED_AT" TIMESTAMP (6),
  "CREATED_BY" VARCHAR2(30 CHAR),
  "MODIFIED_AT" TIMESTAMP (6),
  "MODIFIED_BY" VARCHAR2(30 CHAR),
  "BOX_TYPE" VARCHAR2(255 CHAR),
  "CONTENT" CLOB,
  "HIDE_TITLE" NUMBER(1,0) NOT NULL ENABLE,
  "SORT" NUMBER(10,0),
  "TITLE" VARCHAR2(255 CHAR),
   PRIMARY KEY ("ID") ENABLE
 );


CREATE TABLE "CORE_CONFIGURATION"
 (	"CONF_KEY" VARCHAR2(255 CHAR) NOT NULL ENABLE,
  "CONF_DESCRIPTION" VARCHAR2(255 CHAR),
  "CONF_GROUP" VARCHAR2(255 CHAR),
  "CONF_TYPE" VARCHAR2(255 CHAR),
  "CONF_VALUE" VARCHAR2(255 CHAR) NOT NULL ENABLE,
   PRIMARY KEY ("CONF_KEY") ENABLE
 );


CREATE TABLE "CORE_EMAIL_TPL"
 (	"ID" NUMBER(10,0) NOT NULL ENABLE,
  "CREATED_AT" TIMESTAMP (6),
  "CREATED_BY" VARCHAR2(30 CHAR),
  "MODIFIED_AT" TIMESTAMP (6),
  "MODIFIED_BY" VARCHAR2(30 CHAR),
  "CONTENT" CLOB,
  "SUBJECT" VARCHAR2(255 CHAR),
   PRIMARY KEY ("ID") ENABLE
 );


CREATE TABLE "CORE_MODULE_LINK"
 (	"LINK_TYPE" NUMBER(10,0) NOT NULL ENABLE,
  "PAGE_NAME" VARCHAR2(255 CHAR) NOT NULL ENABLE,
  "CREATED_AT" TIMESTAMP (6),
  "CREATED_BY" VARCHAR2(30 CHAR),
  "MODIFIED_AT" TIMESTAMP (6),
  "MODIFIED_BY" VARCHAR2(30 CHAR),
  "MODULE_NAME" VARCHAR2(255 CHAR) NOT NULL ENABLE,
  "SORT" NUMBER(10,0) NOT NULL ENABLE,
  "VISIBLE" NUMBER(1,0) NOT NULL ENABLE,
   PRIMARY KEY ("LINK_TYPE", "PAGE_NAME") ENABLE
 );


CREATE TABLE "CORE_RIGHT"
 (	"RIGHT_ID" VARCHAR2(50 CHAR) NOT NULL ENABLE,
  "CREATED_AT" TIMESTAMP (6),
  "CREATED_BY" VARCHAR2(30 CHAR),
  "MODIFIED_AT" TIMESTAMP (6),
  "MODIFIED_BY" VARCHAR2(30 CHAR),
  "DESCRIPTION" VARCHAR2(255 CHAR),
   PRIMARY KEY ("RIGHT_ID") ENABLE
 );


CREATE TABLE "CORE_ROLE"
 (	"ID" NUMBER(10,0) NOT NULL ENABLE,
  "CREATED_AT" TIMESTAMP (6),
  "CREATED_BY" VARCHAR2(30 CHAR),
  "MODIFIED_AT" TIMESTAMP (6),
  "MODIFIED_BY" VARCHAR2(30 CHAR),
  "ACTIVE" NUMBER(1,0) NOT NULL ENABLE,
  "DESCRIPTION" VARCHAR2(255 CHAR),
   PRIMARY KEY ("ID") ENABLE
 );


CREATE TABLE "CORE_ROLE_RIGHT_XREF"
 (	"ROLE_ID" NUMBER(10,0) NOT NULL ENABLE,
  "RIGHT_ID" VARCHAR2(50 CHAR) NOT NULL ENABLE,
   CONSTRAINT "FK5AFA2427E35D2FF0" FOREIGN KEY ("ROLE_ID")
    REFERENCES "CORE_ROLE" ("ID") ENABLE,
   CONSTRAINT "FK5AFA2427FEB2F584" FOREIGN KEY ("RIGHT_ID")
    REFERENCES "CORE_RIGHT" ("RIGHT_ID") ENABLE
 );


CREATE TABLE "CORE_USER"
 (	"ID" NUMBER(10,0) NOT NULL ENABLE,
  "ACTIVE" NUMBER(1,0) NOT NULL ENABLE,
  "BIRTHDAY" TIMESTAMP (6),
  "CHANGED_AT" TIMESTAMP (6),
  "CONFIRM_APP_AT" TIMESTAMP (6),
  "CONFIRMATION_CODE" VARCHAR2(255 CHAR),
  "CONFIRM_REQ_AT" TIMESTAMP (6),
  "CONFIRMED" NUMBER(1,0) NOT NULL ENABLE,
  "EMAIL" VARCHAR2(100 CHAR) NOT NULL ENABLE,
  "ENABLE_CONTACT_FORM" NUMBER(1,0),
  "PASSWORD" VARCHAR2(255 CHAR) NOT NULL ENABLE,
  "FIRSTNAME" VARCHAR2(100 CHAR),
  "FORGOT_CODE" VARCHAR2(255 CHAR),
  "LAST_IP" VARCHAR2(39 CHAR),
  "LAST_LOGIN_AT" TIMESTAMP (6),
  "LASTNAME" VARCHAR2(100 CHAR),
  "REG_DATE" TIMESTAMP (6) NOT NULL ENABLE,
  "SESSION_ID" VARCHAR2(255 CHAR),
  "USERNAME" VARCHAR2(30 CHAR),
  "ROLE_ID" NUMBER(10,0) NOT NULL ENABLE,
   PRIMARY KEY ("ID") ENABLE,
   UNIQUE ("USERNAME") ENABLE,
   CONSTRAINT "FKA16AE06BE35D2FF0" FOREIGN KEY ("ROLE_ID")
    REFERENCES "CORE_ROLE" ("ID") ENABLE
 );





INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('contact.form.enable','Contact Form: Enables the contact form',{ts '2009-01-05 23:16:52.000'},'admin',{ts '2009-01-05 23:16:52.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('emailnotification.registered.user','Email notification: New registered user',null,null,null,null);
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('emailnotification.unknown.application.error','Email notification: Unknown application error',{ts '2009-01-07 08:12:44.000'},'admin',{ts '2009-01-07 08:12:44.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('general.GlobalAdminBoxPanel','See Box: Global Administration',{ts '2009-01-05 14:13:07.000'},'admin',{ts '2009-01-05 14:13:07.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('general.LoginBoxPanel','See Box: Login box',{ts '2009-01-05 14:21:46.000'},'admin',{ts '2009-01-05 14:21:46.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('general.OtherBoxPanel','See Box: Other boxes',{ts '2009-01-05 14:22:23.000'},'admin',{ts '2009-01-05 14:22:23.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('general.PageAdminBoxPanel','See Box: Page Administration',{ts '2009-01-05 14:13:15.000'},'admin',{ts '2009-01-05 14:13:15.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('general.TagCloudBoxPanel','See Box: Related tags box',{ts '2009-01-05 14:23:13.000'},'admin',{ts '2009-01-05 14:23:13.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('general.UserBoxPanel','See Box: User info box (Settings/Logout)',{ts '2009-01-05 14:24:02.000'},'admin',{ts '2009-01-05 14:24:02.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.BoxPage','Box Administration: Administrate boxes',{ts '2009-01-05 14:11:43.000'},'admin',{ts '2009-01-10 21:28:18.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.ConfigurationPage','Configuration: Administrate configuration',{ts '2009-01-10 21:28:45.000'},'admin',{ts '2009-01-10 21:28:45.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.ContactPage','Contact Form: User who are allowed to access the contact form',{ts '2009-01-05 23:01:39.000'},'admin',{ts '2009-01-06 15:59:04.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.EmailTemplatePage','Email Template Administration: Administrate email templates',{ts '2009-01-10 21:24:57.000'},'admin',{ts '2009-01-10 21:24:57.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.ForgotPasswordPage','Forgot Password: Enables the forgot password page',{ts '2009-01-05 23:29:26.000'},'admin',{ts '2009-01-06 17:02:37.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.RegisterPage','Registration: Register an account',{ts '2009-01-05 23:27:42.000'},'admin',{ts '2009-01-06 17:02:28.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.RightPage','Right Administration: Administrate rights',{ts '2009-01-10 21:23:44.000'},'admin',{ts '2009-01-10 21:23:44.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.RolePage','Role Administration: Administrate roles',{ts '2009-01-10 21:23:01.000'},'admin',{ts '2009-01-10 21:24:11.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.SettingsPage','User Settings: Edit his own settings',null,null,null,null);
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.UserPage','User Administration: Administrate users',{ts '2009-01-10 21:21:23.000'},'admin',{ts '2009-01-10 21:21:53.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.ThemePage','Theme Administration: Manage themes',{ts '2009-01-10 21:21:23.000'},'admin',{ts '2009-01-10 21:21:53.000'},'admin');
INSERT INTO core_right (right_id,created_at,created_by,modified_at,modified_by,description) VALUES ('page.ModuleLinkPage',{ts '2009-05-10 00:30:24.000'},'admin',{ts '2009-05-10 00:30:24.000'},'admin','Module Administration: Module Navigation Links');
INSERT INTO core_right (right_id,created_at,created_by,modified_at,modified_by,description) VALUES ('page.ModuleOverviewPage',{ts '2009-05-10 00:29:41.000'},'admin',{ts '2009-05-10 00:29:41.000'},'admin','Module Administration: Module Overview');

INSERT INTO core_right (right_id,created_at,created_by,modified_at,modified_by,description) VALUES ('captcha.disabled',{ts '2009-05-10 00:29:41.000'},'admin',{ts '2009-05-10 00:29:41.000'},'admin','Disables all captchas');

INSERT INTO core_role (id,active,description,created_at,created_by,modified_at,modified_by) VALUES (1,1,'Admin',{ts '2008-12-26 04:03:10.000'},'admin',{ts '2009-01-04 21:47:37.000'},'admin');
INSERT INTO core_role (id,active,description,created_at,created_by,modified_at,modified_by) VALUES (2,1,'Guest',{ts '2009-01-05 15:36:51.000'},'admin',{ts '2009-01-05 15:36:51.000'},'admin');
INSERT INTO core_role (id,active,description,created_at,created_by,modified_at,modified_by) VALUES (3,1,'Registered user',null,null,null,null);


INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'contact.form.enable');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.ContactPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'emailnotification.registered.user');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.ForgotPasswordPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.RegisterPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'general.GlobalAdminBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'general.OtherBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'general.PageAdminBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'general.TagCloudBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'general.UserBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.SettingsPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.UserPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.ThemePage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.RightPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.RolePage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.EmailTemplatePage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.BoxPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.ConfigurationPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'general.LoginBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'general.OtherBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'general.TagCloudBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'general.UserBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'page.RegisterPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'page.ForgotPasswordPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'page.SettingsPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'general.OtherBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'general.TagCloudBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'general.UserBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'page.ContactPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'page.RegisterPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'page.ForgotPasswordPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.ModuleLinkPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.ModuleOverviewPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'captcha.disabled');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'captcha.disabled');



INSERT INTO core_user (id,active,birthday,changed_at,confirm_app_at,confirmation_code,confirm_req_at,confirmed,email,firstname,forgot_code,last_ip,last_login_at,lastname,password,reg_date,session_id,username,role_id,enable_contact_form) VALUES (2,1,{ts '2008-08-09 00:00:00.000'},{ts '2009-01-05 23:18:13.000'},{ts '2008-08-10 16:35:37.000'},'bfda0f177e0b56113118131fe696412',{ts '2008-08-10 16:55:20.000'},1,'your@email.de','admin',null,'62.216.221.209',{ts '2009-01-06 17:21:56.000'},'admin','21232f297a57a5a743894a0e4a801fc3',{ts '2008-07-25 15:25:29.000'},'d4212b818096297975e5c59b738a7a98','admin',1,1);
INSERT INTO core_user (id,active,birthday,changed_at,confirm_app_at,confirmation_code,confirm_req_at,confirmed,email,firstname,forgot_code,last_ip,last_login_at,lastname,password,reg_date,session_id,username,role_id,enable_contact_form) VALUES (9,1,{ts '2009-01-05 00:00:00.000'},{ts '2009-01-05 23:26:40.000'},null,null,null,1,'email@test.de','test',null,'127.0.0.1',{ts '2009-01-06 15:37:10.000'},'user','5d9c68c6c50ed3d02a2fcf54f63993b6',{ts '2009-01-05 23:25:59.000'},'c73ff2a18ca36b9304d93dca157b521c','testuser',3,1);


INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('show_real_author','Show real author name','General','java.lang.Boolean','true');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('show_modified_by','Show modified by','General','java.lang.Boolean','true');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('show_modified_at_as_created_at','Show modified at as created at','General','java.lang.Boolean','false');

INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('input_date_format','Default input date format','General','java.lang.String','yyyy-MM-dd');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('input_date_time_format','Default input date time format','General','java.lang.String','yyyy-MM-dd HH:mm');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('display_date_format','Default display date format','General','java.lang.String','EEEEE, MMMMM dd, yyyy');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('display_date_time_format','Default display date time format','General','java.lang.String','EEEEE, MMMMM dd, yyyy HH:mm');

INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('email_validation','Enable email validation for registration','User','java.lang.Boolean','true');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('page_title','Page title','General','java.lang.String','devproof.org');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('copyright_owner','Copyright owner','General','java.lang.String','devproof.org Copyright 2010');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('footer_content','Footer content','General','java.lang.String','&copy; 2010 - www.devproof.org');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('from_email_address','From email address','Email','java.lang.String','your@email.com');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('from_email_name','From email name','Email','java.lang.String','devproof.org');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('page_name','Page name','General','java.lang.String','devproof.org');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('registration_required_birthday','Require birthday for registration','User','java.lang.Boolean','true');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('registration_required_name','Require first and lastname for registration','User','java.lang.Boolean','true');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.fontService.findAllSystemFonts.name.name.string2image','String to image font','General','java.lang.String','Times New Roman');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailTemplateDao.findAll.subject.id.forgotemail','Forgot your password email','Email','java.lang.Integer','2');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailTemplateDao.findAll.subject.id.reconfirmemail','Reconfirmation email, when email was changed','Email','java.lang.Integer','5');

INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailTemplateDao.findAll.subject.id.contactformemail','Contact form email template','Email','java.lang.Integer','6');

INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailTemplateDao.findAll.subject.id.regemail','Registration email','Email','java.lang.Integer','1');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailTemplateDao.findAll.subject.id.registereduser','Notification: A new user has been registered','Email','java.lang.Integer','3');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailTemplateDao.findAll.subject.id.unknownerror','Notification: Unknown error email','Email','java.lang.Integer','4');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.roleDao.findAll.description.id.guestrole','Default guest role','User','java.lang.Integer','2');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.roleDao.findAll.description.id.registerrole','Default role for registration','User','java.lang.Integer','3');

INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('box_num_tags','Number of related tags','Tags','java.lang.Integer','10');

INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('google_analytics_enabled','Google Analytics enabled','Google Analytics','java.lang.Boolean','false');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('google_webproperty_id','Google Web Property-ID','Google Analytics','java.lang.String','empty');

INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('hidden.selected_theme_uuid','Selected theme','hidden','java.lang.String','_default_');


INSERT INTO core_email_tpl (id,created_at,created_by,modified_at,modified_by,content,subject) VALUES (1,{ts '2009-01-05 15:32:07.000'},'admin',{ts '2009-01-05 15:32:07.000'},'admin','<p>Hi #FIRSTNAME#,<br /><br />Congratulations! Now you are a member of #PAGENAME#.  Please save this email for your records.<br />Your user name to log into #PAGENAME# is #USERNAME#.<br /><br />Click the following link to confirm the registration: <a href="#CONFIRMATIONLINK#">#CONFIRMATIONLINK#</a></p>
<p>Kind regards<br />Your #PAGENAME# team</p>','Your registration on #PAGENAME#');
INSERT INTO core_email_tpl (id,created_at,created_by,modified_at,modified_by,content,subject) VALUES (2,null,null,{ts '2008-07-31 22:35:07.000'},'admin','<p>Hi #FIRSTNAME#,</p>
<p>you requested a password reset for your account #USERNAME# on #PAGENAME#.</p>
<p>Click the following link to set a new password:</p>
<p><a href="#PASSWORDRESETLINK#">#PASSWORDRESETLINK#</a></p>
<p>Kind regards</p>
<p>Your #PAGENAME# team</p>','Password reset for your account on #PAGENAME#');
INSERT INTO core_email_tpl (id,created_at,created_by,modified_at,modified_by,content,subject) VALUES (3,{ts '2009-01-04 17:30:28.000'},'admin',{ts '2009-01-04 17:44:44.000'},'admin','<p>Hi,</p>
<p>there is a new registered user on #PAGENAME#!</p>
<p>User: #USERNAME#<br />Firstname: #FIRSTNAME#<br />Lastname: #LASTNAME#<br />Email: #EMAIL#</p>
<p>Birthday: #BIRTHDAY#</p>
<p>Kind regards</p>
<p>Your #PAGENAME# Team</p>','Admin: New registered user on #PAGENAME#');
INSERT INTO core_email_tpl (id,created_at,created_by,modified_at,modified_by,content,subject) VALUES (4,{ts '2009-01-04 17:44:48.000'},'admin',{ts '2009-01-04 17:44:48.000'},'admin','<p>Hi,</p>
<p>there occurred an unknown error on #PAGENAME#:</p>
<p>#CONTENT#</p>
<p>Kind regards</p>
<p>Your #PAGENAME# team</p>','Admin: Unknown error on #PAGENAME#');
INSERT INTO core_email_tpl (id,created_at,created_by,modified_at,modified_by,content,subject) VALUES (5,null,null,{ts '2008-08-10 16:31:01.000'},'admin','<p>Hi #FIRSTNAME#,<br /><br />you just changed your email address. <br /><br />Click the following link to confirm the registration: <a href="#CONFIRMATIONLINK#">#CONFIRMATIONLINK#</a></p>

<p>Kind regards<br />Your #PAGENAME# team</p>','Email reconfirmation for your account on  #PAGENAME#');
INSERT INTO core_email_tpl (id,created_at,created_by,modified_at,modified_by,content,subject) VALUES (6,{ts '2009-01-05 22:22:31.000'},'admin',{ts '2009-01-05 22:52:16.000'},'admin','<p>Hi #USERNAME#,</p>
<p>Mr./Mrs #CONTACT_FULLNAME# send you this email from #PAGENAME#. If you do not like too receive emails, you can disable this function on #PAGENAME# under Settings.</p>
<p>Contact email: #CONTACT_EMAIL#</p>
<p>Contact IP: #CONTACT_IP#</p>
<p>#CONTENT#</p>
<p>&nbsp;</p>
<p>Kind regards</p>
<p>Your #PAGENAME# Team</p>','Contact request from #PAGENAME#');

INSERT INTO core_box (id, created_at,created_by,modified_at,modified_by,box_type,content,sort,title,hide_title) VALUES (1, {ts '2009-01-05 12:19:08.000'},'admin',{ts '2009-01-05 14:28:30.000'},'admin','OtherBoxPanel','<!-- AddThis Button BEGIN -->
<a class="addthis_button" href="http://www.addthis.com/bookmark.php?v=250&amp"><img src="http://s7.addthis.com/static/btn/v2/lg-share-en.gif" width="125" height="16" alt="Bookmark and Share" style="border:0"/></a><script type="text/javascript" src="http://s7.addthis.com/js/250/addthis_widget.js"></script>
<!-- AddThis Button END -->
<br/>',1,'AddThis.com',1);
INSERT INTO core_box (id, created_at,created_by,modified_at,modified_by,box_type,content,sort,title,hide_title) VALUES (2, {ts '2009-01-05 11:41:22.000'},'admin',{ts '2009-01-05 13:22:25.000'},'admin','TagCloudBoxPanel',null,7,'Related Tags Box',0);
INSERT INTO core_box (id, created_at,created_by,modified_at,modified_by,box_type,content,sort,title,hide_title) VALUES (3, {ts '2009-01-05 11:43:49.000'},'admin',{ts '2009-01-05 13:22:25.000'},'admin','SearchBoxPanel',null,2,'Search Box',0);
INSERT INTO core_box (id, created_at,created_by,modified_at,modified_by,box_type,content,sort,title,hide_title) VALUES (4, {ts '2009-01-05 12:16:28.000'},'admin',{ts '2009-01-05 13:22:03.000'},'admin','LoginBoxPanel',null,6,'Login Box',0);
INSERT INTO core_box (id, created_at,created_by,modified_at,modified_by,box_type,content,sort,title,hide_title) VALUES (5, {ts '2009-01-05 12:16:38.000'},'admin',{ts '2009-01-05 13:22:03.000'},'admin','UserBoxPanel',null,3,'User Box',0);
INSERT INTO core_box (id, created_at,created_by,modified_at,modified_by,box_type,content,sort,title,hide_title) VALUES (6, {ts '2009-01-05 12:17:03.000'},'admin',{ts '2009-01-05 15:36:40.000'},'admin','PageAdminBoxPanel',null,4,'Page Administration Box',0);
INSERT INTO core_box (id, created_at,created_by,modified_at,modified_by,box_type,content,sort,title,hide_title) VALUES (7, {ts '2009-01-05 12:17:35.000'},'admin',{ts '2009-01-05 13:11:05.000'},'admin','GlobalAdminBoxPanel',null,5,'Global Administration Box',0);
INSERT INTO core_box (id, created_at,created_by,modified_at,modified_by,box_type,content,sort,title,hide_title) VALUES (8, {ts '2009-01-05 12:19:08.000'},'admin',{ts '2009-01-05 14:28:30.000'},'admin','OtherBoxPanel','A little <i>bit</i> content',11,'Other Test Box',0);
INSERT INTO core_box (id, created_at,created_by,modified_at,modified_by,box_type,content,sort,title,hide_title) VALUES (9, {ts '2009-01-05 12:19:08.000'},'admin',{ts '2009-01-05 14:28:30.000'},'admin','FeedBoxPanel',null,12,'Feed Box',1);

INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.fontService.findSyntaxHighlighterThemes.theme','Syntax Highlighter Theme','General','java.lang.String','Eclipse');INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.ArticleEditPage','Article Author: Edit articles',{ts '2009-01-10 21:48:33.000'},'admin',{ts '2009-01-10 21:48:33.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('article.read','Article: Read as admin',{ts '2008-12-30 23:36:13.000'},'admin',{ts '2008-12-30 23:36:13.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('article.read.guest','Article: Read as guest',{ts '2008-12-30 23:36:31.000'},'admin',{ts '2008-12-30 23:36:31.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('article.read.preview','Article: Preview read',{ts '2008-12-30 23:51:18.000'},'admin',{ts '2008-12-30 23:51:18.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('article.read.registered','Article: Read as registered user',{ts '2008-12-30 23:36:57.000'},'admin',{ts '2008-12-30 23:36:57.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('article.view','Article: View as admin',{ts '2008-12-30 23:34:35.000'},'admin',{ts '2008-12-30 23:34:35.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('article.view.guest','Article: View as guest',{ts '2008-12-30 23:35:00.000'},'admin',{ts '2008-12-30 23:35:00.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('article.view.preview','Article: Preview teaser',{ts '2008-12-30 23:50:53.000'},'admin',{ts '2008-12-30 23:51:34.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('article.view.registered','Article: View as registered user',{ts '2008-12-30 23:35:19.000'},'admin',{ts '2008-12-30 23:35:19.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('general.ArticleBoxPanel','See Box: Latest articles box',{ts '2009-01-05 14:19:24.000'},'admin',{ts '2009-01-05 14:20:10.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.ArticlePage','Article: See the articles',{ts '2009-01-05 23:33:57.000'},'admin',{ts '2009-01-10 21:26:54.000'},'admin');

INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'article.read.preview');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'article.view.preview');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'article.read');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'article.read.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'article.read.registered');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'article.view');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'article.view.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'article.view.registered');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'general.ArticleBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.ArticlePage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.ArticleEditPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'article.read.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'article.view.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'general.ArticleBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'page.ArticlePage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'article.view.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'article.view.registered');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'article.read.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'article.read.registered');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'general.ArticleBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'page.ArticlePage');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('articles_per_page','Articles per page','Articles','java.lang.Integer','5');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('box_num_latest_articles','Number of latest articles','Articles','java.lang.Integer','3');
INSERT INTO core_box (id, created_at,created_by,modified_at,modified_by,box_type,content,sort,title,hide_title) VALUES (40, {ts '2009-01-05 12:17:49.000'},'admin',{ts '2009-01-05 12:18:07.000'},'admin','ArticleBoxPanel',null,10,'Latest Articles Box', 0);

INSERT INTO article (id,created_at,created_by,modified_at,modified_by,content_id,teaser,title) VALUES (1,{ts '2009-01-06 19:28:56.000'},'admin',{ts '2009-01-06 19:28:56.000'},'admin','Sample_article','<p>This is a sample article and this is the teaser</p>','Sample article');
INSERT INTO article_page (content_id,page,content,article_id) VALUES ('Sample_article',1,'<p>Some sample content on page 1.</p>',1);
INSERT INTO article_page (content_id,page,content,article_id) VALUES ('Sample_article',2,'<p>Some sample content on page 2.</p>',1);
INSERT INTO article_page (content_id,page,content,article_id) VALUES ('Sample_article',3,'<p>Some sample content on page 3.</p>',1);


INSERT INTO article_right_xref (article_id,right_id) VALUES (1,'article.view.guest');
INSERT INTO article_right_xref (article_id,right_id) VALUES (1,'article.view.registered');
INSERT INTO article_right_xref (article_id,right_id) VALUES (1,'article.view.preview');
INSERT INTO article_right_xref (article_id,right_id) VALUES (1,'article.read.preview');
INSERT INTO article_right_xref (article_id,right_id) VALUES (1,'article.read.guest');
INSERT INTO article_right_xref (article_id,right_id) VALUES (1,'article.read.registered');
INSERT INTO article_tag (tagname,created_at,created_by,modified_at,modified_by) VALUES ('sample',{ts '2009-01-06 19:28:56.000'},'admin',{ts '2009-01-06 19:28:56.000'},'admin');
INSERT INTO article_tag_xref (article_id,tagname) VALUES (1,'sample');

-- since 1.0-rc3
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('article_entries_in_feed','Article entries in feed','Articles','java.lang.Integer','10');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('article_feed_title','Article feed title','Articles','java.lang.String','Articles');

INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('article.comment.view','Article: View comments',null,null,null,null);
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('article.comment.write','Article: Write comments',null,null,null,null);

INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'article.comment.view');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'article.comment.write');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'article.comment.view');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'article.comment.write');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'article.comment.view');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'article.comment.write');INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.BlogEditPage','Blog Author: Edit blog entries',{ts '2009-01-10 21:30:01.000'},'admin',{ts '2009-01-10 21:30:01.000'},'admin');
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

INSERT INTO blog (id,created_at,created_by,modified_at,modified_by,content,headline) VALUES (1,{ts '2009-01-06 19:27:20.000'},'admin',{ts '2009-01-06 19:27:20.000'},'admin','<p>Congratulation. Devproof Portal is running.</p>
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
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'blog.comment.write');INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.BookmarkEditPage','Bookmark Author: Edit bookmarks',{ts '2009-01-10 23:33:51.000'},'admin',{ts '2009-01-10 23:33:51.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('bookmark.view','Bookmark: View as admin',{ts '2008-12-15 17:53:49.000'},'admin',{ts '2008-12-15 17:53:49.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('bookmark.view.guest','Bookmark: View as guest',{ts '2008-12-15 17:54:16.000'},'admin',{ts '2008-12-15 17:54:16.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('bookmark.view.registered','Bookmark: View as registered user',{ts '2008-12-15 17:54:39.000'},'admin',{ts '2008-12-15 17:54:39.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('bookmark.visit','Bookmark: Visit as admin',{ts '2008-12-15 17:55:14.000'},'admin',{ts '2008-12-15 17:55:14.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('bookmark.visit.guest','Bookmark: Visit as guest',{ts '2008-12-15 17:55:31.000'},'admin',{ts '2008-12-15 17:55:31.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('bookmark.visit.registered','Bookmark: Visit as registered user',{ts '2008-12-15 17:55:51.000'},'admin',{ts '2008-12-15 17:55:51.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('bookmark.vote','Bookmark: Vote as admin',{ts '2008-12-29 19:53:07.000'},'admin',{ts '2008-12-29 19:53:07.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('bookmark.vote.guest','Bookmark: Vote as guest',{ts '2008-12-29 19:53:32.000'},'admin',{ts '2008-12-29 19:53:32.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('bookmark.vote.registered','Bookmark: Vote as registered user',{ts '2008-12-29 19:53:58.000'},'admin',{ts '2008-12-29 19:53:58.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('general.BookmarkBoxPanel','See Box: Latest bookmarks box',{ts '2009-01-05 14:20:01.000'},'admin',{ts '2009-01-05 14:20:01.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.BookmarkPage','Bookmark: See the bookmarks',{ts '2009-01-05 23:30:05.000'},'admin',{ts '2009-01-05 23:30:38.000'},'admin');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.BookmarkPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'bookmark.view');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'bookmark.view.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'bookmark.view.registered');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'bookmark.visit');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'bookmark.visit.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'bookmark.visit.registered');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'bookmark.vote');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'bookmark.vote.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'bookmark.vote.registered');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'general.BookmarkBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.BookmarkEditPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'bookmark.view.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'bookmark.visit.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'bookmark.vote.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'general.BookmarkBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'page.BookmarkPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'bookmark.view.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'bookmark.view.registered');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'bookmark.visit.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'bookmark.visit.registered');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'bookmark.vote.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'bookmark.vote.registered');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'general.BookmarkBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'page.BookmarkPage');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('bookmarks_per_page','Bookmarks per page','Bookmarks','java.lang.Integer','5');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('bookmark_vote_enabled','Vote enabled','Bookmarks','java.lang.Boolean','true');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('bookmark_hide_broken','Hide broken bookmarks','Bookmarks','java.lang.Boolean','true');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('box_num_latest_bookmarks','Number of latest bookmarks','Bookmarks','java.lang.Integer','3');
INSERT INTO core_box (id, created_at,created_by,modified_at,modified_by,box_type,content,sort,title,hide_title) VALUES (20, {ts '2009-01-05 12:18:22.000'},'admin',{ts '2009-01-05 12:47:42.000'},'Guest','BookmarkBoxPanel',null,9,'Latest Bookmarks Box',0);

INSERT INTO bookmark (id,created_at,created_by,modified_at,modified_by,description,hits,number_of_votes,sum_of_rating,title,url,source,broken,sync_hash,sync_username) VALUES (1,{ts '2009-01-06 19:35:49.000'},'admin',{ts '2009-01-06 19:35:49.000'},'admin','<p>This a sample bookmark and refers to devproof.org. </p>',0,0,0,'Sample Bookmark','http://devproof.org','MANUAL',0,null,null);

INSERT INTO bookmark_right_xref (bookmark_id,right_id) VALUES (1,'bookmark.view.guest');
INSERT INTO bookmark_right_xref (bookmark_id,right_id) VALUES (1,'bookmark.view.registered');
INSERT INTO bookmark_right_xref (bookmark_id,right_id) VALUES (1,'bookmark.visit.guest');
INSERT INTO bookmark_right_xref (bookmark_id,right_id) VALUES (1,'bookmark.visit.registered');
INSERT INTO bookmark_right_xref (bookmark_id,right_id) VALUES (1,'bookmark.vote.guest');
INSERT INTO bookmark_right_xref (bookmark_id,right_id) VALUES (1,'bookmark.vote.registered');
INSERT INTO bookmark_tag (tagname,created_at,created_by,modified_at,modified_by) VALUES ('devproof',{ts '2009-01-06 19:28:56.000'},'admin',{ts '2009-01-06 19:28:56.000'},'admin');
INSERT INTO bookmark_tag (tagname,created_at,created_by,modified_at,modified_by) VALUES ('sample',{ts '2009-01-06 19:28:56.000'},'admin',{ts '2009-01-06 19:28:56.000'},'admin');
INSERT INTO bookmark_tag_xref (bookmark_id,tagname) VALUES (1,'devproof');
INSERT INTO bookmark_tag_xref (bookmark_id,tagname) VALUES (1,'sample');

-- since 1.0-rc3
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('bookmark_entries_in_feed','Bookmark entries in feed','Bookmarks','java.lang.Integer','10');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('bookmark_feed_title','Bookmark feed title','Bookmarks','java.lang.String','Bookmarks');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('comment_blamed_threshold','Number of blamed notifications to hide the comment automatically','Comments','java.lang.Integer','3');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('comment_show_real_author','Show real author name in comments','Comments','java.lang.Boolean','false');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('comment_number_per_page','Number of comments per page','Comments','java.lang.Integer','10');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('comment_number_per_page_admin','Number of comments per page for administration','Comments','java.lang.Integer','50');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('comment_show_only_reviewed','Show only reviewed comments','Comments','java.lang.Boolean','false');

INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.CommentAdminPage','Comment: Administrate comments',null,null,null,null);
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.CommentAdminPage');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('comment.notify.autoblocked','Comment: Automatic blocked notification email',null,null,null,null);
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'comment.notify.autoblocked');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('comment.notify.violation','Comment: Violation notication email',null,null,null,null);
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'comment.notify.violation');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('comment.notify.newcomment','Comment: New comment notification email',null,null,null,null);
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'comment.notify.newcomment');

INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailTemplateDao.findAll.subject.id.newcommentnotification','Notification email for new comments','Comments','java.lang.Integer','7');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailTemplateDao.findAll.subject.id.autoblockednotification','Notification email for automatic blocked comments','Comments','java.lang.Integer','9');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailTemplateDao.findAll.subject.id.violationnotification','Notification email for reported violations on comments','Comments','java.lang.Integer','8');

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
<p>Your #PAGENAME# Team</p>','Admin: Comment - Automatic blocked comment');INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.DownloadEditPage','Download Author: Edit downloads',{ts '2009-01-10 23:34:54.000'},'admin',{ts '2009-01-10 23:34:54.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('download.download','Download: Download as admin',{ts '2008-12-15 17:49:19.000'},'admin',{ts '2008-12-15 17:49:19.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('download.download.guest','Download: Download as guest',{ts '2008-12-15 17:49:34.000'},'admin',{ts '2008-12-15 17:49:34.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('download.download.registered','Download: Download as registered user',{ts '2008-12-15 17:49:53.000'},'admin',{ts '2008-12-15 17:49:53.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('download.view','Download: View as admin',{ts '2008-12-15 17:45:59.000'},'admin',{ts '2008-12-15 17:45:59.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('download.view.guest','Download: View as guest',{ts '2008-12-15 17:46:25.000'},'admin',{ts '2008-12-15 17:46:25.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('download.view.registered','Download: View as registered user',{ts '2008-12-15 17:46:59.000'},'admin',{ts '2008-12-15 17:46:59.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('download.vote','Download: Vote as admin',{ts '2008-12-26 01:36:19.000'},'admin',{ts '2008-12-26 01:36:19.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('download.vote.guest','Download: Vote as guest',{ts '2008-12-26 01:36:44.000'},'admin',{ts '2008-12-26 01:36:44.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('download.vote.registered','Download: Vote as registered user',{ts '2008-12-26 01:37:06.000'},'admin',{ts '2008-12-26 01:37:06.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('general.DownloadBoxPanel','See Box: Latest downloads box',{ts '2009-01-05 14:20:40.000'},'admin',{ts '2009-01-05 14:24:41.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.DownloadPage','Download: See the downloads',{ts '2009-01-05 23:31:34.000'},'admin',{ts '2009-01-05 23:31:34.000'},'admin');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'download.download');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'download.download.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'download.download.registered');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.DownloadPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'download.view');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'download.view.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'download.view.registered');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'download.vote');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'download.vote.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'download.vote.registered');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'general.DownloadBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.DownloadEditPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'download.download.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'download.view.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'download.vote.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'general.DownloadBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (2,'page.DownloadPage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'download.view.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'download.view.registered');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'download.download.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'download.download.registered');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'download.vote.guest');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'download.vote.registered');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'general.DownloadBoxPanel');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'page.DownloadPage');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('downloads_per_page','Downloads per page','Downloads','java.lang.Integer','5');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('download_vote_enabled','Vote enabled','Downloads','java.lang.Boolean','true');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('download_hide_broken','Hide broken downloads','Downloads','java.lang.Boolean','true');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('box_num_latest_downloads','Number of latest downloads','Downloads','java.lang.Integer','3');
INSERT INTO core_box (id, created_at,created_by,modified_at,modified_by,box_type,content,sort,title,hide_title) VALUES (10, {ts '2009-01-05 12:18:01.000'},'admin',{ts '2009-01-05 12:47:42.000'},'Guest','DownloadBoxPanel',null,8,'Latest Downloads Box',0);

INSERT INTO download (id,created_at,created_by,modified_at,modified_by,description,hits,number_of_votes,sum_of_rating,title,url,download_size,licence,manufacturer_homepage,price,software_version,manufacturer,broken) VALUES (1,{ts '2009-01-06 19:34:23.000'},'admin',{ts '2009-01-06 19:34:23.000'},'admin','<p>This is a sample. You can define download for http, https, ftp and local urls. Local urls starts with file:/path.<br />You can start a broken check and all broken downloads will marked as it.</p>',1,0,0,'Sample Download','/img/bg.gif',null,null,null,null,null,null,0);
INSERT INTO download_right_xref (download_id,right_id) VALUES (1,'download.view.guest');
INSERT INTO download_right_xref (download_id,right_id) VALUES (1,'download.view.registered');
INSERT INTO download_right_xref (download_id,right_id) VALUES (1,'download.download.registered');
INSERT INTO download_right_xref (download_id,right_id) VALUES (1,'download.download.guest');
INSERT INTO download_right_xref (download_id,right_id) VALUES (1,'download.vote.registered');
INSERT INTO download_right_xref (download_id,right_id) VALUES (1,'download.vote.guest');
INSERT INTO download_tag (tagname,created_at,created_by,modified_at,modified_by) VALUES ('sample',{ts '2009-01-06 19:28:56.000'},'admin',{ts '2009-01-06 19:28:56.000'},'admin');
INSERT INTO download_tag_xref (download_id,tagname) VALUES (1,'sample');

-- since 1.0-rc3
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('download_entries_in_feed','Download entries in feed','Downloads','java.lang.Integer','10');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('download_feed_title','Download feed title','Downloads','java.lang.String','Downloads');

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

INSERT INTO other_page (id,created_at,created_by,modified_at,modified_by,content,content_id) VALUES (2,{ts '2009-01-07 19:40:23.000'},'admin',{ts '2009-01-07 19:40:23.000'},'admin','<p>Terms of use for registration.</p>','terms_of_use');
INSERT INTO other_page_right_xref (other_id,right_id) VALUES (2,'otherPage.view.preview');
INSERT INTO other_page_right_xref (other_id,right_id) VALUES (2,'otherPage.view.guest');
INSERT INTO other_page_right_xref (other_id,right_id) VALUES (2,'otherPage.view.registered');INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.UploadCenterPage','Upload Center: Upload, delete and download files',{ts '2008-12-15 17:58:02.000'},'admin',{ts '2009-01-10 21:26:21.000'},'admin');
INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('page.UploadThemePage','Theme Administration: Upload themes',{ts '2009-01-10 21:21:23.000'},'admin',{ts '2009-01-10 21:21:53.000'},'admin');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.UploadThemePage');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'page.UploadCenterPage');

INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('uploadcenter_maxfiles','Maximum number of files','Upload Center','java.lang.Integer','10');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('uploadcenter_maxsize','Maximum size of file (in KB)','Upload Center','java.lang.Integer','100000');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('uploadcenter_folder','Folder to store files','Upload Center','java.lang.String','java.io.tmpdir');
