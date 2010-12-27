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

-- entfernen
-- INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('general.GlobalAdminBoxPanel','See Box: Global Administration',{ts '2009-01-05 14:13:07.000'},'admin',{ts '2009-01-05 14:13:07.000'},'admin');
-- INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('general.LoginBoxPanel','See Box: Login box',{ts '2009-01-05 14:21:46.000'},'admin',{ts '2009-01-05 14:21:46.000'},'admin');
-- INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('general.OtherBoxPanel','See Box: Other boxes',{ts '2009-01-05 14:22:23.000'},'admin',{ts '2009-01-05 14:22:23.000'},'admin');
-- INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('general.PageAdminBoxPanel','See Box: Page Administration',{ts '2009-01-05 14:13:15.000'},'admin',{ts '2009-01-05 14:13:15.000'},'admin');
-- INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('general.TagCloudBoxPanel','See Box: Related tags box',{ts '2009-01-05 14:23:13.000'},'admin',{ts '2009-01-05 14:23:13.000'},'admin');
-- INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('general.UserBoxPanel','See Box: User info box (Settings/Logout)',{ts '2009-01-05 14:24:02.000'},'admin',{ts '2009-01-05 14:24:02.000'},'admin');