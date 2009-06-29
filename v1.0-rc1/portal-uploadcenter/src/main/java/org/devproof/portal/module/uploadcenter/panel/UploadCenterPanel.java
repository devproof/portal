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
package org.devproof.portal.module.uploadcenter.panel;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.component.ExternalImage;
import org.devproof.portal.core.module.common.component.InternalDownloadLink;
import org.devproof.portal.core.module.common.factory.CommonPageFactory;
import org.devproof.portal.core.module.common.panel.ConfirmDeletePanel;
import org.devproof.portal.core.module.common.registry.SharedRegistry;
import org.devproof.portal.module.uploadcenter.UploadCenterConstants;

/**
 * @author Carsten Hufe
 */
public abstract class UploadCenterPanel extends Panel {
	private static final long serialVersionUID = 1L;

	@SpringBean(name = "sharedRegistry")
	private SharedRegistry sharedRegistry;

	public UploadCenterPanel(final String id, final IModel<File> model, final ModalWindow modalWindow, final boolean createDownload) {
		super(id, model);
		final File file = model.getObject();
		final Link<File> createDownloadLink = new Link<File>("createDownloadLink", model) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				final CommonPageFactory createDownloadPage = UploadCenterPanel.this.sharedRegistry.getResource("createDownloadPage");
				this.setResponsePage(createDownloadPage.newInstance(model.getObject().toURI().toString()));
			}

		};
		createDownloadLink.setVisible((file == null || file.isFile()) && createDownload && this.sharedRegistry.isResourceAvailable("createDownloadPage"));
		createDownloadLink.add(new ExternalImage("createDownloadImage", UploadCenterConstants.REF_GALLERY_IMG));
		this.add(createDownloadLink);

		final InternalDownloadLink downloadLink = new InternalDownloadLink("downloadLink") {
			private static final long serialVersionUID = 1L;

			@Override
			protected File getFile() {
				return file;
			}

		};
		downloadLink.add(new ExternalImage("downloadImage", UploadCenterConstants.REF_DOWNLOAD_IMG));
		downloadLink.setVisible(file == null || file.isFile());
		this.add(downloadLink);

		this.add(new AjaxLink<File>("deleteLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				final ConfirmDeletePanel<File> confirmDeletePanel = new ConfirmDeletePanel<File>(modalWindow.getContentId(), file, modalWindow) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onDelete(final AjaxRequestTarget target, final Form<?> form) {
						if (file.isDirectory()) {
							try {
								FileUtils.deleteDirectory(file);
							} catch (final IOException e) {
								throw new UnhandledException(e);
							}
						} else {
							file.delete();
						}
						UploadCenterPanel.this.onDelete(target);
						modalWindow.close(target);
					}

				};
				modalWindow.setContent(confirmDeletePanel);
				modalWindow.show(target);
			}
		}.add(new ExternalImage("deleteImage", CommonConstants.REF_DELETE_IMG)));
	}

	public abstract void onDelete(final AjaxRequestTarget target);
}
