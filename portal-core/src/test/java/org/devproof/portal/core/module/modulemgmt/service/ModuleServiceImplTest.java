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
package org.devproof.portal.core.module.modulemgmt.service;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

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
		moduleLinkDaoMock = createStrictMock(ModuleLinkDao.class);
		pageLocatorMock = createStrictMock(PageLocator.class);
		applicationContextMock = createStrictMock(ApplicationContext.class);
		impl = new ModuleServiceImpl();
		impl.setModuleLinkDao(moduleLinkDaoMock);
		impl.setPageLocator(pageLocatorMock);
		impl.setApplicationContext(applicationContextMock);
	}

	public void testSave() {
		ModuleLinkEntity e = new ModuleLinkEntity();
		e.setPageName("hello");
		e.setLinkType(LinkType.GLOBAL_ADMINISTRATION);
		expect(moduleLinkDaoMock.save(e)).andReturn(e);
		replay(moduleLinkDaoMock);
		impl.save(e);
		verify(moduleLinkDaoMock);
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
		expect(moduleLinkDaoMock.getMaxSortNum(LinkType.GLOBAL_ADMINISTRATION)).andReturn(2);
		expect(moduleLinkDaoMock.findModuleLinkBySort(LinkType.GLOBAL_ADMINISTRATION, 2)).andReturn(e2);
		expect(moduleLinkDaoMock.save(e2)).andReturn(e2);
		expect(moduleLinkDaoMock.save(e1)).andReturn(e1);
		replay(moduleLinkDaoMock);
		impl.moveDown(e1);
		assertEquals(Integer.valueOf(2), e1.getSort());
		assertEquals(Integer.valueOf(1), e2.getSort());
		verify(moduleLinkDaoMock);
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
		expect(moduleLinkDaoMock.findModuleLinkBySort(LinkType.GLOBAL_ADMINISTRATION, 1)).andReturn(e1);
		expect(moduleLinkDaoMock.save(e2)).andReturn(e2);
		expect(moduleLinkDaoMock.save(e1)).andReturn(e1);
		replay(moduleLinkDaoMock);
		impl.moveUp(e2);
		assertEquals(Integer.valueOf(2), e1.getSort());
		assertEquals(Integer.valueOf(1), e2.getSort());
		verify(moduleLinkDaoMock);
	}

	public void testRebuildModuleLinks() {
		// TODO impl.rebuildModuleLinks(); who wanna write it?
	}

	public void testFindAllVisibleGlobalAdministrationLinks() {
		List<ModuleLinkEntity> links = new ArrayList<ModuleLinkEntity>();
		links.add(new ModuleLinkEntity());
		expect(moduleLinkDaoMock.findVisibleModuleLinks(LinkType.GLOBAL_ADMINISTRATION)).andReturn(links);
		replay(moduleLinkDaoMock);
		impl.findAllVisibleGlobalAdministrationLinks();
		verify(moduleLinkDaoMock);
	}

	public void testFindAllVisibleMainNavigationLinks() {
		List<ModuleLinkEntity> links = new ArrayList<ModuleLinkEntity>();
		links.add(new ModuleLinkEntity());
		expect(moduleLinkDaoMock.findVisibleModuleLinks(LinkType.TOP_NAVIGATION)).andReturn(links);
		replay(moduleLinkDaoMock);
		impl.findAllVisibleMainNavigationLinks();
		verify(moduleLinkDaoMock);
	}

	public void testFindAllVisiblePageAdministrationLinks() {
		List<ModuleLinkEntity> links = new ArrayList<ModuleLinkEntity>();
		links.add(new ModuleLinkEntity());
		expect(moduleLinkDaoMock.findVisibleModuleLinks(LinkType.PAGE_ADMINISTRATION)).andReturn(links);
		replay(moduleLinkDaoMock);
		impl.findAllVisiblePageAdministrationLinks();
		verify(moduleLinkDaoMock);
	}

	public void testFindModules() {
		Map<String, ModuleConfiguration> beans = new HashMap<String, ModuleConfiguration>();
		beans.put("bean", new ModuleConfiguration());
		expect(applicationContextMock.getBeansOfType(ModuleConfiguration.class)).andReturn(beans);
		replay(applicationContextMock);
		assertEquals(impl.findModules().size(), beans.size());
		verify(applicationContextMock);
	}
}
