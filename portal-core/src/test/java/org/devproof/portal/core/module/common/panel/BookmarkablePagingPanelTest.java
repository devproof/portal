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
package org.devproof.portal.core.module.common.panel;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.ITestPanelSource;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.core.module.common.query.SearchQuery;
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
public class BookmarkablePagingPanelTest {
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
    public void testRenderDefaultPanel() {
        tester.startPanel(createBookmarkablePagingPanel());
        tester.assertComponent("panel", BookmarkablePagingPanel.class);
    }

    private ITestPanelSource createBookmarkablePagingPanel() {
        return new ITestPanelSource() {
            private static final long serialVersionUID = 1L;

            @Override
            public Panel getTestPanel(String panelId) {
                return new BookmarkablePagingPanel(panelId, new TestIPageable(), Model.of(new TestSearchQuery()), WebPage.class);
            }
        };
    }

    private static class TestIPageable implements IPageable {
        private static final long serialVersionUID = 1L;

        @Override
        public int getCurrentPage() {
            return 0;
        }

        @Override
        public int getPageCount() {
            return 0;
        }

        @Override
        public void setCurrentPage(int page) {
        }
    }

    private static class TestSearchQuery implements SearchQuery {
        private static final long serialVersionUID = 1L;

        @Override
        public PageParameters getPageParameters() {
            return new PageParameters();
        }
    }
}
