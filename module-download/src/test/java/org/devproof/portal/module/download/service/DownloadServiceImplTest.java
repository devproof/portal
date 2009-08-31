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
package org.devproof.portal.module.download.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.download.dao.DownloadDao;
import org.devproof.portal.module.download.entity.DownloadEntity;
import org.devproof.portal.module.download.entity.DownloadTagEntity;
import org.easymock.EasyMock;

/**
 * Checks the delegating functionality of the DownloadServiceImpl
 * 
 * @author Carsten Hufe
 */
public class DownloadServiceImplTest extends TestCase {
	private DownloadServiceImpl impl;
	private DownloadDao mock;
	private TagService<DownloadTagEntity> mockTag;

	@Override
	public void setUp() throws Exception {
		this.mock = EasyMock.createStrictMock(DownloadDao.class);
		@SuppressWarnings("unchecked")
		TagService<DownloadTagEntity> tagService = EasyMock.createStrictMock(TagService.class);
		this.mockTag = tagService;
		this.impl = new DownloadServiceImpl();
		this.impl.setDownloadDao(this.mock);
		this.impl.setDownloadTagService(this.mockTag);
	}

	public void testSave() {
		DownloadEntity e = this.impl.newDownloadEntity();
		e.setId(1);
		this.mock.save(e);
		this.mockTag.deleteUnusedTags();
		EasyMock.replay(this.mock);
		EasyMock.replay(this.mockTag);
		this.impl.save(e);
		EasyMock.verify(this.mock);
		EasyMock.verify(this.mockTag);
	}

	public void testDelete() {
		DownloadEntity e = this.impl.newDownloadEntity();
		e.setId(1);
		this.mock.delete(e);
		this.mockTag.deleteUnusedTags();
		EasyMock.replay(this.mock);
		EasyMock.replay(this.mockTag);
		this.impl.delete(e);
		EasyMock.verify(this.mock);
		EasyMock.verify(this.mockTag);
	}

	public void testFindAll() {
		List<DownloadEntity> list = new ArrayList<DownloadEntity>();
		list.add(this.impl.newDownloadEntity());
		list.add(this.impl.newDownloadEntity());
		EasyMock.expect(this.mock.findAll()).andReturn(list);
		EasyMock.replay(this.mock);
		assertEquals(list, this.impl.findAll());
		EasyMock.verify(this.mock);
	}

	public void testFindById() {
		DownloadEntity e = this.impl.newDownloadEntity();
		e.setId(1);
		EasyMock.expect(this.mock.findById(1)).andReturn(e);
		EasyMock.replay(this.mock);
		assertEquals(this.impl.findById(1), e);
		EasyMock.verify(this.mock);
	}

	public void testNewDownloadEntity() {
		assertNotNull(this.impl.newDownloadEntity());
	}

	public void testFindAllDownloadsForRoleOrderedByDateDesc() {
		List<DownloadEntity> list = new ArrayList<DownloadEntity>();
		list.add(this.impl.newDownloadEntity());
		list.add(this.impl.newDownloadEntity());
		RoleEntity role = new RoleEntity();
		role.setId(1);
		EasyMock.expect(this.mock.findAllDownloadsForRoleOrderedByDateDesc(role, 0, 2)).andReturn(list);
		EasyMock.replay(this.mock);
		this.impl.findAllDownloadsForRoleOrderedByDateDesc(role, 0, 2);
		EasyMock.verify(this.mock);
	}

	public void testIncrementHits() {
		DownloadEntity e = this.impl.newDownloadEntity();
		e.setId(1);
		this.mock.incrementHits(e);
		EasyMock.replay(this.mock);
		this.impl.incrementHits(e);
		EasyMock.verify(this.mock);
	}

	public void testMarkBrokenDownload() {
		DownloadEntity e = this.impl.newDownloadEntity();
		e.setId(1);
		this.mock.markBrokenDownload(e);
		EasyMock.replay(this.mock);
		this.impl.markBrokenDownload(e);
		EasyMock.verify(this.mock);
	}

	public void testMarkValidDownload() {
		DownloadEntity e = this.impl.newDownloadEntity();
		e.setId(1);
		this.mock.markValidDownload(e);
		EasyMock.replay(this.mock);
		this.impl.markValidDownload(e);
		EasyMock.verify(this.mock);
	}

	public void testRateDownload() {
		DownloadEntity e = this.impl.newDownloadEntity();
		e.setId(1);
		this.mock.rateDownload(5, e);
		this.mock.refresh(e);
		EasyMock.replay(this.mock);
		this.impl.rateDownload(5, e);
		EasyMock.verify(this.mock);
	}
}
