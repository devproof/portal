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
package org.devproof.portal.module.blog.page;

import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.module.blog.entity.Blog;
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
        locations = {"classpath:/org/devproof/portal/module/blog/test-datasource.xml" })
public class BlogEditPageTest {
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
        tester.startPage(createNewBlogEditPage());
        tester.assertRenderedPage(BlogEditPage.class);
    }

    private BlogEditPage createNewBlogEditPage() {
        return new BlogEditPage(Model.of(new Blog()));
    }

    @Test
    public void testSaveBlogEntry() {
        callBlogEditPage();
        submitBlogForm();
        assertBlogPage();
    }

    private void callBlogEditPage() {
        tester.startPage(createNewBlogEditPage());
        tester.assertRenderedPage(BlogEditPage.class);
    }

    @Test
    public void testEditBlogEntry() {
        navigateToBlogEditPage();
        submitBlogForm();
        assertBlogPage();
        String s = tester.getServletResponse().getDocument();
        assertFalse(s.contains("This is a sample blog entry."));
    }

    private void navigateToBlogEditPage() {
        tester.startPage(BlogPage.class);
        tester.assertRenderedPage(BlogPage.class);
        tester.assertContains("This is a sample blog entry.");
        tester.debugComponentTrees();
        tester.clickLink("refreshContainerBlogEntries:repeatingBlogEntries:2:blogView:authorButtons:editLink");
        tester.assertRenderedPage(BlogEditPage.class);
        tester.assertContains("This is a sample blog entry.");
    }

    private void assertBlogPage() {
        String expectedMsgs[] = PortalTestUtil.getMessage("msg.saved", createNewBlogEditPage());
        tester.assertRenderedPage(BlogPage.class);
        tester.assertInfoMessages(expectedMsgs);
        tester.startPage(BlogPage.class);
        tester.assertRenderedPage(BlogPage.class);
        tester.assertContains("testing headline");
        tester.assertContains("testing content");
    }

    private void submitBlogForm() {
        FormTester form = tester.newFormTester("form");
        form.setValue("tags", "these are tags");
        form.setValue("headline", "testing headline");
        form.setValue("content", "testing content");
        form.submit();
    }
}
