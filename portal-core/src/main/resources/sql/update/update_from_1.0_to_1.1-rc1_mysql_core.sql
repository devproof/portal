UPDATE core_configuration set conf_key = 'spring.emailTemplateRepository.findAll.subject.id.forgotemail'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.forgotemail';
UPDATE core_configuration set conf_key = 'spring.emailTemplateRepository.findAll.subject.id.reconfirmemail'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.reconfirmemail';
UPDATE core_configuration set conf_key = 'spring.emailTemplateRepository.findAll.subject.id.contactformemail'
  WHERE conf_key = 'spring.emailTemplateDao.findAll.subject.id.contactformemail';