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
package org.devproof.portal.module.uploadcenter.panel;

import junit.framework.TestCase;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.test.PortalTestUtil;

import java.io.File;

/**
 * @author Carsten Hufe
 */
public class UploadCenterPanelTest extends TestCase {
    private WicketTester tester;

    @Override
    public void setUp() throws Exception {
        tester = PortalTestUtil.createWicketTesterWithSpringAndDatabase("insert_uploadcenter.sql");
        PortalTestUtil.loginDefaultAdminUser(tester);
    }

    @Override
    protected void tearDown() throws Exception {
        PortalTestUtil.destroy(tester);
    }

    public void testRenderDefaultPanel() {
        tester.startPanel(TestUploadCenterPanel.class);
        tester.assertComponent("panel", TestUploadCenterPanel.class);
    }

    public static class TestUploadCenterPanel extends UploadCenterPanel {
        public TestUploadCenterPanel(String id) {
            super(id, Model.of(new File(".")), new BubblePanel("foobar"));
        }

        private static final long serialVersionUID = 1L;

        @Override
        public void onDelete(AjaxRequestTarget target) {
        }
    }
}
