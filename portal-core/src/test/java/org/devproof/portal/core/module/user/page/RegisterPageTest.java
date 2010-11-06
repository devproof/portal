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
package org.devproof.portal.core.module.user.page;

import org.apache.wicket.Page;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.right.entity.Right;
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

/**
 * @author Carsten Hufe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class,
        locations = {"classpath:/org/devproof/portal/core/test-datasource.xml" })
public class RegisterPageTest {
    @Autowired
    private ServletContext servletContext;
    private WicketTester tester;

    @Before
    public void setUp() throws Exception {
        tester = PortalTestUtil.createWicketTester(servletContext);
    }

    @After
    public void tearDown() throws Exception {
        PortalTestUtil.destroy(tester);
    }

    @Test
    public void testRenderDefaultPage() {
        tester.startPage(RegisterPage.class);
        tester.assertRenderedPage(RegisterPage.class);
    }

    @Test
    public void testRegistration() {
        Page page = tester.startPage(RegisterPage.class);
        tester.assertRenderedPage(RegisterPage.class);
        PortalSession.get().getRights().add(new Right("captcha.disabled"));
        FormTester ft = tester.newFormTester("form");
        ft.setValue("username", "peterpan123");
        ft.setValue("firstname", "mike");
        ft.setValue("lastname", "jack");
        ft.setValue("email", "mike.jack@email.tld");
        ft.setValue("birthday", "1981-10-13");
        ft.setValue("password1", "testing");
        ft.setValue("password2", "testing");
        ft.setValue("termsOfUse", true);
        tester.executeAjaxEvent("form:registerButton", "onclick");
        tester.assertNoErrorMessage();
        tester.assertRenderedPage(MessagePage.class);
        tester.assertContains(page.getString("confirm.email"));
    }
}
