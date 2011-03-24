/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.module.bookmark.page;

import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.module.bookmark.entity.Bookmark;
import org.devproof.portal.test.MockContextLoader;
import org.devproof.portal.test.PortalTestUtil;
import org.junit.After;
import org.junit.Assert;
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
        locations = {"classpath:/org/devproof/portal/module/bookmark/test-datasource.xml" })
public class BookmarkEditPageTest {
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
        tester.startPage(getNewBookmarkEditPage());
        tester.assertRenderedPage(BookmarkEditPage.class);

    }

    @Test
    public void testSaveBookmark() {
        callBookmarkEditPage();
        submitBookmarkForm();
        assertBookmarkPage();
    }

    @Test
    public void testEditBookmark() {
        navigateToBookmarkEditPage();
        submitBookmarkForm();
        assertBookmarkPage();
        Assert.assertFalse(tester.getServletResponse().getDocument().contains("This a sample bookmark and refers to devproof.org"));
    }

    private void callBookmarkEditPage() {
        tester.startPage(getNewBookmarkEditPage());
        tester.assertRenderedPage(BookmarkEditPage.class);
    }

    private BookmarkEditPage getNewBookmarkEditPage() {
        return new BookmarkEditPage(Model.of(new Bookmark()));
    }

    private void submitBookmarkForm() {
        FormTester form = tester.newFormTester("form");
        form.setValue("title", "testing title");
        form.setValue("description", "testing description");
        form.setValue("url", "http://www.devproof.org");
        form.submit();
    }

    private void navigateToBookmarkEditPage() {
        tester.startPage(BookmarkPage.class);
        tester.assertRenderedPage(BookmarkPage.class);
        tester.assertContains("This a sample bookmark and refers to devproof.org");
        PortalTestUtil.callOnBeginRequest();
        tester.clickLink("refreshContainerBookmarks:repeatingBookmarks:1:bookmarkView:authorButtons:editLink");
        tester.assertRenderedPage(BookmarkEditPage.class);
    }

    private void assertBookmarkPage() {
        String expectedMsgs[] = PortalTestUtil.getMessage("msg.saved", getNewBookmarkEditPage());
        tester.assertRenderedPage(BookmarkPage.class);
        tester.assertInfoMessages(expectedMsgs);
        tester.startPage(BookmarkPage.class);
        tester.assertRenderedPage(BookmarkPage.class);
        tester.assertContains("testing title");
        tester.assertContains("testing description");
    }
}
