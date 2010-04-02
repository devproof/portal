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
package org.devproof.portal.core.module.common.panel;

import junit.framework.TestCase;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.TestPanelSource;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.core.module.common.query.SearchQuery;
import org.devproof.portal.test.PortalTestUtil;

/**
 * @author Carsten Hufe
 */
public class BookmarkablePagingPanelTest extends TestCase {
	private WicketTester tester;

	@Override
	public void setUp() throws Exception {
		tester = PortalTestUtil.createWicketTesterWithSpringAndDatabase();
		PortalTestUtil.loginDefaultAdminUser(tester);
	}

	@Override
	protected void tearDown() throws Exception {
		PortalTestUtil.destroy(tester);
	}

	public void testRenderDefaultPanel() {
		tester.startPanel(createBookmarkablePagingPanel());
		tester.assertComponent("panel", BookmarkablePagingPanel.class);
	}

	private TestPanelSource createBookmarkablePagingPanel() {
		return new TestPanelSource() {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel getTestPanel(String panelId) {
				return new BookmarkablePagingPanel(panelId, new TestIPageable(), Model.of(new TestSearchQuery()),
						WebPage.class);
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
