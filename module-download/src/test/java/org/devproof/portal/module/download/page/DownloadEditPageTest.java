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
package org.devproof.portal.module.download.page;

import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.module.download.entity.Download;
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

import static org.junit.Assert.assertFalse;

/**
 * @author Carsten Hufe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class,
        locations = {"classpath:/org/devproof/portal/module/download/test-datasource.xml" })
public class DownloadEditPageTest {
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
        tester.startPage(new DownloadEditPage(Model.of(new Download())));
        tester.assertRenderedPage(DownloadEditPage.class);
    }

    @Test
    public void testSaveDownload() {
        callDownloadEditPage();
        submitDownloadForm();
        assertDownloadPage();
    }

    @Test
    public void testEditDownload() {
        navigateToDownloadEditPage();
        submitDownloadForm();
        assertDownloadPage();
        assertFalse(tester.getServletResponse().getDocument().contains("This is a sample."));
    }

    private void callDownloadEditPage() {
        tester.startPage(getNewDownloadEditPage());
        tester.assertRenderedPage(DownloadEditPage.class);
    }

    private DownloadEditPage getNewDownloadEditPage() {
        return new DownloadEditPage(Model.of(new Download()));
    }

    private void submitDownloadForm() {
        FormTester form = tester.newFormTester("form");
        form.setValue("title", "testing title");
        form.setValue("description", "testing description");
        form.setValue("url", "http://www.devproof.org/download");
        form.submit();
    }

    private void navigateToDownloadEditPage() {
        tester.startPage(DownloadPage.class);
        tester.assertRenderedPage(DownloadPage.class);
        tester.assertContains("This is a sample.");
        PortalTestUtil.callOnBeginRequest();
        tester.clickLink("repeatingDownloads:1:downloadView:authorButtons:editLink");
        tester.assertRenderedPage(DownloadEditPage.class);
    }

    private void assertDownloadPage() {
        String expectedMsgs[] = PortalTestUtil.getMessage("msg.saved", getNewDownloadEditPage());
        tester.assertRenderedPage(DownloadPage.class);
        tester.assertInfoMessages(expectedMsgs);
        tester.startPage(DownloadPage.class);
        tester.assertRenderedPage(DownloadPage.class);
        tester.assertContains("testing title");
        tester.assertContains("testing description");
    }
}
