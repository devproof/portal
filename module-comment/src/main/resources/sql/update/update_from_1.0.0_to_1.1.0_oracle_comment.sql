UPDATE core_configuration set conf_key = 'spring.emailService.findAll.subject.id.newcommentnotification'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.newcommentnotification';
UPDATE core_configuration set conf_key = 'spring.emailService.findAll.subject.id.autoblockednotification'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.autoblockednotification';
UPDATE core_configuration set conf_key = 'spring.emailService.findAll.subject.id.violationnotification'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.violationnotification';

ALTER TABLE "CORE_ROLE_RIGHT_XREF" DISABLE CONSTRAINT "FK5AFA2427E35D2FF0";
ALTER TABLE "CORE_ROLE_RIGHT_XREF" DISABLE CONSTRAINT "FK5AFA2427FEB2F584";
UPDATE core_role_right_xref SET right_id = 'comment.admin' WHERE right_id LIKE 'page.CommentAdminPage';
UPDATE core_right SET right_id = 'comment.admin', description = 'Comment Administration' WHERE right_id LIKE 'page.CommentAdminPage';
ALTER TABLE "CORE_ROLE_RIGHT_XREF" ENABLE CONSTRAINT "FK5AFA2427E35D2FF0";
ALTER TABLE "CORE_ROLE_RIGHT_XREF" ENABLE CONSTRAINT "FK5AFA2427FEB2F584";