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
package org.devproof.portal.core.module.role.panel;

import junit.framework.TestCase;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.test.PortalTestUtil;

/**
 * @author Carsten Hufe
 */
public class RoleEditPanelTest extends TestCase {
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
		tester.startPanel(TestRoleEditPanel.class);
		tester.assertComponent("panel", TestRoleEditPanel.class);
	}

	public static class TestRoleEditPanel extends RoleEditPanel {
		private static final long serialVersionUID = 1L;

		public TestRoleEditPanel(String id) {
			super(id, Model.of(new RoleEntity()), true);
		}

		@Override
		public void onSave(AjaxRequestTarget target) {

		}

		@Override
		public void onCancel(AjaxRequestTarget target) {
			// TODO Auto-generated method stub

		}
	}
}
