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
package org.devproof.portal.module.bookmark.panel;

import junit.framework.TestCase;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.TestPanelSource;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.module.bookmark.query.BookmarkQuery;
import org.devproof.portal.test.PortalTestUtil;

/**
 * @author Carsten Hufe
 */
public class BookmarkSearchBoxPanelTest extends TestCase {
	private WicketTester tester;

	@Override
	public void setUp() throws Exception {
		tester = PortalTestUtil.createWicketTesterWithSpringAndDatabase("create_tables_hsql_bookmark.sql",
				"insert_bookmark.sql");
	}

	@Override
	protected void tearDown() throws Exception {
		PortalTestUtil.destroy(tester);
	}

	public void testRenderDefaultPanel() {
		tester.startPanel(createBookmarkSearchBoxPanel());
		tester.assertComponent("panel", BookmarkSearchBoxPanel.class);
	}

	private TestPanelSource createBookmarkSearchBoxPanel() {
		return new TestPanelSource() {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel getTestPanel(String panelId) {
				return new TestBookmarkSearchBoxPanel(panelId, Model.of(new BookmarkQuery()));
			}
		};
	}

	private static class TestBookmarkSearchBoxPanel extends BookmarkSearchBoxPanel {
		private static final long serialVersionUID = 1L;

		private TestBookmarkSearchBoxPanel(String id, IModel<BookmarkQuery> queryModel) {
			super(id, queryModel);
		}

		@Override
		protected boolean isAuthor() {
			return true;
		}
	}
}
