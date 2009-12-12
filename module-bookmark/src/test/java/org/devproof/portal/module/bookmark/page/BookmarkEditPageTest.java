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
package org.devproof.portal.module.bookmark.page;

import junit.framework.TestCase;

import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity;
import org.devproof.portal.test.PortalTestUtil;

/**
 * @author Carsten Hufe
 */
public class BookmarkEditPageTest extends TestCase {
	private WicketTester tester;

	@Override
	public void setUp() throws Exception {
		tester = PortalTestUtil.createWicketTesterWithSpringAndDatabase("create_tables_hsql_bookmark.sql",
				"insert_bookmark.sql");
		PortalTestUtil.loginDefaultAdminUser(tester);
	}

	@Override
	protected void tearDown() throws Exception {
		PortalTestUtil.destroy(tester);
	}

	public void testRenderDefaultPage() {
		tester.startPage(getNewBookmarkEditPage());
		tester.assertRenderedPage(BookmarkEditPage.class);

	}

	public void testSaveBookmark() {
		callBookmarkEditPage();
		submitBookmarkForm();
		assertBookmarkPage();
	}

	public void testEditBookmark() {
		navigateToBookmarkEditPage();
		submitBookmarkForm();
		assertBookmarkPage();
		assertFalse(tester.getServletResponse().getDocument().contains(
				"This a sample bookmark and refers to devproof.org"));
	}

	private void callBookmarkEditPage() {
		tester.startPage(getNewBookmarkEditPage());
		tester.assertRenderedPage(BookmarkEditPage.class);
	}

	private BookmarkEditPage getNewBookmarkEditPage() {
		return new BookmarkEditPage(new BookmarkEntity());
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
		tester.clickLink("listBookmark:1:bookmarkView:authorButtons:editLink");
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
