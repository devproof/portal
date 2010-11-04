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
package org.devproof.portal.core.module.tag.panel;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.tester.TestPanelSource;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.core.module.configuration.entity.Configuration;
import org.devproof.portal.core.module.tag.entity.BaseTagEntity;
import org.devproof.portal.test.PortalTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Carsten Hufe
 */
public class TagContentPanelTest {
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
        tester.startPanel(createContentTagPanel());
        tester.assertComponent("panel", TagContentPanel.class);
    }

    private TestPanelSource createContentTagPanel() {
        return new TestPanelSource() {
            private static final long serialVersionUID = 1L;

            @Override
            public Panel getTestPanel(String panelId) {
                List<TestTagEntity> tags = new ArrayList<TestTagEntity>();
                TestTagEntity tag = new TestTagEntity();
                tag.setTagname("foobar");
                tags.add(tag);
                return new TagContentPanel<TestTagEntity>(panelId, new ListModel<TestTagEntity>(tags), WebPage.class);
            }
        };
    }

    private static class TestTagEntity extends BaseTagEntity<Configuration> {
        private static final long serialVersionUID = 1L;

        @Override
        public List<Configuration> getReferencedObjects() {
            return null;
        }

        @Override
        public void setReferencedObjects(List<Configuration> refObjs) {
        }
    }
}
