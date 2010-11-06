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
package org.devproof.portal.core.module.contact.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.core.mock.EmailServiceMock;
import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.email.service.EmailService;
import org.devproof.portal.test.MockContextLoader;
import org.devproof.portal.test.PortalTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletContext;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

/**
 * @author Carsten Hufe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class,
        locations = {"classpath:/org/devproof/portal/core/test-datasource.xml" })
public class ContactPageTest {
    @Autowired
    private ServletContext servletContext;
    private WicketTester tester;

    @Before
    public void setUp() throws Exception {
        tester = PortalTestUtil.createWicketTester(servletContext);
        PortalTestUtil.loginDefaultAdminUser(tester);
    }

    @After
    public void tearDown() throws Exception {
        PortalTestUtil.destroy(tester);
    }

    @Test
    public void testRenderDefaultPage() {
        callContactPage();
    }

    @Test
    public void testSendContactForm() throws Exception {
        EmailServiceMock emailServiceMock = createEmailServiceMock();
        callContactPage();
        setEmailServiceMock(emailServiceMock);
        submitContactForm();
        assertEmail(emailServiceMock);
    }

    private void assertEmail(EmailServiceMock emailServiceMock) {
        EmailPlaceholderBean emailPlaceholderBean = emailServiceMock.getEmailPlaceholderBean();
        assertEquals("Max Power", emailPlaceholderBean.getContactFullname());
        assertEquals("max.power@no.domain", emailPlaceholderBean.getContactEmail());
        assertEquals("testing content more then 30 letters 1234567890", emailPlaceholderBean.getContent());
    }

    private void submitContactForm() {
        FormTester form = tester.newFormTester("form");
        form.setValue("fullname", "Max Power");
        form.setValue("email", "max.power@no.domain");
        form.setValue("content", "testing content more then 30 letters 1234567890");
        tester.executeAjaxEvent("form:sendButton", "onclick");
    }

    private void callContactPage() {
        tester.startPage(ContactPage.class, new PageParameters("0=admin"));
        tester.assertRenderedPage(ContactPage.class);
    }

    private void setEmailServiceMock(EmailService emailServiceMock) throws Exception {
        ContactPage lastRenderedPage = (ContactPage) tester.getLastRenderedPage();
        Field emailServiceField = ContactPage.class.getDeclaredField("emailService");
        emailServiceField.setAccessible(true);
        emailServiceField.set(lastRenderedPage, emailServiceMock);
    }

    private EmailServiceMock createEmailServiceMock() {
        return new EmailServiceMock();
    }
}
