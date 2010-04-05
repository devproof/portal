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
package org.devproof.portal.module.comment.panel;

import junit.framework.TestCase;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.tester.TestPanelSource;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.module.comment.config.DefaultCommentConfiguration;
import org.devproof.portal.test.PortalTestUtil;

/**
 * @author Carsten Hufe
 */
public class ExpandableCommentInfoPanelTest extends TestCase {
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

	public void testRenderDefaultPanel_withRight() {
		tester.startPanel(createExpandableCommentPanel(true));
		tester.assertComponent("panel", ExpandableCommentPanel.class);
	}

	public void testRenderDefaultPanel_withoutRight() {
		tester.startPanel(createExpandableCommentPanel(false));
		tester.assertInvisible("panel");
	}

	private TestPanelSource createExpandableCommentPanel(final boolean allowedToView) {
		return new TestPanelSource() {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel getTestPanel(String panelId) {
				return new ExpandableCommentPanel(panelId, new TestCommentConfiguration(allowedToView));
			}
		};
	}

	private static class TestCommentConfiguration extends DefaultCommentConfiguration {
		private static final long serialVersionUID = 1L;
		private boolean allowedToView;

		public TestCommentConfiguration(boolean allowedToView) {
			this.allowedToView = allowedToView;
			setModuleContentId("contentid");
			setModuleName("modulename");
		}

		@Override
		public boolean isAllowedToView() {
			return allowedToView;
		}

		@Override
		public boolean isAllowedToWrite() {
			return true;
		}
	}
}
