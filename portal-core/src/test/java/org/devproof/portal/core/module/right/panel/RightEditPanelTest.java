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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.test.MockContextLoader;
import org.devproof.portal.test.PortalTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletContext;

/**
 * @author Carsten Hufe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class,
        locations = {"classpath:/org/devproof/portal/core/test-datasource.xml" })
public class RightEditPanelTest {
    @Autowired
    private ServletContext servletContext;
    private WicketTester tester;

    // private static boolean calledSave = false;

    @Before
    public void setUp() throws Exception {
        // calledSave = false;
        tester = PortalTestUtil.createWicketTester(servletContext);
        PortalTestUtil.loginDefaultAdminUser(tester);
    }

    @After
    public void tearDown() throws Exception {
        PortalTestUtil.destroy(tester);
    }

    @Test
    public void testRenderDefaultPanel() {
        tester.startPanel(TestRightEditPanel.class);
        tester.assertComponent("panel", TestRightEditPanel.class);
    }

    /*
      * Palette seems to have a bug, so it is not testable with the WicketTester
      */

    @Test
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
            super(id, Model.of(new Right()), true);
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
