UPDATE core_configuration


ERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailTemplateRepository.findAll.subject.id.forgotemail','Forgot your password email','Email','java.lang.Integer','2');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailTemplateRepository.findAll.subject.id.reconfirmemail','Reconfirmation email, when email was changed','Email','java.lang.Integer','5');

INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailTemplateRepository.findAll.subject.id.contactformemail','Contact form email template','Email','java.lang.Integer','6');

INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailTemplateRepository.findAll.subject.id.regemail','Registration email','Email','java.lang.Integer','1');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailTemplateRepository.findAll.subject.id.registereduser','Notification: A new user has been registered','Email','java.lang.Integer','3');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.emailTemplateRepository.findAll.subject.id.unknownerror','Notification: Unknown error email','Email','java.lang.Integer','4');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.roleRepository.findAll.description.id.guestrole','Default guest role','User','java.lang.Integer','2');
INSERT INTO core_configuration (conf_key,conf_description,conf_group,conf_type,conf_value) VALUES ('spring.roleRepository.findAll.description.id.registerrole','Default role for registration','User','java.lang.Integer','3');
