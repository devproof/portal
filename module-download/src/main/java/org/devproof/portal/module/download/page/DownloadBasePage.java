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
package org.devproof.portal.module.download.page;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.module.deadlinkcheck.panel.DeadlinkCheckPanel;
import org.devproof.portal.module.download.DownloadConstants;
import org.devproof.portal.module.download.entity.Download;
import org.devproof.portal.module.download.service.DownloadService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Carsten Hufe
 */
public abstract class DownloadBasePage extends TemplatePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "downloadService")
    private DownloadService downloadService;
    private WebMarkupContainer bubblePanel;

    public DownloadBasePage(PageParameters params) {
        super(params);
        add(createCSSHeaderContributor());
        add(createHiddenBubbleWindow());
    }

    @Override
    protected List<Component> newPageAdminBoxLinks(String linkMarkupId, String labelMarkupId) {
        if(isAuthor()) {
            List<Component> links = new ArrayList<Component>();
            links.add(createDownloadAddLink(linkMarkupId, labelMarkupId));
            links.add(createDeadlinkCheckLink(linkMarkupId, labelMarkupId));
            return links;
        }
        return super.newPageAdminBoxLinks(linkMarkupId, labelMarkupId);
    }

    private HeaderContributor createCSSHeaderContributor() {
        return CSSPackageResource.getHeaderContribution(DownloadConstants.REF_DOWNLOAD_CSS);
    }

    private WebMarkupContainer createHiddenBubbleWindow() {
        if (isAuthor()) {
            bubblePanel = new BubblePanel("bubblePanel");

        } else {
            bubblePanel = new WebMarkupContainer("bubblePanel");
            bubblePanel.setVisible(false);
        }
        return bubblePanel;
    }

    private AjaxLink<Download> createDeadlinkCheckLink(String linkMarkupId, String labelMarkupId) {
        AjaxLink<Download> deadlinkCheckLink = newDeadlinkCheckLink(linkMarkupId);
        deadlinkCheckLink.add(new Label(labelMarkupId, getString("deadlinkCheckLink")));
        return deadlinkCheckLink;
    }

    private AjaxLink<Download> newDeadlinkCheckLink(String linkMarkupId) {
        return new AjaxLink<Download>(linkMarkupId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                BubblePanel panel = (BubblePanel) DownloadBasePage.this.bubblePanel;
                DeadlinkCheckPanel<Download> deadlinkPanel = createDeadlinkCheckPanel(panel.getContentId());
                panel.setContent(deadlinkPanel);
                panel.showModal(target);
            }

            private DeadlinkCheckPanel<Download> createDeadlinkCheckPanel(String id) {
                IModel<List<Download>> allDownloadsModel = createAllDownloadsModel();
                return new DeadlinkCheckPanel<Download>(id, "download", allDownloadsModel) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onBroken(Download broken) {
                        downloadService.markBrokenDownload(broken);
                    }

                    @Override
                    public void onValid(Download valid) {
                        downloadService.markValidDownload(valid);
                    }

                    @Override
                    public void onCancel(AjaxRequestTarget target) {
                        BubblePanel panel = (BubblePanel) bubblePanel;
                        panel.hide(target);
                        setResponsePage(DownloadPage.class);
                    }
                };
            }

        };
    }

    private IModel<List<Download>> createAllDownloadsModel() {
        return new LoadableDetachableModel<List<Download>>() {
            private static final long serialVersionUID = -3648230899434788060L;

            @Override
            protected List<Download> load() {
                return downloadService.findAll();
            }
        };
    }

    private Link<?> createDownloadAddLink(String linkMarkupId, String labelMarkupId) {
        Link<?> addLink = newDownloadAddLink(linkMarkupId);
        addLink.add(new Label(labelMarkupId, getString("createLink")));
        return addLink;
    }

    private Link<?> newDownloadAddLink(String linkMarkupId) {
        return new Link<Void>(linkMarkupId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                Download newDownload = downloadService.newDownloadEntity();
                IModel<Download> downloadModel = Model.of(newDownload);
                setResponsePage(new DownloadEditPage(downloadModel));
            }
        };
    }

    public boolean isAuthor() {
        PortalSession session = (PortalSession) getSession();
        return session.hasRight(DownloadConstants.AUTHOR_RIGHT);
    }
}
