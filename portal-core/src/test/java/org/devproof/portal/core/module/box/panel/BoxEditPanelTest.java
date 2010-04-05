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
package org.devproof.portal.core.module.box.panel;

import junit.framework.TestCase;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.core.module.box.entity.BoxEntity;
import org.devproof.portal.core.module.box.page.BoxPage;
import org.devproof.portal.test.PortalTestUtil;

/**
 * @author Carsten Hufe
 */
public class BoxEditPanelTest extends TestCase {
    private WicketTester tester;
    private static boolean calledSave = false;

    @Override
    public void setUp() throws Exception {
        calledSave = false;
        tester = PortalTestUtil.createWicketTesterWithSpringAndDatabase();
        PortalTestUtil.loginDefaultAdminUser(tester);
    }

    @Override
    protected void tearDown() throws Exception {
        PortalTestUtil.destroy(tester);
    }

    public void testRenderDefaultPanel() {
        tester.startPanel(TestBoxEditPanel.class);
        tester.assertComponent("panel", TestBoxEditPanel.class);
    }

    public void testSaveBox() {
        tester.startPanel(TestBoxEditPanel.class);
        tester.assertComponent("panel", TestBoxEditPanel.class);
        FormTester ft = tester.newFormTester("panel:form");
        ft.select("boxType", 7);
        ft.setValue("title", "mytitle");
        ft.setValue("content", "mycontent");
        tester.executeAjaxEvent("panel:form:saveButton", "onclick");
        tester.assertNoErrorMessage();
        assertTrue(calledSave);
        tester.startPage(BoxPage.class);
        tester.assertContains("mytitle");
    }

    public static class TestBoxEditPanel extends BoxEditPanel {
        private static final long serialVersionUID = 1L;

        public TestBoxEditPanel(String id) {
            super(id, Model.of(new BoxEntity()));
        }

        @Override
        public void onSave(AjaxRequestTarget target) {
            calledSave = true;
        }

        @Override
        public void onCancel(AjaxRequestTarget target) {
        }

    }
}
