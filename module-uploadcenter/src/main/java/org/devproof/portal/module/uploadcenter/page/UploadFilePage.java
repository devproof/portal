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
package org.devproof.portal.module.uploadcenter.page;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.MultiFileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;
import org.apache.wicket.util.lang.Bytes;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.module.uploadcenter.UploadCenterConstants;

/**
 * @author Carsten Hufe
 */
public class UploadFilePage extends WebPage {
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;
	private File uploadFolder;
	private IModel<Collection<FileUpload>> uploadModel;

	public UploadFilePage(File uploadFolder) {
		add(CSSPackageResource.getHeaderContribution(CommonConstants.class, "css/default.css"));
		this.uploadFolder = uploadFolder;
		uploadModel = new CollectionModel<FileUpload>(new ArrayList<FileUpload>());
		add(createFeedbackPanel());
		add(createUploadForm());
	}

	private Form<Collection<FileUpload>> createUploadForm() {
		Form<Collection<FileUpload>> uploadForm = newUploadForm();
		// set this form to multipart mode (allways needed for uploads!)
		uploadForm.setMultiPart(true);
		uploadForm.add(createMultiFileUploadField());
		uploadForm.setMaxSize(getMaxFileSize());
		uploadForm.add(createUploadProgressBar(uploadForm));
		return uploadForm;
	}

	private UploadProgressBar createUploadProgressBar(Form<Collection<FileUpload>> uploadForm) {
		return new UploadProgressBar("progress", uploadForm);
	}

	private Bytes getMaxFileSize() {
		return Bytes.kilobytes(configurationService.findAsInteger(UploadCenterConstants.CONF_UPLOADCENTER_MAXSIZE));
	}

	private MultiFileUploadField createMultiFileUploadField() {
		return new MultiFileUploadField("fileInput", uploadModel, configurationService
				.findAsInteger(UploadCenterConstants.CONF_UPLOADCENTER_MAXFILES));
	}

	private FeedbackPanel createFeedbackPanel() {
		FeedbackPanel uploadFeedback = new FeedbackPanel("uploadFeedback");
		return uploadFeedback;
	}

	private Form<Collection<FileUpload>> newUploadForm() {
		return new Form<Collection<FileUpload>>("uploadForm", uploadModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				Iterator<FileUpload> it = uploadModel.getObject().iterator();
				while (it.hasNext()) {
					FileUpload upload = it.next();
					File newFile = new File(UploadFilePage.this.getUploadFolder(), upload.getClientFileName());
					UploadFilePage.this.deleteFile(newFile);
					try {
						if (newFile.createNewFile()) {
							upload.writeTo(newFile);
							UploadFilePage.this.info(new StringResourceModel("msg.uploaded", UploadFilePage.this, null,
									new Object[] { upload.getClientFileName() }).getString());
						} else {
							throw new IllegalStateException("Unable to write file" + newFile);
						}
					} catch (Exception e) {
						throw new UnhandledException(e);
					}
				}
				super.onSubmit();
			}
		};
	}

	private void deleteFile(File newFile) {
		if (newFile.exists()) {
			if (!Files.remove(newFile)) {
				throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
			}
		}
	}

	private Folder getUploadFolder() {
		return new Folder(uploadFolder.getAbsolutePath());
	}
}
