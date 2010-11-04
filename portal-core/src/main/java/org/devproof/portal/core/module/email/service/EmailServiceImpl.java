/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.email.service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.email.EmailConstants;
import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.email.dao.EmailTemplateRepository;
import org.devproof.portal.core.module.email.entity.EmailTemplateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * @author Carsten Hufe
 */
@Service("emailService")
public class EmailServiceImpl implements EmailService {
    private final Log logger = LogFactory.getLog(EmailServiceImpl.class);
    private EmailTemplateRepository emailTemplateDao;
    private ConfigurationService configurationService;
    private JavaMailSender javaMailSender;
    private SimpleDateFormat dateFormat;
    private boolean emailDisabled;

    @Override
    public EmailTemplateEntity newEmailTemplateEntity() {
        return new EmailTemplateEntity();
    }

    @Override
    public void delete(EmailTemplateEntity entity) {
        emailTemplateDao.delete(entity);
    }

    @Override
    public List<EmailTemplateEntity> findAll() {
        return emailTemplateDao.findAll();
    }

    @Override
    public EmailTemplateEntity findById(Integer id) {
        return emailTemplateDao.findById(id);
    }

    @Override
    public void save(EmailTemplateEntity entity) {
        emailTemplateDao.save(entity);
    }

    @Override
    public void sendEmail(EmailTemplateEntity template, EmailPlaceholderBean placeholder) {
        if (emailDisabled) {
        	System.out.println("Sending Email <" + placeholder.getToEmail() + ">: " + template.getSubject());
            return;
        }
        // Create email
        try {
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg);
            if (placeholder.getContactEmail() != null) {
                String from = "";
                if (placeholder.getContactFullname() != null) {
                    from += placeholder.getContactFullname();
                } else {
                    from += placeholder.getContactEmail();
                }
                from += " <" + placeholder.getContactEmail() + ">";
                helper.setFrom(from);
            } else {
                String from = configurationService.findAsString(EmailConstants.CONF_FROM_EMAIL_NAME);
                from += " <" + configurationService.findAsString(EmailConstants.CONF_FROM_EMAIL_ADDRESS) + ">";
                helper.setFrom(from);
            }
            if (placeholder.getToEmail() != null) {
                String name = placeholder.getToFirstname() != null ? placeholder.getToFirstname() : "";
                name += " " + (placeholder.getToLastname() != null ? placeholder.getToLastname() : "");
                if (StringUtils.isBlank(name)) {
                    name = placeholder.getToUsername();
                }
                helper.setTo(name + " <" + placeholder.getToEmail() + ">");
            } else {
                String name = placeholder.getFirstname() != null ? placeholder.getFirstname() : "";
                name += " " + (placeholder.getLastname() != null ? placeholder.getLastname() : "");
                if (StringUtils.isBlank(name)) {
                    name = placeholder.getUsername();
                }
                helper.setTo(name + " <" + placeholder.getEmail() + ">");
            }
            helper.setSubject(replace(template.getSubject(), placeholder));
            helper.setText("<html><body>" + replace(template.getContent(), placeholder) + "</body></html>", true);
            javaMailSender.send(msg);
            logger.info("Send email to " + placeholder.getToEmail() + " " + template.getSubject());
        } catch (MailException e) {
            throw new UnhandledException(e);
        } catch (MessagingException e) {
            throw new UnhandledException(e);
        }
    }

    @Override
    public void sendEmail(Integer templateId, EmailPlaceholderBean placeholder) {
        EmailTemplateEntity template = emailTemplateDao.findById(templateId);
        this.sendEmail(template, placeholder);
    }

    private String replace(String in, EmailPlaceholderBean placeholder) {
        String content = in;
        content = content.replace(EmailConstants.EMAIL_PLACEHOLDER_USERNAME, placeholder.getUsername() != null ? placeholder.getUsername() : "");
        content = content.replace(EmailConstants.EMAIL_PLACEHOLDER_FIRSTNAME, placeholder.getFirstname() != null ? placeholder.getFirstname() : "");
        content = content.replace(EmailConstants.EMAIL_PLACEHOLDER_LASTNAME, placeholder.getLastname() != null ? placeholder.getLastname() : "");
        content = content.replace(EmailConstants.EMAIL_PLACEHOLDER_PAGENAME, configurationService.findAsString(EmailConstants.CONF_PAGE_NAME));
        content = content.replace(EmailConstants.EMAIL_PLACEHOLDER_EMAIL, placeholder.getEmail() != null ? placeholder.getEmail() : "");
        content = content.replace(EmailConstants.EMAIL_PLACEHOLDER_CONFIRMATIONLINK, placeholder.getConfirmationLink() != null ? placeholder.getConfirmationLink() : "");
        content = content.replace(EmailConstants.EMAIL_PLACEHOLDER_PASSWORDRESETLINK, placeholder.getResetPasswordLink() != null ? placeholder.getResetPasswordLink() : "");

        content = content.replace(EmailConstants.EMAIL_PLACEHOLDER_CONTACT_FULLNAME, placeholder.getContactFullname() != null ? placeholder.getContactFullname() : "");
        content = content.replace(EmailConstants.EMAIL_PLACEHOLDER_CONTACT_EMAIL, placeholder.getContactEmail() != null ? placeholder.getContactEmail() : "");
        content = content.replace(EmailConstants.EMAIL_PLACEHOLDER_CONTACT_IP, placeholder.getContactIp() != null ? placeholder.getContactIp() : "");

        String inlineContent = "";
        if (placeholder.getContent() != null) {
            inlineContent = placeholder.getContent().replace("\n", "<br />");
        }
        content = content.replace(EmailConstants.EMAIL_PLACEHOLDER_CONTENT, inlineContent);
        String birthday = "";
        if (placeholder.getBirthday() != null) {
            birthday = dateFormat.format(placeholder.getBirthday());
        }
        content = content.replace(EmailConstants.EMAIL_PLACEHOLDER_BIRTHDAY, birthday);
        Map<String, String> additionalPlaceholder = placeholder.getAdditionalPlaceholder();
        for (String key : additionalPlaceholder.keySet()) {
            content = content.replace("#" + key + "#", additionalPlaceholder.get(key));
        }
        return content;
    }

    @Autowired
    public void setEmailTemplateDao(EmailTemplateRepository emailTemplateDao) {
        this.emailTemplateDao = emailTemplateDao;
    }

    @Autowired
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Autowired
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Autowired
    @Qualifier("displayDateFormat")
    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Value("#{jndiConfig.resolveEmailDisabled()}")
    public void setEmailDisabled(boolean emailDisabled) {
        this.emailDisabled = emailDisabled;
    }
}
