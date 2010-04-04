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
package org.devproof.portal.module.download.page;

import java.util.List;

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
import org.devproof.portal.module.download.entity.DownloadEntity;
import org.devproof.portal.module.download.service.DownloadService;

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
        addDownloadAddLink();
        addDeadlinkCheckLink();
    }

    private HeaderContributor createCSSHeaderContributor() {
        return CSSPackageResource.getHeaderContribution(DownloadConstants.REF_DOWNLOAD_CSS);
    }

    private void addDeadlinkCheckLink() {
        if (isAuthor()) {
            addPageAdminBoxLink(createDeadlinkCheckLink());
        }
    }

    private void addDownloadAddLink() {
        if (isAuthor()) {
            addPageAdminBoxLink(createDownloadAddLink());
        }
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

    private AjaxLink<DownloadEntity> createDeadlinkCheckLink() {
        AjaxLink<DownloadEntity> deadlinkCheckLink = newDeadlinkCheckLink();
        deadlinkCheckLink.add(new Label("linkName", getString("deadlinkCheckLink")));
        return deadlinkCheckLink;
    }

    private AjaxLink<DownloadEntity> newDeadlinkCheckLink() {
        return new AjaxLink<DownloadEntity>("adminLink") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                BubblePanel panel = (BubblePanel) DownloadBasePage.this.bubblePanel;
                DeadlinkCheckPanel<DownloadEntity> deadlinkPanel = createDeadlinkCheckPanel(panel.getContentId());
                panel.setContent(deadlinkPanel);
                panel.showModal(target);
            }

            private DeadlinkCheckPanel<DownloadEntity> createDeadlinkCheckPanel(String id) {
                IModel<List<DownloadEntity>> allDownloadsModel = createAllDownloadsModel();
                return new DeadlinkCheckPanel<DownloadEntity>(id, "download", allDownloadsModel) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onBroken(DownloadEntity brokenEntity) {
                        downloadService.markBrokenDownload(brokenEntity);
                    }

                    @Override
                    public void onValid(DownloadEntity validEntity) {
                        downloadService.markValidDownload(validEntity);
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

    private IModel<List<DownloadEntity>> createAllDownloadsModel() {
        return new LoadableDetachableModel<List<DownloadEntity>>() {
            private static final long serialVersionUID = -3648230899434788060L;

            @Override
            protected List<DownloadEntity> load() {
                return downloadService.findAll();
            }
        };
    }

    private Link<?> createDownloadAddLink() {
        Link<?> addLink = newDownloadAddLink();
        addLink.add(new Label(getPageAdminBoxLinkLabelId(), getString("createLink")));
        return addLink;
    }

    private Link<?> newDownloadAddLink() {
        return new Link<Void>(getPageAdminBoxLinkId()) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                DownloadEntity newDownload = downloadService.newDownloadEntity();
                IModel<DownloadEntity> downloadModel = Model.of(newDownload);
                setResponsePage(new DownloadEditPage(downloadModel));
            }
        };
    }

    public boolean isAuthor() {
        PortalSession session = (PortalSession) getSession();
        return session.hasRight(DownloadConstants.AUTHOR_RIGHT);
    }
}
