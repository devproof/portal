/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.module.deadlinkcheck.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.module.deadlinkcheck.entity.BaseLink;
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
import java.util.ArrayList;

/**
 * @author Carsten Hufe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class,
        locations = {"classpath:/org/devproof/portal/module/deadlinkcheck/test-datasource.xml" })
public class DeadLinkCheckPanelTest {
    @Autowired
    private ServletContext servletContext;
    private WicketTester tester;

    @Before
    public void setUp() throws Exception {
        tester = PortalTestUtil.createWicketTester(servletContext);
    }

    @After
    public void tearDown() throws Exception {
        PortalTestUtil.destroy(tester);
    }

    @Test
    public void testRenderDefaultPanel() {
        tester.startComponentInPage(TestDeadLinkCheckPanel.class);
        tester.assertComponent("", TestDeadLinkCheckPanel.class);
    }

    public static class TestDeadLinkCheckPanel extends DeadlinkCheckPanel<TestLinkEntity> {
        public TestDeadLinkCheckPanel(String id) {
            super(id, "download", new ListModel<TestLinkEntity>(new ArrayList<TestLinkEntity>()));
        }

        private static final long serialVersionUID = 1L;

        @Override
        public void onBroken(TestLinkEntity brokenEntity) {
        }

        @Override
        public void onValid(TestLinkEntity validEntity) {
        }

        @Override
        public void onCancel(AjaxRequestTarget target) {

        }
    }

    private static class TestLinkEntity extends BaseLink {
        private static final long serialVersionUID = 1L;
    }
}
