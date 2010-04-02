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
package org.devproof.portal.core.module.tag.panel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.tester.TestPanelSource;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.core.module.configuration.entity.ConfigurationEntity;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.tag.entity.BaseTagEntity;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.test.PortalTestUtil;

/**
 * @author Carsten Hufe
 */
public class TagCloudBoxPanelTest extends TestCase {
	private WicketTester tester;

	@Override
	public void setUp() throws Exception {
		tester = PortalTestUtil.createWicketTesterWithSpringAndDatabase();
	}

	@Override
	protected void tearDown() throws Exception {
		PortalTestUtil.destroy(tester);
	}

	public void testRenderDefaultPanel() {
		tester.startPanel(createTagCloudBoxPanel());
		// tester.assertComponent("panel", TagCloudBoxPanel.class);
	}

	private TestPanelSource createTagCloudBoxPanel() {
		return new TestPanelSource() {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel getTestPanel(String panelId) {
				return new TagCloudBoxPanel<TestTagEntity>(panelId, new TestTagService(), WebPage.class);
			}
		};
	}

	private static class TestTagEntity extends BaseTagEntity<ConfigurationEntity> {
		private static final long serialVersionUID = 1L;

		@Override
		public List<ConfigurationEntity> getReferencedObjects() {
			return null;
		}

		@Override
		public void setReferencedObjects(List<ConfigurationEntity> refObjs) {
		}
	}

	private static class TestTagService implements TagService<TestTagEntity>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void deleteUnusedTags() {
		}

		@Override
		public TestTagEntity findByIdAndCreateIfNotExists(String tagName) {
			return null;
		}

		@Override
		public List<TestTagEntity> findMostPopularTags(Integer firstResult, Integer maxResult) {
			return new ArrayList<TestTagEntity>();
		}

		@Override
		public List<TestTagEntity> findMostPopularTags(RoleEntity role, Integer firstResult, Integer maxResult) {
			return new ArrayList<TestTagEntity>();
		}

		@Override
		public List<TestTagEntity> findTagsStartingWith(String prefix) {
			return new ArrayList<TestTagEntity>();
		}

		@Override
		public String getRelatedTagRight() {
			return null;
		}

		@Override
		public TestTagEntity newTagEntity(String tag) {
			return null;
		}

		@Override
		public void delete(TestTagEntity entity) {
		}

		@Override
		public TestTagEntity findById(String id) {
			return null;
		}

		@Override
		public void save(TestTagEntity entity) {
		}
	}
}
