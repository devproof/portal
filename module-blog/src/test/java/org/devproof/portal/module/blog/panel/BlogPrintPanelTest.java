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
package org.devproof.portal.module.blog.panel;

import junit.framework.TestCase;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.TestPanelSource;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.module.blog.entity.BlogEntity;
import org.devproof.portal.test.PortalTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * @author Carsten Hufe
 */
public class BlogPrintPanelTest {
    private WicketTester tester;

    @Before
    public void setUp() throws Exception {
        tester = PortalTestUtil.createWicketTesterWithSpringAndDatabase("create_tables_hsql_blog.sql", "insert_blog.sql");
    }

    @After
    public void tearDown() throws Exception {
        PortalTestUtil.destroy(tester);
    }

    @Test
    public void testRenderDefaultPanel() {
        tester.startPanel(createBlogPrintPanel());
        tester.assertComponent("panel", BlogPrintPanel.class);
    }

    private TestPanelSource createBlogPrintPanel() {
        return new TestPanelSource() {
            private static final long serialVersionUID = 1L;

            @Override
            public Panel getTestPanel(String panelId) {
                BlogEntity blog = new BlogEntity();
                blog.setCreatedAt(new Date());
                blog.setModifiedAt(new Date());
                blog.setCreatedBy("foo");
                blog.setModifiedBy("bar");
                return new BlogPrintPanel(panelId, Model.of(blog));
            }
        };
    }
}
