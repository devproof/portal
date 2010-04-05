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
package org.devproof.portal.module.download.panel;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.box.panel.BoxTitleVisibility;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.module.download.DownloadConstants;
import org.devproof.portal.module.download.entity.DownloadEntity;
import org.devproof.portal.module.download.page.DownloadPage;
import org.devproof.portal.module.download.service.DownloadService;

import java.util.List;

/**
 * @author Carsten Hufe
 */
public class DownloadBoxPanel extends Panel implements BoxTitleVisibility {

    private static final long serialVersionUID = 1L;

    @SpringBean(name = "downloadService")
    private DownloadService downloadService;
    @SpringBean(name = "configurationService")
    private ConfigurationService configurationService;
    private WebMarkupContainer titleContainer;
    private IModel<List<DownloadEntity>> latestDownloadsModel;

    public DownloadBoxPanel(String id) {
        super(id);
        latestDownloadsModel = createLatestDownloadsModel();
        add(createRepeatingDownloads());
        add(createTitleContainer());
    }

    @Override
    public boolean isVisible() {
        List<DownloadEntity> latestDownloads = latestDownloadsModel.getObject();
        return latestDownloads.size() > 0;
    }

    private WebMarkupContainer createTitleContainer() {
        titleContainer = new WebMarkupContainer("title");
        return titleContainer;
    }

    private ListView<DownloadEntity> createRepeatingDownloads() {
        return new ListView<DownloadEntity>("repeatingDownloads") {
            private static final long serialVersionUID = -1523488276282233553L;

            @Override
            protected void populateItem(ListItem<DownloadEntity> item) {
                item.add(createLinkToDownload(item.getModel()));
            }
        };
    }

    private BookmarkablePageLink<DownloadPage> createLinkToDownload(IModel<DownloadEntity> downloadModel) {
        DownloadEntity download = downloadModel.getObject();
        BookmarkablePageLink<DownloadPage> link = new BookmarkablePageLink<DownloadPage>("link", DownloadPage.class);
        link.setParameter("id", download.getId());
        link.add(createLinkToDownloadLabel(downloadModel));
        return link;
    }

    private Label createLinkToDownloadLabel(IModel<DownloadEntity> downloadModel) {
        IModel<Object> titleModel = new PropertyModel<Object>(downloadModel, "title");
        return new Label("linkName", titleModel);
    }

    private IModel<List<DownloadEntity>> createLatestDownloadsModel() {
        return new LoadableDetachableModel<List<DownloadEntity>>() {
            private static final long serialVersionUID = 7003739130115325197L;

            @Override
            protected List<DownloadEntity> load() {
                PortalSession session = (PortalSession) getSession();
                Integer num = configurationService.findAsInteger(DownloadConstants.CONF_BOX_NUM_LATEST_DOWNLOADS);
                return downloadService.findAllDownloadsForRoleOrderedByDateDesc(session.getRole(), 0, num);
            }
        };
    }

    @Override
    public void setTitleVisible(boolean visible) {
        titleContainer.setVisible(visible);
    }
}
