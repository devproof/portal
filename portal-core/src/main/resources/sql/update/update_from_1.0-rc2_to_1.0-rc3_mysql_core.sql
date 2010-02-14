ALTER TABLE core_box ADD COLUMN hide_title BIT NOT NULL;
UPDATE core_box SET hide_title = 0;
UPDATE core_box SET sort = sort + 1;
INSERT INTO core_box (created_at,created_by,modified_at,modified_by,box_type,content,sort,title,hide_title) VALUES ({ts '2009-01-05 12:19:08.000'},'admin',{ts '2009-01-05 14:28:30.000'},'admin','OtherBoxPanel','<!-- AddThis Button BEGIN -->
<a class="addthis_button" href="http://www.addthis.com/bookmark.php?v=250"><img src="http://s7.addthis.com/static/btn/v2/lg-share-en.gif" width="125" height="16" alt="Bookmark and Share" style="border:0"/></a><script type="text/javascript" src="http://s7.addthis.com/js/250/addthis_widget.js"></script>
<!-- AddThis Button END -->
<br/>',1,'AddThis.com',1);
SELECT @newSort := MAX(sort) + 1 FROM core_box;
INSERT INTO core_box (created_at,created_by,modified_at,modified_by,box_type,content,sort,title,hide_title) VALUES ({ts '2009-01-05 12:19:08.000'},'admin',{ts '2009-01-05 14:28:30.000'},'admin','FeedBoxPanel',null,@newSort,'Feed Box',1);

INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('display_date_format','Default display date format','General','java.lang.String','EEEEE, MMMMM dd, yyyy');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('display_date_time_format','Default display date time format','General','java.lang.String','EEEEE, MMMMM dd, yyyy HH:mm');
update core_configuration set conf_key = 'input_date_format', conf_value = 'yyyy-MM-dd', conf_description = 'Default input date format' where conf_key = 'date_format';
update core_configuration set conf_key = 'input_date_time_format', conf_value = 'yyyy-MM-dd HH:mm', conf_description = 'Default input date time format' where conf_key = 'date_time_format';
update core_configuration set conf_description = 'Require birthday for registration' where conf_key = 'registration_required_birthday';
update core_configuration set conf_description = 'Require first and lastname for registration' where conf_key = 'registration_required_name';


INSERT INTO core_right (right_id,created_at,created_by,modified_at,modified_by,description) VALUES ('captcha.disabled',{ts '2009-05-10 00:29:41.000'},'admin',{ts '2009-05-10 00:29:41.000'},'admin','Disables all captchas');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'captcha.disabled');
INSERT INTO core_role_right_xref (role_id,right_id) VALUES (3,'captcha.disabled');

delete from core_configuration  where conf_key = 'registration_captcha';
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('show_modified_at_as_created_at','Show modified at as created at','General','java.lang.Boolean','false');

-- IPv6 compatibility
alter table core_user modify last_ip VARCHAR(39) ; 
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.fontService.findSyntaxHighlighterThemes.theme','Syntax Highlighter Theme','General','java.lang.String','Eclipse');