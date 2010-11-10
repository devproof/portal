UPDATE core_configuration set conf_key = 'spring.emailService.findAll.subject.id.newcommentnotification'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.newcommentnotification';
UPDATE core_configuration set conf_key = 'spring.emailService.findAll.subject.id.autoblockednotification'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.autoblockednotification';
UPDATE core_configuration set conf_key = 'spring.emailService.findAll.subject.id.violationnotification'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.violationnotification';
