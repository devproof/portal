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
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.TestPanelSource;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.module.comment.config.DefaultCommentConfiguration;
import org.devproof.portal.test.PortalTestUtil;

/**
 * @author Carsten Hufe
 */
public class CommentPanelTest extends TestCase {
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
		tester.startPanel(createCommentPanel());
		tester.assertComponent("panel", CommentPanel.class);
	}

	public void testWriteComment() throws Exception {
		PortalTestUtil.loginDefaultAdminUser(tester);
		tester.startPanel(createCommentPanel());
		tester.assertComponent("panel", CommentPanel.class);
		FormTester ft = tester.newFormTester("panel:form");
		ft.setValue("comment", "I believe I can fly.");
		ft.submit();
		tester.clickLink("panel:form:addCommentButton", true);
		tester.assertNoErrorMessage();
		tester.assertInvisible("panel:form");
		tester.assertContains("I believe I can fly.");
	}

	private TestPanelSource createCommentPanel() {
		return new TestPanelSource() {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel getTestPanel(String panelId) {
				return new CommentPanel(panelId, new TestCommentConfiguration());
			}
		};
	}

	private static class TestCommentConfiguration extends DefaultCommentConfiguration {
		private static final long serialVersionUID = 1L;

		public TestCommentConfiguration() {
			setModuleContentId("contentid");
			setModuleName("modulename");
		}

		@Override
		public boolean isAllowedToView() {
			return true;
		}

		@Override
		public boolean isAllowedToWrite() {
			return true;
		}
	}
}
