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
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
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
	private boolean isAuthor;

	public DownloadBasePage(PageParameters params) {
		super(params);
		setAuthorRight();
		add(createCSSHeaderContributor());
		addDownloadAddLink();
		addDeadlinkCheckLink();
		add(createHiddenModalWindow());
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

	private WebMarkupContainer createHiddenModalWindow() {
		if (isAuthor()) {
			modalWindow = new ModalWindow("modalWindow");

		} else {
			modalWindow = new WebMarkupContainer("modalWindow");
			modalWindow.setVisible(false);
		}
		return modalWindow;
	}

	private AjaxLink<DownloadEntity> createDeadlinkCheckLink() {
		AjaxLink<DownloadEntity> deadlinkCheckLink = newDeadlinkCheckLink();
		deadlinkCheckLink.add(new Label("linkName", getString("deadlinkCheckLink")));
		return deadlinkCheckLink;
	}

	private AjaxLink<DownloadEntity> newDeadlinkCheckLink() {
		AjaxLink<DownloadEntity> deadlinkCheckLink = new AjaxLink<DownloadEntity>("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				ModalWindow modalWindow = (ModalWindow) DownloadBasePage.this.modalWindow;
				DeadlinkCheckPanel<DownloadEntity> deadlinkPanel = createDeadlinkCheckPanel(modalWindow.getContentId());
				modalWindow.setInitialHeight(300);
				modalWindow.setInitialWidth(500);
				modalWindow.setContent(deadlinkPanel);
				modalWindow.show(target);
			}

			private DeadlinkCheckPanel<DownloadEntity> createDeadlinkCheckPanel(String id) {
				List<DownloadEntity> allDownloads = downloadService.findAll();
				return newDeadlinkCheckPanel(id, allDownloads);
			}

			private DeadlinkCheckPanel<DownloadEntity> newDeadlinkCheckPanel(String id,
					List<DownloadEntity> allDownloads) {
				DeadlinkCheckPanel<DownloadEntity> deadlinkPanel = new DeadlinkCheckPanel<DownloadEntity>(id,
						"download", allDownloads) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onBroken(DownloadEntity brokenEntity) {
						downloadService.markBrokenDownload(brokenEntity);
					}

					@Override
					public void onValid(DownloadEntity validEntity) {
						downloadService.markValidDownload(validEntity);
					}
				};
				return deadlinkPanel;
			}
		};
		return deadlinkCheckLink;
	}

	private Link<?> createDownloadAddLink() {
		Link<?> addLink = newDownloadAddLink();
		addLink.add(new Label("linkName", getString("createLink")));
		return addLink;
	}

	private Link<?> newDownloadAddLink() {
		return new Link<Object>("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				DownloadEntity newDownload = downloadService.newDownloadEntity();
				IModel<DownloadEntity> downloadModel = Model.of(newDownload);
				setResponsePage(new DownloadEditPage(downloadModel));
			}
		};
	}

	private void setAuthorRight() {
		PortalSession session = (PortalSession) getSession();
		isAuthor = session.hasRight("page." + DownloadEditPage.class.getSimpleName());
	}

	public boolean isAuthor() {
		return isAuthor;
	}

	public WebMarkupContainer getModalWindow() {
		return modalWindow;
	}
}
