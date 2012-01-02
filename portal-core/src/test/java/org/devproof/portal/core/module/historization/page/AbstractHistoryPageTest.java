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
package org.devproof.portal.core.module.historization.page;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.historization.service.Historized;
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

import static org.easymock.EasyMock.*;

/**
 * @author Carsten Hufe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class,
        locations = {"classpath:/org/devproof/portal/core/test-datasource.xml"})
public class AbstractHistoryPageTest {
    @SuppressWarnings({"SpringJavaAutowiringInspection"})
    @Autowired
    private ServletContext servletContext;
    private WicketTester tester;

    @Before
    public void setUp() throws Exception {
        tester = PortalTestUtil.createWicketTester(servletContext);
        PortalTestUtil.loginDefaultAdminUser(tester);
    }

    @After
    public void tearDown() throws Exception {
        PortalTestUtil.destroy(tester);
    }

    @Test
    public void testRenderDefaultPage() {
        tester.startPage(DummyHistoryPage.class);
        tester.assertRenderedPage(DummyHistoryPage.class);
    }

    public static class DummyHistoryPage extends AbstractHistoryPage<Historized> {
        private static final long serialVersionUID = 761684962832228840L;

        public DummyHistoryPage(PageParameters params) {
            super(params);
        }

        @Override
        protected QueryDataProvider<Historized, Serializable> getQueryDataProvider() {
            @SuppressWarnings("unchecked") QueryDataProvider<Historized, Serializable> mock = createNiceMock(QueryDataProvider.class);
            expect(mock.getSortState()).andReturn(new SingleSortState()).anyTimes();
            replay(mock);
            return mock;
        }

        @Override
        protected Component newHistorizedView(String markupId, IModel<Historized> historizedModel) {
            return new EmptyPanel(markupId);
        }

        @Override
        protected void onRestore(IModel<Historized> historizedModel) {
        }
    }
}
