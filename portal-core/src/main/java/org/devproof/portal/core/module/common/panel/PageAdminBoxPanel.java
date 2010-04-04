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
package org.devproof.portal.core.module.common.panel;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.resource.loader.ClassStringResourceLoader;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.box.panel.BoxTitleVisibility;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.registry.PageAdminPageRegistry;

import java.util.List;

/**
 * Contains link for upload center and the "add" links
 * 
 * @author Carsten Hufe
 */
public class PageAdminBoxPanel extends Panel implements BoxTitleVisibility {

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "pageAdminPageRegistry")
	private PageAdminPageRegistry adminPageRegistry;
	private RepeatingView extendableRepeating;
	private WebMarkupContainer titleContainer;

	public PageAdminBoxPanel(String id) {
		super(id);
		add(createTitleContainer());
		add(createExtendableView());
		add(createFixedView());
	}

	private WebMarkupContainer createTitleContainer() {
		titleContainer = new WebMarkupContainer("title");
		return titleContainer;
	}

    private ListView createFixedView() {
        IModel<List<Class<? extends Page>>> registeredPageAdminPagesModel = createRegisteredPageAdminPagesModel();
        return new ListView<Class<? extends Page>>("repeatingNavFixed", registeredPageAdminPagesModel) {
            private static final long serialVersionUID = -277523349047078562L;
            @Override
            protected void populateItem(ListItem<Class<? extends Page>> item) {
                Class<? extends Page> pageClass = item.getModelObject();
                item.add(createAdminLink(pageClass));
            }


            private BookmarkablePageLink<Void> createAdminLink(Class<? extends Page> pageClass) {
                String label = getLinkLabelName(pageClass);
                BookmarkablePageLink<Void> link = new BookmarkablePageLink<Void>("adminLink", pageClass);
                link.add(new Label("adminLinkLabel", label));
                return link;
            }
//
//            private BookmarkablePageLink<Void> createAdminLink(Class<? extends Page> pageClass) {
//                BookmarkablePageLink<Void> link = new BookmarkablePageLink<Void>("adminLink", pageClass);
//                link.add(createAdminItemLink(pageClass));
//                return link;
//            }
        };
	}

    private IModel<List<Class<? extends Page>>> createRegisteredPageAdminPagesModel() {
        return new LoadableDetachableModel<List<Class<? extends Page>>>() {
            private static final long serialVersionUID = 3289204569577932297L;
            @Override
            protected List<Class<? extends Page>> load() {
                return adminPageRegistry.getRegisteredPageAdminPages();
            }
        };
    }

	private RepeatingView createExtendableView() {
		extendableRepeating = new RepeatingView("repeatingNav");
		return extendableRepeating;
	}

	private String getLinkLabelName(Class<? extends Page> pageClass) {
		String label = new ClassStringResourceLoader(pageClass).loadStringResource(null,
				CommonConstants.GLOBAL_ADMIN_BOX_LINK_LABEL);
		if (StringUtils.isEmpty(label)) {
			label = new ClassStringResourceLoader(pageClass).loadStringResource(null,
					CommonConstants.CONTENT_TITLE_LABEL);
		}
		return label;
	}

	public void addLink(Component link) {
		WebMarkupContainer container = new WebMarkupContainer(extendableRepeating.newChildId());
		extendableRepeating.add(container);
		container.add(link);
	}

	@Override
	public void setTitleVisible(boolean visible) {
		titleContainer.setVisible(visible);
	}
}
