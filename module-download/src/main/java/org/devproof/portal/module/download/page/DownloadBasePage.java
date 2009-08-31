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
package org.devproof.portal.module.download.page;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.page.TemplatePage;
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
	private WebMarkupContainer modalWindow;
	private final boolean isAuthor;

	public DownloadBasePage(final PageParameters params) {
		super(params);
		add(CSSPackageResource.getHeaderContribution(DownloadConstants.REF_DOWNLOAD_CSS));
		PortalSession session = (PortalSession) getSession();
		isAuthor = session.hasRight("page.DownloadEditPage");
		if (isAuthor) {
			modalWindow = new ModalWindow("modalWindow");
			Link<?> addLink = new Link<Object>("adminLink") {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick() {
					final DownloadEntity newDownload = downloadService.newDownloadEntity();
					setResponsePage(new DownloadEditPage(newDownload));
				}
			};
			addLink.add(new Label("linkName", getString("createLink")));
			addPageAdminBoxLink(addLink);
			AjaxLink<DownloadEntity> deadlinkCheckLink = new AjaxLink<DownloadEntity>("adminLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(final AjaxRequestTarget target) {
					final ModalWindow modalWindow = (ModalWindow) DownloadBasePage.this.modalWindow;
					List<DownloadEntity> allDownloads = downloadService.findAll();
					final DeadlinkCheckPanel<DownloadEntity> deadlinkPanel = new DeadlinkCheckPanel<DownloadEntity>(
							modalWindow.getContentId(), "download", allDownloads) {
						private static final long serialVersionUID = 1L;

						@Override
						public void onBroken(final DownloadEntity brokenEntity) {
							downloadService.markBrokenDownload(brokenEntity);
						}

						@Override
						public void onValid(final DownloadEntity validEntity) {
							downloadService.markValidDownload(validEntity);
						}
					};
					modalWindow.setInitialHeight(300);
					modalWindow.setInitialWidth(500);
					modalWindow.setContent(deadlinkPanel);
					modalWindow.show(target);
				}
			};
			deadlinkCheckLink.add(new Label("linkName", getString("deadlinkCheckLink")));
			addPageAdminBoxLink(deadlinkCheckLink);
		} else {
			modalWindow = new WebMarkupContainer("modalWindow");
			modalWindow.setVisible(false);
		}
		add(modalWindow);
	}

	public boolean isAuthor() {
		return isAuthor;
	}

	public WebMarkupContainer getModalWindow() {
		return modalWindow;
	}
}
