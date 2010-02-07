/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.devproof.portal.core.module.email.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import junit.framework.TestCase;

import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.email.EmailConstants;
import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.email.dao.EmailTemplateDao;
import org.devproof.portal.core.module.email.entity.EmailTemplateEntity;
import org.easymock.EasyMock;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author Carsten Hufe
 */
public class EmailServiceImplTest extends TestCase {
	private EmailServiceImpl impl;
	private EmailTemplateDao daomock;
	private JavaMailSender emailmock;
	private ConfigurationService confservice;

	@Override
	public void setUp() throws Exception {
		daomock = EasyMock.createStrictMock(EmailTemplateDao.class);
		emailmock = EasyMock.createStrictMock(JavaMailSender.class);
		confservice = EasyMock.createNiceMock(ConfigurationService.class);
		impl = new EmailServiceImpl();
		impl.setEmailTemplateDao(daomock);
		impl.setConfigurationService(confservice);
		impl.setJavaMailSender(emailmock);
		impl.setDateFormat(new SimpleDateFormat("dd-mm-yyyy"));
	}

	public void testSave() {
		EmailTemplateEntity e = impl.newEmailTemplateEntity();
		e.setId(1);
		EasyMock.expect(daomock.save(e)).andReturn(e);
		EasyMock.replay(daomock);
		impl.save(e);
		EasyMock.verify(daomock);
	}

	public void testDelete() {
		EmailTemplateEntity e = impl.newEmailTemplateEntity();
		e.setId(1);
		daomock.delete(e);
		EasyMock.replay(daomock);
		impl.delete(e);
		EasyMock.verify(daomock);
	}

	public void testFindAll() {
		List<EmailTemplateEntity> list = new ArrayList<EmailTemplateEntity>();
		list.add(impl.newEmailTemplateEntity());
		list.add(impl.newEmailTemplateEntity());
		EasyMock.expect(daomock.findAll()).andReturn(list);
		EasyMock.replay(daomock);
		assertEquals(list, impl.findAll());
		EasyMock.verify(daomock);
	}

	public void testFindById() {
		EmailTemplateEntity e = impl.newEmailTemplateEntity();
		e.setId(1);
		EasyMock.expect(daomock.findById(1)).andReturn(e);
		EasyMock.replay(daomock);
		assertEquals(impl.findById(1), e);
		EasyMock.verify(daomock);
	}

	public void testNewEmailTemplateEntity() {
		assertNotNull(impl.newEmailTemplateEntity());
	}

	public void testSendEmail1() {
		EmailTemplateEntity e = impl.newEmailTemplateEntity();
		e.setSubject("hello");
		e.setContent("world");
		EmailPlaceholderBean b = new EmailPlaceholderBean();

		MimeMessage mm = new MimeMessage((Session) null);
		EasyMock.expect(confservice.findAsString(EmailConstants.CONF_FROM_EMAIL_NAME)).andReturn("anything");
		EasyMock.expect(confservice.findAsString(EmailConstants.CONF_FROM_EMAIL_ADDRESS)).andReturn("anything");
		EasyMock.expect(confservice.findAsString(EmailConstants.CONF_PAGE_NAME)).andReturn("anything");
		EasyMock.expect(confservice.findAsString(EmailConstants.CONF_PAGE_NAME)).andReturn("anything");
		EasyMock.expect(emailmock.createMimeMessage()).andReturn(mm);
		emailmock.send(mm);
		EasyMock.replay(confservice);
		EasyMock.replay(emailmock);
		impl.sendEmail(e, b);
		EasyMock.verify(emailmock);
	}

	public void testSendEmail2() {
		EmailTemplateEntity e = impl.newEmailTemplateEntity();
		e.setSubject("hello");
		e.setContent("world");
		EmailPlaceholderBean b = new EmailPlaceholderBean();

		MimeMessage mm = new MimeMessage((Session) null);
		EasyMock.expect(daomock.findById(1)).andReturn(e);
		EasyMock.expect(confservice.findAsString(EmailConstants.CONF_FROM_EMAIL_NAME)).andReturn("anything");
		EasyMock.expect(confservice.findAsString(EmailConstants.CONF_FROM_EMAIL_ADDRESS)).andReturn("anything");
		EasyMock.expect(confservice.findAsString(EmailConstants.CONF_PAGE_NAME)).andReturn("anything");
		EasyMock.expect(confservice.findAsString(EmailConstants.CONF_PAGE_NAME)).andReturn("anything");
		EasyMock.expect(emailmock.createMimeMessage()).andReturn(mm);
		emailmock.send(mm);
		EasyMock.replay(daomock);
		EasyMock.replay(confservice);
		EasyMock.replay(emailmock);
		impl.sendEmail(1, b);
		EasyMock.verify(emailmock);
	}
}
