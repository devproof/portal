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
		mock = EasyMock.createStrictMock(DownloadDao.class);
		@SuppressWarnings("unchecked")
		TagService<DownloadTagEntity> tagService = EasyMock.createStrictMock(TagService.class);
		mockTag = tagService;
		impl = new DownloadServiceImpl();
		impl.setDownloadDao(mock);
		impl.setDownloadTagService(mockTag);
	}

	public void testSave() {
		DownloadEntity e = createDownloadEntity();
		mock.save(e);
		mockTag.deleteUnusedTags();
		EasyMock.replay(mock);
		EasyMock.replay(mockTag);
		impl.save(e);
		EasyMock.verify(mock);
		EasyMock.verify(mockTag);
	}

	public void testDelete() {
		DownloadEntity e = createDownloadEntity();
		mock.delete(e);
		mockTag.deleteUnusedTags();
		EasyMock.replay(mock);
		EasyMock.replay(mockTag);
		impl.delete(e);
		EasyMock.verify(mock);
		EasyMock.verify(mockTag);
	}

	public void testFindAll() {
		List<DownloadEntity> list = new ArrayList<DownloadEntity>();
		list.add(createDownloadEntity());
		list.add(createDownloadEntity());
		EasyMock.expect(mock.findAll()).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(list, impl.findAll());
		EasyMock.verify(mock);
	}

	public void testFindById() {
		DownloadEntity e = createDownloadEntity();
		EasyMock.expect(mock.findById(1)).andReturn(e);
		EasyMock.replay(mock);
		assertEquals(impl.findById(1), e);
		EasyMock.verify(mock);
	}

	public void testNewDownloadEntity() {
		assertNotNull(impl.newDownloadEntity());
	}

	public void testFindAllDownloadsForRoleOrderedByDateDesc() {
		List<DownloadEntity> list = new ArrayList<DownloadEntity>();
		list.add(createDownloadEntity());
		list.add(createDownloadEntity());
		RoleEntity role = new RoleEntity();
		role.setId(1);
		EasyMock.expect(mock.findAllDownloadsForRoleOrderedByDateDesc(role, 0, 2)).andReturn(list);
		EasyMock.replay(mock);
		impl.findAllDownloadsForRoleOrderedByDateDesc(role, 0, 2);
		EasyMock.verify(mock);
	}

	public void testIncrementHits() {
		DownloadEntity e = createDownloadEntity();
		mock.incrementHits(e);
		EasyMock.replay(mock);
		impl.incrementHits(e);
		EasyMock.verify(mock);
	}

	public void testMarkBrokenDownload() {
		DownloadEntity e = createDownloadEntity();
		mock.markBrokenDownload(e);
		EasyMock.replay(mock);
		impl.markBrokenDownload(e);
		EasyMock.verify(mock);
	}

	public void testMarkValidDownload() {
		DownloadEntity e = createDownloadEntity();
		mock.markValidDownload(e);
		EasyMock.replay(mock);
		impl.markValidDownload(e);
		EasyMock.verify(mock);
	}

	public void testRateDownload() {
		DownloadEntity e = createDownloadEntity();
		mock.rateDownload(5, e);
		mock.refresh(e);
		EasyMock.replay(mock);
		impl.rateDownload(5, e);
		EasyMock.verify(mock);
	}
	

	private DownloadEntity createDownloadEntity() {
		DownloadEntity download = new DownloadEntity();
		download.setId(1);
		return download;
	}
}
