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
package org.devproof.portal.module.article.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.module.article.entity.ArticleEntity;
import org.devproof.portal.test.PortalTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Carsten Hufe
 */
public class ArticleEditPageTest {
    private WicketTester tester;

    @Before
    public void setUp() throws Exception {
        tester = PortalTestUtil.createWicketTesterWithSpringAndDatabase("create_tables_hsql_article.sql", "insert_article.sql");
        PortalTestUtil.loginDefaultAdminUser(tester);
    }

    @After
    public void tearDown() throws Exception {
        PortalTestUtil.destroy(tester);
    }

    @Test
    public void testRenderDefaultPage() {
        tester.startPage(getNewArticleEditPage());
        tester.assertRenderedPage(ArticleEditPage.class);
    }

    @Test
    public void testSaveArticle() {
        callArticleEditPage();
        submitArticleForm();
        assertArticleViewPage("0=testing_content_id");
        assertArticlePage();
    }

    @Test
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
        tester.clickLink("repeatingArticles:1:articleView:authorButtons:editLink");
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

    private void assertArticleViewPage(String contentId) {
        String expectedMsgs[] = PortalTestUtil.getMessage("msg.saved", ArticleEditPage.class);
        tester.assertRenderedPage(ArticleReadPage.class);
        tester.assertInfoMessages(expectedMsgs);
        tester.startPage(ArticleReadPage.class, new PageParameters(contentId));
        tester.assertRenderedPage(ArticleReadPage.class);
        tester.assertContains("testing title");
        tester.assertContains("testing content");
    }

    private ArticleEditPage getNewArticleEditPage() {
        return new ArticleEditPage(Model.of(new ArticleEntity()));
    }

    private void submitArticleForm() {
        PortalTestUtil.callOnBeginRequest();
        FormTester form = tester.newFormTester("form");
        form.setValue("tags", "these are tags");
        form.setValue("title", "testing title");
        form.setValue("teaser", "testing teaser");
        form.setValue("contentId", "testing_content_id");
        form.setValue("fullArticle", "testing content");
        form.submit();
    }

}
