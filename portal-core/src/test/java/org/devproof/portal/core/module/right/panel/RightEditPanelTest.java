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
package org.devproof.portal.core.module.right.panel;

import junit.framework.TestCase;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.test.PortalTestUtil;

/**
 * @author Carsten Hufe
 */
public class RightEditPanelTest extends TestCase {
	private WicketTester tester;

	// private static boolean calledSave = false;

	@Override
	public void setUp() throws Exception {
		// calledSave = false;
		tester = PortalTestUtil.createWicketTesterWithSpringAndDatabase();
		PortalTestUtil.loginDefaultAdminUser(tester);
	}

	@Override
	protected void tearDown() throws Exception {
		PortalTestUtil.destroy(tester);
	}

	public void testRenderDefaultPanel() {
		tester.startPanel(TestRightEditPanel.class);
		tester.assertComponent("panel", TestRightEditPanel.class);
	}

	/*
	 * Palette seems to have a bug, so it is not testable with the WicketTester
	 */
	public void testSaveRight() {
		// tester.startPanel(TestRightEditPanel.class);
		// tester.assertComponent("panel", RightEditPanel.class);
		// FormTester ft = tester.newFormTester("panel:form");
		// ft.setValue("right", "myrightname");
		// ft.setValue("description", "myrightdescription");
		// tester.clickLink("panel:form:saveButton", true);
		// tester.assertNoErrorMessage();
		// assertTrue(calledSave);
		// tester.startPage(RightPage.class);
		// tester.assertContains("myrightname");
		// tester.assertContains("myrightdescription");
	}

	public static class TestRightEditPanel extends RightEditPanel {
		private static final long serialVersionUID = 1L;

		public TestRightEditPanel(String id) {
			super(id, Model.of(new RightEntity()), true);
		}

		@Override
		public void onSave(AjaxRequestTarget target) {
			// calledSave = true;
		}

		@Override
		public void onCancel(AjaxRequestTarget target) {
		}
	}
}
