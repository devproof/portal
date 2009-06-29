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
package org.devproof.portal.core.module.modulemgmt.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.devproof.portal.core.config.ModuleConfiguration;
import org.devproof.portal.core.module.common.locator.PageLocator;
import org.devproof.portal.core.module.modulemgmt.dao.ModuleLinkDao;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLinkEntity;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLinkEntity.LinkType;
import org.easymock.EasyMock;
import org.springframework.context.ApplicationContext;

/**
 * @author Carsten Hufe
 */
public class ModuleServiceImplTest extends TestCase {
	private ModuleServiceImpl impl;
	private ModuleLinkDao moduleLinkDaoMock;
	private PageLocator pageLocatorMock;
	private ApplicationContext applicationContextMock;

	@Override
	public void setUp() throws Exception {
		this.moduleLinkDaoMock = EasyMock.createStrictMock(ModuleLinkDao.class);
		this.pageLocatorMock = EasyMock.createStrictMock(PageLocator.class);
		this.applicationContextMock = EasyMock.createStrictMock(ApplicationContext.class);
		this.impl = new ModuleServiceImpl();
		this.impl.setModuleLinkDao(this.moduleLinkDaoMock);
		this.impl.setPageLocator(this.pageLocatorMock);
		this.impl.setApplicationContext(this.applicationContextMock);
	}

	public void testSave() {
		ModuleLinkEntity e = new ModuleLinkEntity();
		e.setPageName("hello");
		e.setLinkType(LinkType.GLOBAL_ADMINISTRATION);
		this.moduleLinkDaoMock.save(e);
		EasyMock.replay(this.moduleLinkDaoMock);
		this.impl.save(e);
		EasyMock.verify(this.moduleLinkDaoMock);
	}

	public void testMoveDown() {
		ModuleLinkEntity e1 = new ModuleLinkEntity();
		e1.setPageName("hello1");
		e1.setLinkType(LinkType.GLOBAL_ADMINISTRATION);
		e1.setSort(1);
		ModuleLinkEntity e2 = new ModuleLinkEntity();
		e2.setPageName("hello2");
		e2.setLinkType(LinkType.GLOBAL_ADMINISTRATION);
		e2.setSort(2);
		EasyMock.expect(this.moduleLinkDaoMock.getMaxSortNum(LinkType.GLOBAL_ADMINISTRATION)).andReturn(2);
		EasyMock.expect(this.moduleLinkDaoMock.findModuleLinkBySort(LinkType.GLOBAL_ADMINISTRATION, 2)).andReturn(e2);
		this.moduleLinkDaoMock.save(e2);
		this.moduleLinkDaoMock.save(e1);
		EasyMock.replay(this.moduleLinkDaoMock);
		this.impl.moveDown(e1);
		assertEquals(Integer.valueOf(2), e1.getSort());
		assertEquals(Integer.valueOf(1), e2.getSort());
		EasyMock.verify(this.moduleLinkDaoMock);
	}

	public void testMoveUp() {
		ModuleLinkEntity e1 = new ModuleLinkEntity();
		e1.setPageName("hello1");
		e1.setLinkType(LinkType.GLOBAL_ADMINISTRATION);
		e1.setSort(1);
		ModuleLinkEntity e2 = new ModuleLinkEntity();
		e2.setPageName("hello2");
		e2.setLinkType(LinkType.GLOBAL_ADMINISTRATION);
		e2.setSort(2);
		EasyMock.expect(this.moduleLinkDaoMock.findModuleLinkBySort(LinkType.GLOBAL_ADMINISTRATION, 1)).andReturn(e1);
		this.moduleLinkDaoMock.save(e2);
		this.moduleLinkDaoMock.save(e1);
		EasyMock.replay(this.moduleLinkDaoMock);
		this.impl.moveUp(e2);
		assertEquals(Integer.valueOf(2), e1.getSort());
		assertEquals(Integer.valueOf(1), e2.getSort());
		EasyMock.verify(this.moduleLinkDaoMock);
	}

	public void testRebuildModuleLinks() {
		// TODO impl.rebuildModuleLinks(); who wanna write it?
	}

	public void testFindAllVisibleGlobalAdministrationLinks() {
		List<ModuleLinkEntity> links = new ArrayList<ModuleLinkEntity>();
		links.add(new ModuleLinkEntity());
		EasyMock.expect(this.moduleLinkDaoMock.findVisibleModuleLinks(LinkType.GLOBAL_ADMINISTRATION)).andReturn(links);
		EasyMock.replay(this.moduleLinkDaoMock);
		this.impl.findAllVisibleGlobalAdministrationLinks();
		EasyMock.verify(this.moduleLinkDaoMock);
	}

	public void testFindAllVisibleMainNavigationLinks() {
		List<ModuleLinkEntity> links = new ArrayList<ModuleLinkEntity>();
		links.add(new ModuleLinkEntity());
		EasyMock.expect(this.moduleLinkDaoMock.findVisibleModuleLinks(LinkType.TOP_NAVIGATION)).andReturn(links);
		EasyMock.replay(this.moduleLinkDaoMock);
		this.impl.findAllVisibleMainNavigationLinks();
		EasyMock.verify(this.moduleLinkDaoMock);
	}

	public void testFindAllVisiblePageAdministrationLinks() {
		List<ModuleLinkEntity> links = new ArrayList<ModuleLinkEntity>();
		links.add(new ModuleLinkEntity());
		EasyMock.expect(this.moduleLinkDaoMock.findVisibleModuleLinks(LinkType.PAGE_ADMINISTRATION)).andReturn(links);
		EasyMock.replay(this.moduleLinkDaoMock);
		this.impl.findAllVisiblePageAdministrationLinks();
		EasyMock.verify(this.moduleLinkDaoMock);
	}

	public void testFindModules() {
		Map<String, ModuleConfiguration> beans = new HashMap<String, ModuleConfiguration>();
		beans.put("bean", new ModuleConfiguration());
		EasyMock.expect(this.applicationContextMock.getBeansOfType(ModuleConfiguration.class)).andReturn(beans);
		EasyMock.replay(this.applicationContextMock);
		assertEquals(this.impl.findModules().size(), beans.size());
		EasyMock.verify(this.applicationContextMock);
	}
}
