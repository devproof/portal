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
package org.devproof.portal.core.module.user.panel;

import junit.framework.TestCase;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.page.UserPage;
import org.devproof.portal.test.PortalTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Carsten Hufe
 */
public class UserEditPanelTest {
    private WicketTester tester;

    @Before
    public void setUp() throws Exception {
        tester = PortalTestUtil.createWicketTesterWithSpringAndDatabase();
        PortalTestUtil.loginDefaultAdminUser(tester);
    }

    @After
    public void tearDown() throws Exception {
        PortalTestUtil.destroy(tester);
    }

    @Test
    public void testRenderDefaultPanel() {
        tester.startPanel(TestUserEditPanel.class);
        tester.assertComponent("panel", TestUserEditPanel.class);
    }

    @Test
    public void testSaveUserTestCase() {
        tester.startPanel(TestUserEditPanel.class);
        tester.assertComponent("panel", TestUserEditPanel.class);
        FormTester ft = tester.newFormTester("panel:form");
        ft.setValue("firstname", "Peter");
        ft.setValue("lastname", "Pan");
        ft.setValue("email", "peterpan@email.tld");
        ft.select("role", 1);
        ft.setValue("password1", "testing");
        ft.setValue("password2", "testing");
        tester.executeAjaxEvent("panel:form:saveButton", "onclick");
        tester.assertNoErrorMessage();
        tester.startPage(UserPage.class);
        tester.assertContains("Peter");
        tester.assertContains("Pan");
    }

    public static class TestUserEditPanel extends UserEditPanel {
        public TestUserEditPanel(String id) {
            super(id, Model.of(new UserEntity()), true);
        }

        private static final long serialVersionUID = 1L;

        @Override
        public void onSave(AjaxRequestTarget target) {

        }

        @Override
        public void onCancel(AjaxRequestTarget target) {

        }
    }
}
