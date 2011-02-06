/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.common.panel;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.resource.loader.ClassStringResourceLoader;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.config.NavigationBox;
import org.devproof.portal.core.module.box.panel.BoxTitleVisibility;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.registry.GlobalAdminPageRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Carsten Hufe
 */
@NavigationBox("Global Admin Box")
public class GlobalAdminBoxPanel extends Panel implements BoxTitleVisibility {

    private static final long serialVersionUID = 1L;

    @SpringBean(name = "globalAdminPageRegistry")
    private GlobalAdminPageRegistry adminPageRegistry;
    private WebMarkupContainer titleContainer;
    private IModel<List<Class<? extends Page>>> registeredAdminPageModel;

    public GlobalAdminBoxPanel(String id) {
        super(id);
        registeredAdminPageModel = createRegisteredAdminPageModel();
        add(createTitleContainer());
        add(createRepeatingNavExtendable());
    }

    @Override
    public boolean isVisible() {
        List<Class<? extends Page>> adminPages = registeredAdminPageModel.getObject();
        return adminPages.size() > 0;
    }

    private ListView<?> createRepeatingNavExtendable() {
        return new ListView<Class<? extends Page>>("repeatingNavExtendable", registeredAdminPageModel) {
            private static final long serialVersionUID = -277523349047078562L;

            @Override
            protected void populateItem(ListItem<Class<? extends Page>> item) {
                Class<? extends Page> pageClass = item.getModelObject();
                item.add(createAdminLink(pageClass));
            }

            private BookmarkablePageLink<Void> createAdminLink(Class<? extends Page> pageClass) {
                BookmarkablePageLink<Void> link = new BookmarkablePageLink<Void>("adminLink", pageClass);
                link.add(createAdminLinkLabel(pageClass));
                return link;
            }
        };
    }

    private IModel<List<Class<? extends Page>>> createRegisteredAdminPageModel() {
        return new LoadableDetachableModel<List<Class<? extends Page>>>() {
            private static final long serialVersionUID = -4836280928165419121L;

            @Override
            protected List<Class<? extends Page>> load() {
                List<Class<? extends Page>> pages = adminPageRegistry.getRegisteredGlobalAdminPages();
                List<Class<? extends Page>> filteredPages = new ArrayList<Class<? extends Page>>(pages.size());
                PortalSession session = PortalSession.get();
                for (Class<? extends Page> page : pages) {
                    if (session.hasRight(page)) {
                        filteredPages.add(page);
                    }
                }
                return filteredPages;
            }
        };
    }

    private Label createAdminLinkLabel(Class<? extends Page> pageClass) {
        IModel<String> pageClassModel = createPageClassModel(pageClass);
        return new Label("adminLinkLabel", pageClassModel);
    }

    private AbstractReadOnlyModel<String> createPageClassModel(final Class<? extends Page> pageClass) {
        return new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = -302528457464799755L;

            @Override
            public String getObject() {
                return getLinkNameByClass(pageClass);
            }
        };
    }

    private String getLinkNameByClass(Class<? extends Page> pageClass) {
        String label = new ClassStringResourceLoader(pageClass).loadStringResource(null, CommonConstants.GLOBAL_ADMIN_BOX_LINK_LABEL);
        if (StringUtils.isEmpty(label)) {
            label = new ClassStringResourceLoader(pageClass).loadStringResource(null, CommonConstants.CONTENT_TITLE_LABEL);
        }
        return label;
    }

    private WebMarkupContainer createTitleContainer() {
        titleContainer = new WebMarkupContainer("title");
        return titleContainer;
    }

    @Override
    public void setTitleVisible(boolean visible) {
        titleContainer.setVisible(visible);
    }
}
