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
package org.devproof.portal.module.blog.page;

import junit.framework.TestCase;

import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.module.blog.entity.BlogEntity;
import org.devproof.portal.test.PortalTestUtil;

/**
 * @author Carsten Hufe
 */
public class BlogEditPageTest extends TestCase {
	private WicketTester tester;

	@Override
	public void setUp() throws Exception {
		this.tester = PortalTestUtil.createWicketTesterWithSpringAndDatabase("create_tables_hsql_blog.sql",
				"insert_blog.sql");
		PortalTestUtil.loginDefaultAdminUser(this.tester);
	}

	@Override
	protected void tearDown() throws Exception {
		PortalTestUtil.destroy(this.tester);
	}

	public void testRenderDefaultPage() {
		this.tester.startPage(getNewBlogEditPage());
		this.tester.assertRenderedPage(BlogEditPage.class);
	}

	private BlogEditPage getNewBlogEditPage() {
		return new BlogEditPage(new BlogEntity());
	}

	public void testSaveBlogEntry() {
		callBlogEditPage();
		submitBlogForm();
		assertBlogPage();
	}

	private void callBlogEditPage() {
		this.tester.startPage(getNewBlogEditPage());
		this.tester.assertRenderedPage(BlogEditPage.class);
	}

	public void testEditBlogEntry() {
		navigateToBlogEditPage();
		submitBlogForm();
		assertBlogPage();
		assertFalse(this.tester.getServletResponse().getDocument().contains("this is a sample blog entry"));
	}

	private void navigateToBlogEditPage() {
		this.tester.startPage(BlogPage.class);
		this.tester.assertRenderedPage(BlogPage.class);
		this.tester.assertContains("this is a sample blog entry");
		this.tester.clickLink("listBlog:1:blogView:authorButtons:editLink");
		this.tester.assertRenderedPage(BlogEditPage.class);
	}

	private void assertBlogPage() {
		String expectedMsgs[] = PortalTestUtil.getMessage("msg.saved", getNewBlogEditPage());
		this.tester.assertRenderedPage(BlogPage.class);
		this.tester.assertInfoMessages(expectedMsgs);
		this.tester.startPage(BlogPage.class);
		this.tester.assertRenderedPage(BlogPage.class);
		this.tester.assertContains("testing headline");
		this.tester.assertContains("testing content");
	}

	private void submitBlogForm() {
		FormTester form = this.tester.newFormTester("form");
		form.setValue("tags", "these are tags");
		form.setValue("headline", "testing headline");
		form.setValue("content", "testing content");
		form.submit();
	}
}
