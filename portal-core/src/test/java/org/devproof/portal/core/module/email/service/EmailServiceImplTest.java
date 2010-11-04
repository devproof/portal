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

import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.email.EmailConstants;
import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.email.entity.EmailTemplate;
import org.devproof.portal.core.module.email.repository.EmailTemplateRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Carsten Hufe
 */
public class EmailServiceImplTest {
    private EmailServiceImpl impl;
    private EmailTemplateRepository daomock;
    private JavaMailSender emailmock;
    private ConfigurationService confservice;

    @Before
    public void setUp() throws Exception {
        daomock = createStrictMock(EmailTemplateRepository.class);
        emailmock = createStrictMock(JavaMailSender.class);
        confservice = createNiceMock(ConfigurationService.class);
        impl = new EmailServiceImpl();
        impl.setEmailTemplateRepository(daomock);
        impl.setConfigurationService(confservice);
        impl.setJavaMailSender(emailmock);
        impl.setDateFormat(new SimpleDateFormat("dd-mm-yyyy"));
    }

    @Test
    public void testSave() {
        EmailTemplate e = impl.newEmailTemplateEntity();
        e.setId(1);
        expect(daomock.save(e)).andReturn(e);
        replay(daomock);
        impl.save(e);
        verify(daomock);
    }

    @Test
    public void testDelete() {
        EmailTemplate e = impl.newEmailTemplateEntity();
        e.setId(1);
        daomock.delete(e);
        replay(daomock);
        impl.delete(e);
        verify(daomock);
    }

    @Test
    public void testFindAll() {
        List<EmailTemplate> list = new ArrayList<EmailTemplate>();
        list.add(impl.newEmailTemplateEntity());
        list.add(impl.newEmailTemplateEntity());
        expect(daomock.findAll()).andReturn(list);
        replay(daomock);
        assertEquals(list, impl.findAll());
        verify(daomock);
    }

    @Test
    public void testFindById() {
        EmailTemplate e = impl.newEmailTemplateEntity();
        e.setId(1);
        expect(daomock.findById(1)).andReturn(e);
        replay(daomock);
        assertEquals(impl.findById(1), e);
        verify(daomock);
    }

    @Test
    public void testNewEmailTemplateEntity() {
        assertNotNull(impl.newEmailTemplateEntity());
    }

    @Test
    public void testSendEmail1() {
        EmailTemplate e = impl.newEmailTemplateEntity();
        e.setSubject("hello");
        e.setContent("world");
        EmailPlaceholderBean b = new EmailPlaceholderBean();

        MimeMessage mm = new MimeMessage((Session) null);
        expect(confservice.findAsString(EmailConstants.CONF_FROM_EMAIL_NAME)).andReturn("anything");
        expect(confservice.findAsString(EmailConstants.CONF_FROM_EMAIL_ADDRESS)).andReturn("anything");
        expect(confservice.findAsString(EmailConstants.CONF_PAGE_NAME)).andReturn("anything");
        expect(confservice.findAsString(EmailConstants.CONF_PAGE_NAME)).andReturn("anything");
        expect(emailmock.createMimeMessage()).andReturn(mm);
        emailmock.send(mm);
        replay(confservice);
        replay(emailmock);
        impl.sendEmail(e, b);
        verify(emailmock);
    }

    @Test
    public void testSendEmail2() {
        EmailTemplate e = impl.newEmailTemplateEntity();
        e.setSubject("hello");
        e.setContent("world");
        EmailPlaceholderBean b = new EmailPlaceholderBean();

        MimeMessage mm = new MimeMessage((Session) null);
        expect(daomock.findById(1)).andReturn(e);
        expect(confservice.findAsString(EmailConstants.CONF_FROM_EMAIL_NAME)).andReturn("anything");
        expect(confservice.findAsString(EmailConstants.CONF_FROM_EMAIL_ADDRESS)).andReturn("anything");
        expect(confservice.findAsString(EmailConstants.CONF_PAGE_NAME)).andReturn("anything");
        expect(confservice.findAsString(EmailConstants.CONF_PAGE_NAME)).andReturn("anything");
        expect(emailmock.createMimeMessage()).andReturn(mm);
        emailmock.send(mm);
        replay(daomock);
        replay(confservice);
        replay(emailmock);
        impl.sendEmail(1, b);
        verify(emailmock);
    }
}
