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
package org.devproof.portal.core.module.feed.page;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.core.module.feed.DummyFeedProviderImpl;
import org.devproof.portal.core.module.feed.registry.FeedProviderRegistry;
import org.devproof.portal.test.MockContextLoader;
import org.devproof.portal.test.PortalTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletContext;

/**
 * @author Carsten Hufe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class,
        locations = {"classpath:/org/devproof/portal/core/test-datasource.xml"})
public class Atom1FeedPageTest {
    @Autowired
    private ServletContext servletContext;
    @Autowired
    private ApplicationContext applicationContext;
    private WicketTester tester;

    @Before
    public void setUp() throws Exception {
        tester = PortalTestUtil.createWicketTester(servletContext);
        FeedProviderRegistry registry = (FeedProviderRegistry) applicationContext.getBean("feedProviderRegistry");
        registry.removeFeedProvider("dummy");
        registry.registerFeedProvider("dummy", new DummyFeedProviderImpl());
    }

    @After
    public void tearDown() throws Exception {
        PortalTestUtil.destroy(tester);
    }

    @Test
    public void testRenderDefaultPage() {
        // TODO use named parameters
        tester.startPage(Atom1FeedPage.class, new PageParameters("0=dummy"));
        tester.assertRenderedPage(Atom1FeedPage.class);
        tester.assertContains("dummy feed description");
        tester.assertContains("http://dummy.url");
        tester.assertContains("dummy title");
        tester.assertContains("dummy value");
    }
}
