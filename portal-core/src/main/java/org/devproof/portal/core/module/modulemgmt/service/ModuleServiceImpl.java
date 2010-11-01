/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.modulemgmt.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.devproof.portal.core.config.BoxConfiguration;
import org.devproof.portal.core.config.ModuleConfiguration;
import org.devproof.portal.core.config.PageConfiguration;
import org.devproof.portal.core.module.common.locator.PageLocator;
import org.devproof.portal.core.module.modulemgmt.bean.ModuleBean;
import org.devproof.portal.core.module.modulemgmt.dao.ModuleLinkDao;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLinkEntity;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLinkEntity.LinkType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author Carsten Hufe
 */
@Service("moduleService")
public class ModuleServiceImpl implements ModuleService {
	private ApplicationContext applicationContext;
	private ModuleLinkDao moduleLinkDao;
	private PageLocator pageLocator;

	@Override
	public List<ModuleBean> findModules() {
		List<ModuleBean> coreModules = new ArrayList<ModuleBean>();
		List<ModuleBean> otherModules = new ArrayList<ModuleBean>();
		Map<String, ModuleConfiguration> beans = applicationContext.getBeansOfType(ModuleConfiguration.class);
		for (ModuleConfiguration module : beans.values()) {
			ModuleBean bean = new ModuleBean();
			bean.setConfiguration(module);
			bean.setLocation(getLocations(module));
			if ("core".equals(module.getPortalVersion()) && "core".equals(module.getModuleVersion())) {
				coreModules.add(bean);
			} else {
				otherModules.add(bean);
			}
		}
		List<ModuleBean> back = new ArrayList<ModuleBean>(coreModules.size() + otherModules.size());
		back.addAll(coreModules);
		back.addAll(otherModules);
		return back;

	}

	@Override
	public void moveDown(ModuleLinkEntity link) {
		int maxSort = moduleLinkDao.getMaxSortNum(link.getLinkType());
		if (link.getSort() < maxSort) {
			ModuleLinkEntity moveDown = link;
			ModuleLinkEntity moveUp = moduleLinkDao.findModuleLinkBySort(link.getLinkType(), link.getSort() + 1);
			moveUp.setSort(moveUp.getSort() - 1);
			moveDown.setSort(moveDown.getSort() + 1);
			moduleLinkDao.save(moveUp);
			moduleLinkDao.save(moveDown);
		}
	}

	@Override
	public void moveUp(ModuleLinkEntity link) {
		if (link.getSort() > 1) {
			ModuleLinkEntity moveUp = link;
			ModuleLinkEntity moveDown = moduleLinkDao.findModuleLinkBySort(link.getLinkType(), link.getSort() - 1);
			moveUp.setSort(moveUp.getSort() - 1);
			moveDown.setSort(moveDown.getSort() + 1);
			moduleLinkDao.save(moveUp);
			moduleLinkDao.save(moveDown);
		}
	}

	@Override
	public void save(ModuleLinkEntity link) {
		moduleLinkDao.save(link);
	}

	@Override
	public List<ModuleLinkEntity> findAllVisibleGlobalAdministrationLinks() {
		return moduleLinkDao.findVisibleModuleLinks(LinkType.GLOBAL_ADMINISTRATION);
	}

	@Override
	public List<ModuleLinkEntity> findAllVisibleMainNavigationLinks() {
		return moduleLinkDao.findVisibleModuleLinks(LinkType.TOP_NAVIGATION);
	}

	@Override
	public List<ModuleLinkEntity> findAllVisiblePageAdministrationLinks() {
		return moduleLinkDao.findVisibleModuleLinks(LinkType.PAGE_ADMINISTRATION);
	}

	@PostConstruct
	public void afterPropertiesSet() throws Exception {
		rebuildModuleLinks();
	}

	/*
	 * Rebuilds the module links in the database, protected for unit test
	 */
	protected void rebuildModuleLinks() {
		for (LinkType type : LinkType.values()) {
			ModuleLinkEntity startPage = null;
			List<ModuleLinkEntity> toAddSelected = new ArrayList<ModuleLinkEntity>();
			List<ModuleLinkEntity> toAddNotSelected = new ArrayList<ModuleLinkEntity>();
			Set<ModuleLinkEntity> toRemove = new HashSet<ModuleLinkEntity>();
			List<ModuleLinkEntity> allLinks = moduleLinkDao.findModuleLinks(type);
			toRemove.addAll(allLinks);
			for (PageConfiguration page : pageLocator.getPageConfigurations()) {
				ModuleLinkEntity link = mapTo(page, type);
				// new link
				if (!allLinks.contains(link)) {
					if (page.isDefaultStartPage() && link.getLinkType() == LinkType.TOP_NAVIGATION) {
						startPage = link;
					} else if (link.getVisible()) {
						toAddSelected.add(link);
					} else {
						toAddNotSelected.add(link);
					}
				}
				// link found, remove from list
				toRemove.remove(link);
			}
			// remove links which was not found
			for (ModuleLinkEntity link : toRemove) {
				moduleLinkDao.delete(link);
			}
			Integer maxSort = moduleLinkDao.getMaxSortNum(type);
			if (maxSort == null) {
				maxSort = 1;
			} else {
				maxSort++;
			}
			// save new links, add visible links at first
			List<ModuleLinkEntity> toAdd = new ArrayList<ModuleLinkEntity>();
			if (startPage != null) {
				toAdd.add(startPage);
			}
			toAdd.addAll(toAddSelected);
			toAdd.addAll(toAddNotSelected);

			for (ModuleLinkEntity link : toAdd) {
				link.setSort(maxSort++);
				moduleLinkDao.save(link);
			}
			// make the sort order consistent (remove sort gaps)
			Set<ModuleLinkEntity> sortedLinks = new TreeSet<ModuleLinkEntity>(moduleLinkDao.findModuleLinks(type));
			int i = 1;
			for (ModuleLinkEntity link : sortedLinks) {
				link.setSort(i++);
				moduleLinkDao.save(link);
			}
		}
	}

	private ModuleLinkEntity mapTo(PageConfiguration configuration, LinkType linkType) {
		ModuleLinkEntity link = new ModuleLinkEntity();
		link.setLinkType(linkType);
		// link.setModuleName(configuration.)
		link.setPageName(configuration.getPageClass().getSimpleName());
		link.setModuleName(configuration.getModule().getName());
		if (LinkType.GLOBAL_ADMINISTRATION == linkType) {
			link.setVisible(configuration.isRegisterGlobalAdminLink());
		} else if (LinkType.PAGE_ADMINISTRATION == linkType) {
			link.setVisible(configuration.isRegisterPageAdminLink());
		} else if (LinkType.TOP_NAVIGATION == linkType) {
			link.setVisible(configuration.isRegisterMainNavigationLink());
		} else {
			throw new IllegalArgumentException("LinkType " + linkType + " is not handled!");
		}
		link.setSort(Integer.MAX_VALUE);
		return link;
	}

	private String getLocations(ModuleConfiguration config) {
		Set<String> locations = new HashSet<String>();
		for (BoxConfiguration c : config.getBoxes()) {
			locations.add(getLocation(c.getBoxClass()));
		}
		for (Class<?> c : config.getEntities()) {
			locations.add(getLocation(c));
		}
		for (PageConfiguration c : config.getPages()) {
			locations.add(getLocation(c.getPageClass()));
		}
		if (locations.isEmpty()) {
			locations.add("unknown");
		}
		return StringUtils.replaceChars(locations.toString(), "[]", "");
	}

	private String getLocation(Class<?> clazz) {
		URL url = clazz.getResource("");
		if (!"jar".equals(url.getProtocol())) {
			return "WAR";
		} else {
			String path = url.getPath();
			String strs[] = StringUtils.split(path, '/');
			for (String str : strs) {
				if (str.endsWith("!")) {
					return StringUtils.removeEnd(str, "!");
				}
			}
			return "unknown";
		}
	}

	@Autowired
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Autowired
	public void setModuleLinkDao(ModuleLinkDao moduleLinkDao) {
		this.moduleLinkDao = moduleLinkDao;
	}

	@Autowired
	public void setPageLocator(PageLocator pageLocator) {
		this.pageLocator = pageLocator;
	}
}
