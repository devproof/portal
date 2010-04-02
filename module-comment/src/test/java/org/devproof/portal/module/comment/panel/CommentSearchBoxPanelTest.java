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
package org.devproof.portal.module.comment.panel;

import junit.framework.TestCase;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.TestPanelSource;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.module.comment.query.CommentQuery;
import org.devproof.portal.test.PortalTestUtil;

/**
 * @author Carsten Hufe
 */
public class CommentSearchBoxPanelTest extends TestCase {
	private WicketTester tester;

	@Override
	public void setUp() throws Exception {
		tester = PortalTestUtil.createWicketTesterWithSpringAndDatabase("create_tables_hsql_comment.sql",
				"insert_comment.sql");
	}

	@Override
	protected void tearDown() throws Exception {
		PortalTestUtil.destroy(tester);
	}

	public void testRenderDefaultPanel() {
		tester.startPanel(createCommentSearchBoxPanel());
		tester.assertComponent("panel", CommentSearchBoxPanel.class);
	}

	private TestPanelSource createCommentSearchBoxPanel() {
		return new TestPanelSource() {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel getTestPanel(String panelId) {
				return new TestCommentSearchBoxPanel(panelId, new CommentQuery());
			}
		};
	}

	protected static class TestCommentSearchBoxPanel extends CommentSearchBoxPanel {
		private static final long serialVersionUID = 1L;

		public TestCommentSearchBoxPanel(String id, CommentQuery query) {
			super(id, Model.of(query));
		}

		@Override
		protected void onSubmit(AjaxRequestTarget target) {

		}
	}
}
