UPDATE core_configuration set conf_key = 'spring.emailService.findAll.subject.id.newcommentnotification'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.newcommentnotification';
UPDATE core_configuration set conf_key = 'spring.emailService.findAll.subject.id.autoblockednotification'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.autoblockednotification';
UPDATE core_configuration set conf_key = 'spring.emailService.findAll.subject.id.violationnotification'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.violationnotification';


-- INSERT INTO core_right (right_id,description,created_at,created_by,modified_at,modified_by) VALUES ('comment.admin','Comment: Administrate comments',null,null,null,null);
-- INSERT INTO core_role_right_xref (role_id,right_id) VALUES (1,'comment.admin');