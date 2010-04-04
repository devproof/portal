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

import junit.framework.TestCase;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.email.EmailConstants;
import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.email.dao.EmailTemplateDao;
import org.devproof.portal.core.module.email.entity.EmailTemplateEntity;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;

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
		daomock = createStrictMock(EmailTemplateDao.class);
		emailmock = createStrictMock(JavaMailSender.class);
		confservice = createNiceMock(ConfigurationService.class);
		impl = new EmailServiceImpl();
		impl.setEmailTemplateDao(daomock);
		impl.setConfigurationService(confservice);
		impl.setJavaMailSender(emailmock);
		impl.setDateFormat(new SimpleDateFormat("dd-mm-yyyy"));
	}

	public void testSave() {
		EmailTemplateEntity e = impl.newEmailTemplateEntity();
		e.setId(1);
		expect(daomock.save(e)).andReturn(e);
		replay(daomock);
		impl.save(e);
		verify(daomock);
	}

	public void testDelete() {
		EmailTemplateEntity e = impl.newEmailTemplateEntity();
		e.setId(1);
		daomock.delete(e);
		replay(daomock);
		impl.delete(e);
		verify(daomock);
	}

	public void testFindAll() {
		List<EmailTemplateEntity> list = new ArrayList<EmailTemplateEntity>();
		list.add(impl.newEmailTemplateEntity());
		list.add(impl.newEmailTemplateEntity());
		expect(daomock.findAll()).andReturn(list);
		replay(daomock);
		assertEquals(list, impl.findAll());
		verify(daomock);
	}

	public void testFindById() {
		EmailTemplateEntity e = impl.newEmailTemplateEntity();
		e.setId(1);
		expect(daomock.findById(1)).andReturn(e);
		replay(daomock);
		assertEquals(impl.findById(1), e);
		verify(daomock);
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

	public void testSendEmail2() {
		EmailTemplateEntity e = impl.newEmailTemplateEntity();
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
