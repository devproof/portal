/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.devproof.portal.core.module.common.panel;

import junit.framework.TestCase;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.TestPanelSource;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.core.module.common.entity.BaseEntity;
import org.devproof.portal.test.PortalTestUtil;

import java.util.Date;

/**
 * @author Carsten Hufe
 */
public class MetaInfoPanelTest extends TestCase {
	private WicketTester tester;

	@Override
	public void setUp() throws Exception {
		tester = PortalTestUtil.createWicketTesterWithSpringAndDatabase();
		PortalTestUtil.loginDefaultAdminUser(tester);
	}

	@Override
	protected void tearDown() throws Exception {
		PortalTestUtil.destroy(tester);
	}

	public void testRenderDefaultPanel() {
		tester.startPanel(createMetaInfoPanel());
		tester.assertComponent("panel", MetaInfoPanel.class);
	}

	private TestPanelSource createMetaInfoPanel() {
		return new TestPanelSource() {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel getTestPanel(String panelId) {
				TestEntity entity = new TestEntity();
				entity.setCreatedAt(new Date());
				entity.setModifiedAt(new Date());
				entity.setCreatedBy("foo");
				entity.setModifiedBy("bar");
				return new MetaInfoPanel<TestEntity>(panelId, Model.of(entity));
			}
		};
	}

	private static class TestEntity extends BaseEntity {
		private static final long serialVersionUID = 1L;
	}
}
