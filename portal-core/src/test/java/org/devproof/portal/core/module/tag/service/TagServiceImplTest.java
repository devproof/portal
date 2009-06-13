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
		this.mock = EasyMock.createStrictMock(TagDao.class);
		this.impl = new TagServiceImpl<DummyTagEntity>();
		this.impl.setTagDao(this.mock);
		this.impl.setRelatedTagRight("testright");
	}

	public void testSave() {
		final DummyTagEntity e = new DummyTagEntity();
		e.setTagname("tag");
		this.mock.save(e);
		EasyMock.replay(this.mock);
		this.impl.save(e);
		EasyMock.verify(this.mock);
	}

	public void testDelete() {
		final DummyTagEntity e = new DummyTagEntity();
		e.setTagname("tag");
		this.mock.delete(e);
		EasyMock.replay(this.mock);
		this.impl.delete(e);
		EasyMock.verify(this.mock);
	}

	public void testFindAll() {
		final List<DummyTagEntity> list = new ArrayList<DummyTagEntity>();
		list.add(new DummyTagEntity());
		list.add(new DummyTagEntity());
		EasyMock.expect(this.mock.findAll()).andReturn(list);
		EasyMock.replay(this.mock);
		assertEquals(list, this.impl.findAll());
		EasyMock.verify(this.mock);
	}

	public void testFindById() {
		final DummyTagEntity e = new DummyTagEntity();
		e.setTagname("tag");
		EasyMock.expect(this.mock.findById("tag")).andReturn(e);
		EasyMock.replay(this.mock);
		assertEquals(this.impl.findById("tag"), e);
		EasyMock.verify(this.mock);
	}

	public void testNewTagEntity() {
		EasyMock.expect(this.mock.getType()).andReturn(DummyTagEntity.class);
		EasyMock.replay(this.mock);
		assertNotNull(this.impl.newTagEntity("tag"));
	}

	public void testFindMostPopularTags1() {
		final List<DummyTagEntity> list = new ArrayList<DummyTagEntity>();
		list.add(new DummyTagEntity());
		list.add(new DummyTagEntity());
		EasyMock.expect(this.mock.findMostPopularTags(0, 2)).andReturn(list);
		EasyMock.replay(this.mock);
		assertEquals(list, this.impl.findMostPopularTags(0, 2));
		EasyMock.verify(this.mock);
	}

	public void testFindMostPopularTags2() {
		final List<DummyTagEntity> list = new ArrayList<DummyTagEntity>();
		list.add(new DummyTagEntity());
		list.add(new DummyTagEntity());
		final RoleEntity role = new RoleEntity();
		EasyMock.expect(this.mock.findMostPopularTags(role, "testright", 0, 2)).andReturn(list);
		EasyMock.replay(this.mock);
		assertEquals(list, this.impl.findMostPopularTags(role, 0, 2));
		EasyMock.verify(this.mock);
	}

	public void testFindTagsStartingWith() {
		final List<DummyTagEntity> list = new ArrayList<DummyTagEntity>();
		list.add(new DummyTagEntity());
		list.add(new DummyTagEntity());
		EasyMock.expect(this.mock.findTagsStartingWith("prefix")).andReturn(list);
		EasyMock.replay(this.mock);
		assertEquals(list, this.impl.findTagsStartingWith("prefix"));
		EasyMock.verify(this.mock);
	}

	public void testDeleteUnusedTags() {
		this.mock.deleteUnusedTags();
		EasyMock.replay(this.mock);
		this.impl.deleteUnusedTags();
		EasyMock.verify(this.mock);
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
