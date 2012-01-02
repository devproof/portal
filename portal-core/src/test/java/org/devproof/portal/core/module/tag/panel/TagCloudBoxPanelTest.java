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
package org.devproof.portal.core.module.tag.panel;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.core.module.configuration.entity.Configuration;
import org.devproof.portal.core.module.role.entity.Role;
import org.devproof.portal.core.module.tag.entity.AbstractTag;
import org.devproof.portal.core.module.tag.service.TagService;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Carsten Hufe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class,
        locations = {"classpath:/org/devproof/portal/core/test-datasource.xml"})
public class TagCloudBoxPanelTest {
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
        tester.startComponentInPage(new TagCloudBoxPanel<TestTag>("panel", new PageParameters(), new TestTagService(), WebPage.class));
        // tester.assertComponent("panel", TagCloudBoxPanel.class);
    }

    private static class TestTag extends AbstractTag<Configuration> {
        private static final long serialVersionUID = 1L;

        @Override
        public List<Configuration> getReferencedObjects() {
            return null;
        }

        @Override
        public void setReferencedObjects(List<Configuration> refObjs) {
        }
    }

    private static class TestTagService implements TagService<TestTag>, Serializable {
        private static final long serialVersionUID = 1L;

        @Override
        public void deleteUnusedTags() {
        }

        @Override
        public TestTag findByIdAndCreateIfNotExists(String tagName) {
            return null;
        }

        @Override
        public List<TestTag> findMostPopularTags(Integer firstResult, Integer maxResult) {
            return new ArrayList<TestTag>();
        }

        @Override
        public List<TestTag> findMostPopularTags(Role role, Integer firstResult, Integer maxResult) {
            return new ArrayList<TestTag>();
        }

        @Override
        public List<TestTag> findTagsStartingWith(String prefix) {
            return new ArrayList<TestTag>();
        }

        @Override
        public String getRelatedTagRight() {
            return null;
        }

        @Override
        public TestTag newTagEntity(String tag) {
            return null;
        }

        @Override
        public void delete(TestTag entity) {
        }

        @Override
        public TestTag findById(String id) {
            return null;
        }

        @Override
        public void save(TestTag entity) {
        }

        @Override
        public List<TestTag> findWhitespaceSeparatedTagsAndCreateIfNotExists(String tags) {
            return new ArrayList<TestTag>();
        }

        @Override
        public String convertTagsToWhitespaceSeparated(List<TestTag> tags) {
            return null;
        }
    }
}
