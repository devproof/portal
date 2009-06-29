/*
 * Copyright 2009 Carsten Hufe devproof.org
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
		this.daomock = EasyMock.createStrictMock(EmailTemplateDao.class);
		this.emailmock = EasyMock.createStrictMock(JavaMailSender.class);
		this.confservice = EasyMock.createNiceMock(ConfigurationService.class);
		this.impl = new EmailServiceImpl();
		this.impl.setEmailTemplateDao(this.daomock);
		this.impl.setConfigurationService(this.confservice);
		this.impl.setJavaMailSender(this.emailmock);
		this.impl.setDateFormat(new SimpleDateFormat("dd-mm-yyyy"));
	}

	public void testSave() {
		EmailTemplateEntity e = this.impl.newEmailTemplateEntity();
		e.setId(1);
		this.daomock.save(e);
		EasyMock.replay(this.daomock);
		this.impl.save(e);
		EasyMock.verify(this.daomock);
	}

	public void testDelete() {
		EmailTemplateEntity e = this.impl.newEmailTemplateEntity();
		e.setId(1);
		this.daomock.delete(e);
		EasyMock.replay(this.daomock);
		this.impl.delete(e);
		EasyMock.verify(this.daomock);
	}

	public void testFindAll() {
		List<EmailTemplateEntity> list = new ArrayList<EmailTemplateEntity>();
		list.add(this.impl.newEmailTemplateEntity());
		list.add(this.impl.newEmailTemplateEntity());
		EasyMock.expect(this.daomock.findAll()).andReturn(list);
		EasyMock.replay(this.daomock);
		assertEquals(list, this.impl.findAll());
		EasyMock.verify(this.daomock);
	}

	public void testFindById() {
		EmailTemplateEntity e = this.impl.newEmailTemplateEntity();
		e.setId(1);
		EasyMock.expect(this.daomock.findById(1)).andReturn(e);
		EasyMock.replay(this.daomock);
		assertEquals(this.impl.findById(1), e);
		EasyMock.verify(this.daomock);
	}

	public void testNewEmailTemplateEntity() {
		assertNotNull(this.impl.newEmailTemplateEntity());
	}

	public void testSendEmail1() {
		EmailTemplateEntity e = this.impl.newEmailTemplateEntity();
		e.setSubject("hello");
		e.setContent("world");
		EmailPlaceholderBean b = new EmailPlaceholderBean();

		MimeMessage mm = new MimeMessage((Session) null);
		EasyMock.expect(this.confservice.findAsString(EmailConstants.CONF_FROM_EMAIL_NAME)).andReturn("anything");
		EasyMock.expect(this.confservice.findAsString(EmailConstants.CONF_FROM_EMAIL_ADDRESS)).andReturn("anything");
		EasyMock.expect(this.confservice.findAsString(EmailConstants.CONF_PAGE_NAME)).andReturn("anything");
		EasyMock.expect(this.confservice.findAsString(EmailConstants.CONF_PAGE_NAME)).andReturn("anything");
		EasyMock.expect(this.emailmock.createMimeMessage()).andReturn(mm);
		this.emailmock.send(mm);
		EasyMock.replay(this.confservice);
		EasyMock.replay(this.emailmock);
		this.impl.sendEmail(e, b);
		EasyMock.verify(this.emailmock);
	}

	public void testSendEmail2() {
		EmailTemplateEntity e = this.impl.newEmailTemplateEntity();
		e.setSubject("hello");
		e.setContent("world");
		EmailPlaceholderBean b = new EmailPlaceholderBean();

		MimeMessage mm = new MimeMessage((Session) null);
		EasyMock.expect(this.daomock.findById(1)).andReturn(e);
		EasyMock.expect(this.confservice.findAsString(EmailConstants.CONF_FROM_EMAIL_NAME)).andReturn("anything");
		EasyMock.expect(this.confservice.findAsString(EmailConstants.CONF_FROM_EMAIL_ADDRESS)).andReturn("anything");
		EasyMock.expect(this.confservice.findAsString(EmailConstants.CONF_PAGE_NAME)).andReturn("anything");
		EasyMock.expect(this.confservice.findAsString(EmailConstants.CONF_PAGE_NAME)).andReturn("anything");
		EasyMock.expect(this.emailmock.createMimeMessage()).andReturn(mm);
		this.emailmock.send(mm);
		EasyMock.replay(this.daomock);
		EasyMock.replay(this.confservice);
		EasyMock.replay(this.emailmock);
		this.impl.sendEmail(1, b);
		EasyMock.verify(this.emailmock);
	}
}
