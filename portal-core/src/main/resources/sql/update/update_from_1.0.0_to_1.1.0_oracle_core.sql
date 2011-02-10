UPDATE core_configuration set conf_key = 'spring.emailService.findAll.subject.id.forgotemail'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.forgotemail';
UPDATE core_configuration set conf_key = 'spring.emailService.findAll.subject.id.reconfirmemail'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.reconfirmemail';
UPDATE core_configuration set conf_key = 'spring.emailService.findAll.subject.id.contactformemail'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.contactformemail';
UPDATE core_configuration set conf_key = 'spring.emailService.findAll.subject.id.regemail'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.regemail';
UPDATE core_configuration set conf_key = 'spring.emailService.findAll.subject.id.registereduser'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.registereduser';
UPDATE core_configuration set conf_key = 'spring.emailService.findAll.subject.id.unknownerror'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.unknownerror';
UPDATE core_configuration set conf_key = 'spring.emailService.findAll.subject.id.forgotemail'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.forgotemail';
UPDATE core_configuration set conf_key = 'spring.roleService.findAll.description.id.guestrole'
  WHERE conf_key = 'spring.roleDao.findAll.description.id.guestrole';
UPDATE core_configuration set conf_key = 'spring.roleService.findAll.description.id.registerrole'
  WHERE conf_key = 'spring.roleDao.findAll.description.id.registerrole';

ALTER TABLE "CORE_ROLE_RIGHT_XREF" DISABLE CONSTRAINT "FK5AFA2427E35D2FF0";
ALTER TABLE "CORE_ROLE_RIGHT_XREF" DISABLE CONSTRAINT "FK5AFA2427FEB2F584";
DELETE FROM core_role_right_xref WHERE right_id LIKE 'general.GlobalAdminBoxPanel';
DELETE FROM core_right WHERE right_id LIKE 'general.GlobalAdminBoxPanel';
DELETE FROM core_role_right_xref WHERE right_id LIKE 'general.LoginBoxPanel';
DELETE FROM core_right WHERE right_id LIKE 'general.LoginBoxPanel';
DELETE FROM core_role_right_xref WHERE right_id LIKE 'general.OtherBoxPanel';
DELETE FROM core_right WHERE right_id LIKE 'general.OtherBoxPanel';
DELETE FROM core_role_right_xref WHERE right_id LIKE 'general.PageAdminBoxPanel';
DELETE FROM core_right WHERE right_id LIKE 'general.PageAdminBoxPanel';
DELETE FROM core_role_right_xref WHERE right_id LIKE 'general.TagCloudBoxPanel';
DELETE FROM core_right WHERE right_id LIKE 'general.TagCloudBoxPanel';
DELETE FROM core_role_right_xref WHERE right_id LIKE 'general.UserBoxPanel';
DELETE FROM core_right WHERE right_id LIKE 'general.UserBoxPanel';
DELETE FROM core_role_right_xref WHERE right_id LIKE 'page.ForgotPasswordPage';
DELETE FROM core_right WHERE right_id LIKE 'page.ForgotPasswordPage';
DELETE FROM core_role_right_xref WHERE right_id LIKE 'page.RegisterPage';
DELETE FROM core_right WHERE right_id LIKE 'page.RegisterPage';
DELETE FROM core_role_right_xref WHERE right_id LIKE 'page.SettingsPage';
DELETE FROM core_right WHERE right_id LIKE 'page.SettingsPage';

UPDATE core_role_right_xref SET right_id = 'box.admin' WHERE right_id LIKE 'page.BoxPage';
UPDATE core_right SET right_id = 'box.admin', description = 'Box Administration' WHERE right_id LIKE 'page.BoxPage';
UPDATE core_role_right_xref SET right_id = 'configuration.admin' WHERE right_id LIKE 'page.ConfigurationPage';
UPDATE core_right SET right_id = 'configuration.admin', description = 'Configuration Administration' WHERE right_id LIKE 'page.ConfigurationPage';
UPDATE core_role_right_xref SET right_id = 'contact' WHERE right_id LIKE 'page.ContactPage';
UPDATE core_right SET right_id = 'contact', description = 'Contact Form: User who are allowed to access the contact form' WHERE right_id LIKE 'page.ContactPage';
UPDATE core_role_right_xref SET right_id = 'emailtemplate.admin' WHERE right_id LIKE 'page.EmailTemplatePage';
UPDATE core_right SET right_id = 'emailtemplate.admin', description = 'Email Templates Administration' WHERE right_id LIKE 'page.EmailTemplatePage';
UPDATE core_role_right_xref SET right_id = 'right.admin' WHERE right_id LIKE 'page.RightPage';
UPDATE core_right SET right_id = 'right.admin', description = 'Rights Administration' WHERE right_id LIKE 'page.RightPage';
UPDATE core_role_right_xref SET right_id = 'role.admin' WHERE right_id LIKE 'page.RolePage';
UPDATE core_right SET right_id = 'role.admin', description = 'Roles Administration' WHERE right_id LIKE 'page.RolePage';
UPDATE core_role_right_xref SET right_id = 'user.admin' WHERE right_id LIKE 'page.UserPage';
UPDATE core_right SET right_id = 'user.admin', description = 'User Administration' WHERE right_id LIKE 'page.UserPage';
UPDATE core_role_right_xref SET right_id = 'theme.admin' WHERE right_id LIKE 'page.ThemePage';
UPDATE core_right SET right_id = 'theme.admin', description = 'Theme Administration' WHERE right_id LIKE 'page.ThemePage';
DELETE FROM core_role_right_xref WHERE right_id LIKE 'page.UploadThemePage';
DELETE FROM core_right WHERE right_id LIKE 'page.UploadThemePage';
UPDATE core_role_right_xref SET right_id = 'modulemgmt.admin' WHERE right_id LIKE 'page.ModuleOverviewPage';
UPDATE core_right SET right_id = 'modulemgmt.admin', description = 'Module Administration' WHERE right_id LIKE 'page.ModuleOverviewPage';
DELETE FROM core_role_right_xref WHERE right_id LIKE 'page.ModuleLinkPage';
DELETE FROM core_right WHERE right_id LIKE 'page.ModuleLinkPage';
ALTER TABLE "CORE_ROLE_RIGHT_XREF" ENABLE CONSTRAINT "FK5AFA2427E35D2FF0";
ALTER TABLE "CORE_ROLE_RIGHT_XREF" ENABLE CONSTRAINT "FK5AFA2427FEB2F584";


CREATE TABLE "CORE_MOUNT_POINT" (
  "ID" NUMBER(10,0) NOT NULL ENABLE,
  "MOUNT_PATH" VARCHAR2(255 CHAR) NOT NULL ENABLE,
  "RELATED_CONTENT_ID" VARCHAR2(255 CHAR),
  "HANDLER_KEY" VARCHAR2(255 CHAR) NOT NULL ENABLE,
  "DEFAULT_URL" NUMBER(1,0) NOT NULL ENABLE,
  PRIMARY KEY  ("ID") ENABLE,
  UNIQUE ("MOUNT_PATH") ENABLE
);

CREATE INDEX "MP_RELATED_CONTENT_ID" ON "CORE_MOUNT_POINT" ("RELATED_CONTENT_ID");
CREATE INDEX "MP_HANDLER_KEY" ON "CORE_MOUNT_POINT" ("HANDLER_KEY");
CREATE INDEX "MP_DEFAULT_URL" ON "CORE_MOUNT_POINT" ("DEFAULT_URL");

ALTER TABLE "CORE_BOX" ADD ("CUSTOM_STYLE" VARCHAR2(255 CHAR));