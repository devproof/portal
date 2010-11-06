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

import org.apache.commons.lang.StringUtils;
import org.devproof.portal.core.config.BoxConfiguration;
import org.devproof.portal.core.config.ModuleConfiguration;
import org.devproof.portal.core.config.PageConfiguration;
import org.devproof.portal.core.module.common.locator.PageLocator;
import org.devproof.portal.core.module.modulemgmt.bean.ModuleBean;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLink;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLink.LinkType;
import org.devproof.portal.core.module.modulemgmt.repository.ModuleLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.*;

/**
 * @author Carsten Hufe
 */
@Service("moduleService")
public class ModuleServiceImpl implements ModuleService {
	private ApplicationContext applicationContext;
	private ModuleLinkRepository moduleLinkRepository;
	private PageLocator pageLocator;

	@Override
    @Transactional(readOnly = true)
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
    @Transactional
	public void moveDown(ModuleLink link) {
		int maxSort = moduleLinkRepository.getMaxSortNum(link.getLinkType());
		if (link.getSort() < maxSort) {
			ModuleLink moveDown = link;
			ModuleLink moveUp = moduleLinkRepository.findModuleLinkBySort(link.getLinkType(), link.getSort() + 1);
			moveUp.setSort(moveUp.getSort() - 1);
			moveDown.setSort(moveDown.getSort() + 1);
			moduleLinkRepository.save(moveUp);
			moduleLinkRepository.save(moveDown);
		}
	}

	@Override
    @Transactional
	public void moveUp(ModuleLink link) {
		if (link.getSort() > 1) {
			ModuleLink moveUp = link;
			ModuleLink moveDown = moduleLinkRepository.findModuleLinkBySort(link.getLinkType(), link.getSort() - 1);
			moveUp.setSort(moveUp.getSort() - 1);
			moveDown.setSort(moveDown.getSort() + 1);
			moduleLinkRepository.save(moveUp);
			moduleLinkRepository.save(moveDown);
		}
	}

	@Override
    @Transactional
	public void save(ModuleLink link) {
		moduleLinkRepository.save(link);
	}

	@Override
    @Transactional(readOnly = true)
	public List<ModuleLink> findAllVisibleGlobalAdministrationLinks() {
		return moduleLinkRepository.findVisibleModuleLinks(LinkType.GLOBAL_ADMINISTRATION);
	}

	@Override
    @Transactional(readOnly = true)
	public List<ModuleLink> findAllVisibleMainNavigationLinks() {
		return moduleLinkRepository.findVisibleModuleLinks(LinkType.TOP_NAVIGATION);
	}

	@Override
    @Transactional(readOnly = true)
	public List<ModuleLink> findAllVisiblePageAdministrationLinks() {
		return moduleLinkRepository.findVisibleModuleLinks(LinkType.PAGE_ADMINISTRATION);
	}

    @Override
    @Transactional
	public void rebuildModuleLinks() {
		for (LinkType type : LinkType.values()) {
			ModuleLink startPage = null;
			List<ModuleLink> toAddSelected = new ArrayList<ModuleLink>();
			List<ModuleLink> toAddNotSelected = new ArrayList<ModuleLink>();
			Set<ModuleLink> toRemove = new HashSet<ModuleLink>();
			List<ModuleLink> allLinks = moduleLinkRepository.findModuleLinks(type);
			toRemove.addAll(allLinks);
			for (PageConfiguration page : pageLocator.getPageConfigurations()) {
				ModuleLink link = mapTo(page, type);
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
			for (ModuleLink link : toRemove) {
				moduleLinkRepository.delete(link);
			}
			Integer maxSort = moduleLinkRepository.getMaxSortNum(type);
			if (maxSort == null) {
				maxSort = 1;
			} else {
				maxSort++;
			}
			// save new links, add visible links at first
			List<ModuleLink> toAdd = new ArrayList<ModuleLink>();
			if (startPage != null) {
				toAdd.add(startPage);
			}
			toAdd.addAll(toAddSelected);
			toAdd.addAll(toAddNotSelected);

			for (ModuleLink link : toAdd) {
				link.setSort(maxSort++);
				moduleLinkRepository.save(link);
			}
			// make the sort order consistent (remove sort gaps)
			Set<ModuleLink> sortedLinks = new TreeSet<ModuleLink>(moduleLinkRepository.findModuleLinks(type));
			int i = 1;
			for (ModuleLink link : sortedLinks) {
				link.setSort(i++);
				moduleLinkRepository.save(link);
			}
		}
	}

	private ModuleLink mapTo(PageConfiguration configuration, LinkType linkType) {
		ModuleLink link = new ModuleLink();
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
	public void setModuleLinkRepository(ModuleLinkRepository moduleLinkRepository) {
		this.moduleLinkRepository = moduleLinkRepository;
	}

	@Autowired
	public void setPageLocator(PageLocator pageLocator) {
		this.pageLocator = pageLocator;
	}
}
