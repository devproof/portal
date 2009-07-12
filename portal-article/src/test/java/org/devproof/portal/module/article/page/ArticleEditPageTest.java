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
		this.tester = PortalTestUtil.createWicketTesterWithSpringAndDatabase("create_tables_hsql_article.sql", "insert_article.sql");
		PortalTestUtil.loginDefaultAdminUser(this.tester);
	}

	@Override
	protected void tearDown() throws Exception {
		PortalTestUtil.destroy(this.tester);
	}

	public void testRenderDefaultPage() {
		this.tester.startPage(new ArticleEditPage(new ArticleEntity()));
		this.tester.assertRenderedPage(ArticleEditPage.class);
	}

	public void testSaveArticle() {
		this.tester.startPage(new ArticleEditPage(new ArticleEntity()));
		this.tester.assertRenderedPage(ArticleEditPage.class);
		String expectedMsgs[] = new String[] { this.tester.getLastRenderedPage().getString("msg.saved") };
		FormTester form = this.tester.newFormTester("form");
		form.setValue("tags", "these are tags");
		form.setValue("title", "testing title");
		form.setValue("teaser", "testing teaser");
		form.setValue("contentId", "testing_content_id");
		form.setValue("content", "testing content");
		form.submit();
		this.tester.assertRenderedPage(ArticleViewPage.class);
		this.tester.assertInfoMessages(expectedMsgs);
		this.tester.startPage(ArticleViewPage.class, new PageParameters("0=testing_content_id"));
		this.tester.assertRenderedPage(ArticleViewPage.class);
		this.tester.assertContains("testing title");
		this.tester.assertContains("testing content");
		this.tester.startPage(ArticlePage.class);
		this.tester.assertRenderedPage(ArticlePage.class);
		this.tester.assertContains("testing title");
		this.tester.assertContains("testing teaser");
	}

	public void testEditArticle() {
		this.tester.startPage(ArticlePage.class);
		this.tester.assertRenderedPage(ArticlePage.class);
		this.tester.assertContains("This is a sample article and this is the teaser");
		this.tester.clickLink("listArticle:1:articleView:authorButtons:editLink");
		this.tester.assertRenderedPage(ArticleEditPage.class);
		String expectedMsgs[] = new String[] { this.tester.getLastRenderedPage().getString("msg.saved") };
		FormTester form = this.tester.newFormTester("form");
		form.setValue("tags", "these are tags");
		form.setValue("title", "testing title");
		form.setValue("teaser", "testing teaser");
		form.setValue("contentId", "testing_content_id");
		form.setValue("content", "testing content");
		form.submit();
		this.tester.assertRenderedPage(ArticleViewPage.class);
		this.tester.assertInfoMessages(expectedMsgs);
		this.tester.startPage(ArticleViewPage.class, new PageParameters("0=Sample_article"));
		this.tester.assertRenderedPage(ArticleViewPage.class);
		this.tester.assertContains("testing title");
		this.tester.assertContains("testing content");
		this.tester.startPage(ArticlePage.class);
		this.tester.assertRenderedPage(ArticlePage.class);
		this.tester.assertContains("testing title");
		this.tester.assertContains("testing teaser");
	}
}
