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
package org.devproof.portal.module.article.page;

import junit.framework.TestCase;

import org.apache.wicket.PageParameters;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.module.article.entity.ArticleEntity;
import org.devproof.portal.test.PortalTestUtil;

/**
 * @author Carsten Hufe
 */
public class ArticleEditPageTest extends TestCase {
	private WicketTester tester;

	@Override
	public void setUp() throws Exception {
		tester = PortalTestUtil.createWicketTesterWithSpringAndDatabase("create_tables_hsql_article.sql",
				"insert_article.sql");
		PortalTestUtil.loginDefaultAdminUser(tester);
	}

	@Override
	protected void tearDown() throws Exception {
		PortalTestUtil.destroy(tester);
	}

	public void testRenderDefaultPage() {
		tester.startPage(getNewArticleEditPage());
		tester.assertRenderedPage(ArticleEditPage.class);
	}

	public void testSaveArticle() {
		callArticleEditPage();
		submitArticleForm();
		assertArticleViewPage("0=testing_content_id");
		assertArticlePage();
	}

	public void testEditArticle() {
		navigateToArticleEditPage();
		submitArticleForm();
		assertArticleViewPage("0=Sample_article");
		assertArticlePage();
	}

	private void navigateToArticleEditPage() {
		tester.startPage(ArticlePage.class);
		tester.assertRenderedPage(ArticlePage.class);
		tester.assertContains("This is a sample article and this is the teaser");
		tester.clickLink("listArticle:1:articleView:authorButtons:editLink");
		tester.assertRenderedPage(ArticleEditPage.class);
	}

	private void callArticleEditPage() {
		tester.startPage(getNewArticleEditPage());
		tester.assertRenderedPage(ArticleEditPage.class);
	}

	private void assertArticlePage() {
		tester.startPage(ArticlePage.class);
		tester.assertRenderedPage(ArticlePage.class);
		tester.assertContains("testing title");
		tester.assertContains("testing teaser");
	}

	private void assertArticleViewPage(final String contentId) {
		String expectedMsgs[] = PortalTestUtil.getMessage("msg.saved", getNewArticleEditPage());
		tester.assertRenderedPage(ArticleReadPage.class);
		tester.assertInfoMessages(expectedMsgs);
		tester.startPage(ArticleReadPage.class, new PageParameters(contentId));
		tester.assertRenderedPage(ArticleReadPage.class);
		tester.assertContains("testing title");
		tester.assertContains("testing content");
	}

	private ArticleEditPage getNewArticleEditPage() {
		return new ArticleEditPage(new ArticleEntity());
	}

	private void submitArticleForm() {
		FormTester form = tester.newFormTester("form");
		form.setValue("tags", "these are tags");
		form.setValue("title", "testing title");
		form.setValue("teaser", "testing teaser");
		form.setValue("contentId", "testing_content_id");
		form.setValue("fullArticle", "testing content");
		form.submit();
	}

}
