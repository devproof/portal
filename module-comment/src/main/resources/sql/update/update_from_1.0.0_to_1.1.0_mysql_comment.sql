UPDATE core_configuration set conf_key = 'spring.emailService.findAll.subject.id.newcommentnotification'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.newcommentnotification';
UPDATE core_configuration set conf_key = 'spring.emailService.findAll.subject.id.autoblockednotification'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.autoblockednotification';
UPDATE core_configuration set conf_key = 'spring.emailService.findAll.subject.id.violationnotification'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.violationnotification';

SET FOREIGN_KEY_CHECKS=0;
UPDATE core_role_right_xref SET right_id = 'comment.admin' WHERE right_id LIKE 'page.CommentAdminPage';
UPDATE core_right SET right_id = 'comment.admin', description = 'Comment Administration' WHERE right_id LIKE 'page.CommentAdminPage';
SET FOREIGN_KEY_CHECKS=1;
