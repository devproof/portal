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
package org.devproof.portal.core.module.user.page;

import junit.framework.TestCase;

import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.test.PortalTestUtil;

/**
 * @author Carsten Hufe
 */
public class SettingsPageTest extends TestCase {
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

	public void testRenderDefaultPage() {
		tester.startPage(SettingsPage.class);
		tester.assertRenderedPage(SettingsPage.class);
	}

	public void testSaveUserSettings() {
		tester.startPage(SettingsPage.class);
		tester.assertRenderedPage(SettingsPage.class);
		FormTester ft = tester.newFormTester("form");
		ft.setValue("firstname", "Peter");
		ft.setValue("lastname", "Pan");
		ft.submit("saveButton");
		tester.assertNoErrorMessage();
		tester.startPage(SettingsPage.class);
		tester.assertContains("Peter");
		tester.assertContains("Pan");
	}
}
