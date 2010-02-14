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
package org.devproof.portal.module.deadlinkcheck.panel;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.module.deadlinkcheck.entity.BaseLinkEntity;
import org.devproof.portal.test.PortalTestUtil;

/**
 * @author Carsten Hufe
 */
public class DeadLinkCheckPanelTest extends TestCase {
	private WicketTester tester;

	@Override
	public void setUp() throws Exception {
		tester = PortalTestUtil.createWicketTesterWithSpringAndDatabase();
	}

	@Override
	protected void tearDown() throws Exception {
		PortalTestUtil.destroy(tester);
	}

	public void testRenderDefaultPage() {
		tester.startPanel(TestDeadLinkCheckPanel.class);
		tester.assertComponent("panel", TestDeadLinkCheckPanel.class);
	}

	public static class TestDeadLinkCheckPanel extends DeadlinkCheckPanel<TestLinkEntity> {
		public TestDeadLinkCheckPanel(String id) {
			super(id, "download", new ArrayList<TestLinkEntity>());
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

	private static class TestLinkEntity extends BaseLinkEntity {
		private static final long serialVersionUID = 1L;
	}
}
