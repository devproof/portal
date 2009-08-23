/*
 * Copyright 2009 Carsten Hufe devproof.org
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
package org.devproof.portal.core.module.tag.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.tag.dao.TagDao;
import org.devproof.portal.core.module.tag.entity.BaseTagEntity;
import org.easymock.EasyMock;

/**
 * @author Carsten Hufe
 */
public class TagServiceImplTest extends TestCase {
	private TagServiceImpl<DummyTagEntity> impl;
	private TagDao<DummyTagEntity> mock;

	@Override
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		mock = EasyMock.createStrictMock(TagDao.class);
		impl = new TagServiceImpl<DummyTagEntity>();
		impl.setTagDao(mock);
		impl.setRelatedTagRight("testright");
	}

	public void testSave() {
		final DummyTagEntity e = new DummyTagEntity();
		e.setTagname("tag");
		mock.save(e);
		EasyMock.replay(mock);
		impl.save(e);
		EasyMock.verify(mock);
	}

	public void testDelete() {
		final DummyTagEntity e = new DummyTagEntity();
		e.setTagname("tag");
		mock.delete(e);
		EasyMock.replay(mock);
		impl.delete(e);
		EasyMock.verify(mock);
	}

	public void testFindAll() {
		final List<DummyTagEntity> list = new ArrayList<DummyTagEntity>();
		list.add(new DummyTagEntity());
		list.add(new DummyTagEntity());
		EasyMock.expect(mock.findAll()).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(list, impl.findAll());
		EasyMock.verify(mock);
	}

	public void testFindById() {
		final DummyTagEntity e = new DummyTagEntity();
		e.setTagname("tag");
		EasyMock.expect(mock.findById("tag")).andReturn(e);
		EasyMock.replay(mock);
		assertEquals(impl.findById("tag"), e);
		EasyMock.verify(mock);
	}

	public void testNewTagEntity() {
		EasyMock.expect(mock.getType()).andReturn(DummyTagEntity.class);
		EasyMock.replay(mock);
		assertNotNull(impl.newTagEntity("tag"));
	}

	public void testFindMostPopularTags1() {
		final List<DummyTagEntity> list = new ArrayList<DummyTagEntity>();
		list.add(new DummyTagEntity());
		list.add(new DummyTagEntity());
		EasyMock.expect(mock.findMostPopularTags(0, 2)).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(list, impl.findMostPopularTags(0, 2));
		EasyMock.verify(mock);
	}

	public void testFindMostPopularTags2() {
		final List<DummyTagEntity> list = new ArrayList<DummyTagEntity>();
		list.add(new DummyTagEntity());
		list.add(new DummyTagEntity());
		final RoleEntity role = new RoleEntity();
		EasyMock.expect(mock.findMostPopularTags(role, "testright", 0, 2)).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(list, impl.findMostPopularTags(role, 0, 2));
		EasyMock.verify(mock);
	}

	public void testFindTagsStartingWith() {
		final List<DummyTagEntity> list = new ArrayList<DummyTagEntity>();
		list.add(new DummyTagEntity());
		list.add(new DummyTagEntity());
		EasyMock.expect(mock.findTagsStartingWith("prefix")).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(list, impl.findTagsStartingWith("prefix"));
		EasyMock.verify(mock);
	}

	public void testDeleteUnusedTags() {
		mock.deleteUnusedTags();
		EasyMock.replay(mock);
		impl.deleteUnusedTags();
		EasyMock.verify(mock);
	}

	private static class DummyTagEntity extends BaseTagEntity<Object> {

		private static final long serialVersionUID = 1L;

		public DummyTagEntity() {
			super();
			setTagname(String.valueOf(Math.random()));
		}

		@Override
		public List<?> getReferencedObjects() {
			return null;
		}

		@Override
		public void setReferencedObjects(final List<Object> refObjs) {

		}

	}
}
