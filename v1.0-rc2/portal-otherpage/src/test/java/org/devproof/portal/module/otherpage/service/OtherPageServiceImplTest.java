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
package org.devproof.portal.module.otherpage.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.devproof.portal.module.otherpage.dao.OtherPageDao;
import org.devproof.portal.module.otherpage.entity.OtherPageEntity;
import org.devproof.portal.module.otherpage.service.OtherPageServiceImpl;
import org.easymock.EasyMock;


/**
 * Checks the delegating functionality of the OtherPageServiceImpl
 * @author Carsten Hufe
 */
public class OtherPageServiceImplTest extends TestCase {
	private OtherPageServiceImpl impl;
	private OtherPageDao mock;
	@Override
	public void setUp() throws Exception {
		this.mock = EasyMock.createStrictMock(OtherPageDao.class);
		this.impl = new OtherPageServiceImpl();
		this.impl.setOtherPageDao(this.mock);
	}

	public void testSave() {
		OtherPageEntity e = this.impl.newOtherPageEntity();
		e.setId(1);
		this.mock.save(e);
		EasyMock.replay(this.mock);
		this.impl.save(e);
		EasyMock.verify(this.mock);
	}

	public void testDelete() {
		OtherPageEntity e = this.impl.newOtherPageEntity();
		e.setId(1);
		this.mock.delete(e);
		EasyMock.replay(this.mock);
		this.impl.delete(e);
		EasyMock.verify(this.mock);
	}
	
	public void testFindAll() {
		List<OtherPageEntity> list = new ArrayList<OtherPageEntity>();
		list.add(this.impl.newOtherPageEntity());
		list.add(this.impl.newOtherPageEntity());
		EasyMock.expect(this.mock.findAll()).andReturn(list);
		EasyMock.replay(this.mock);
		assertEquals(list,this.impl.findAll());
		EasyMock.verify(this.mock);
	}
	
	public void testFindById() {
		OtherPageEntity e = this.impl.newOtherPageEntity();
		e.setId(1);
		EasyMock.expect(this.mock.findById(1)).andReturn(e);
		EasyMock.replay(this.mock);
		assertEquals(this.impl.findById(1), e);
		EasyMock.verify(this.mock);
	}
	
	public void testNewOtherPageEntity() {
		assertNotNull(this.impl.newOtherPageEntity());
	}
	
	public void testExistsContentId() {
		EasyMock.expect(this.mock.existsContentId("contentId")).andReturn(1l);
		EasyMock.replay(this.mock);
		assertTrue(this.impl.existsContentId("contentId"));
		EasyMock.verify(this.mock);
	}
}
