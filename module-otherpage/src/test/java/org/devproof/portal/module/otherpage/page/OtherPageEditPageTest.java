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
package org.devproof.portal.module.otherpage.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.module.otherpage.entity.OtherPage;
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
        locations = {"classpath:/org/devproof/portal/module/otherpage/test-datasource.xml" })
public class OtherPageEditPageTest {
    @SuppressWarnings({"SpringJavaAutowiringInspection"})
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
        tester.startPage(createNewOtherPageEditPage());
        tester.assertRenderedPage(OtherPageEditPage.class);
    }

    @Test
    public void testSaveOtherPage() {
        callOtherPageEditPage();
        submitOtherPageForm();
        assertOtherPageViewPage();
    }

    private void callOtherPageEditPage() {
        tester.startPage(createNewOtherPageEditPage());
        tester.assertRenderedPage(OtherPageEditPage.class);
    }

    private OtherPageEditPage createNewOtherPageEditPage() {
        return new OtherPageEditPage(Model.of(new OtherPage()));
    }

    private void assertOtherPageViewPage() {
        String expectedMsgs[] = PortalTestUtil.getMessage("msg.saved", createNewOtherPageEditPage());
        tester.assertRenderedPage(OtherPageViewPage.class);
        tester.assertNoErrorMessage();
        tester.assertInfoMessages(expectedMsgs);
        tester.startPage(OtherPageViewPage.class, new PageParameters("0=cont_id"));
        tester.assertRenderedPage(OtherPageViewPage.class);
        tester.assertContains("testing content");
    }

    private void submitOtherPageForm() {
        FormTester form = tester.newFormTester("form");
        form.setValue("contentId", "cont_id");
        form.setValue("content", "testing content");
        form.submit();
    }
}
