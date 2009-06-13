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
package org.devproof.portal.module.uploadcenter.page;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
	private final File uploadFolder;

	public UploadFilePage(final File uploadFolder) {
		this.add(CSSPackageResource.getHeaderContribution(CommonConstants.class, "css/default.css"));
		this.uploadFolder = uploadFolder;
		final FeedbackPanel uploadFeedback = new FeedbackPanel("uploadFeedback");
		this.add(uploadFeedback);
		final Collection<FileUpload> uploads = new ArrayList<FileUpload>();
		final IModel<Collection<FileUpload>> uploadModel = new CollectionModel<FileUpload>(uploads);

		final Form<Collection<FileUpload>> uploadForm = new Form<Collection<FileUpload>>("uploadForm", uploadModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				Iterator<FileUpload> it = uploads.iterator();
				while (it.hasNext()) {
					final FileUpload upload = it.next();
					File newFile = new File(UploadFilePage.this.getUploadFolder(), upload.getClientFileName());

					UploadFilePage.this.deleteFile(newFile);
					try {
						newFile.createNewFile();
						upload.writeTo(newFile);
						UploadFilePage.this.info(new StringResourceModel("msg.uploaded", UploadFilePage.this, null, new Object[] { upload.getClientFileName() }).getString());
					} catch (Exception e) {
						throw new IllegalStateException("Unable to write file");
					}
				}
				super.onSubmit();
			}
		};
		// set this form to multipart mode (allways needed for uploads!)
		uploadForm.setMultiPart(true);
		uploadForm.add(new MultiFileUploadField("fileInput", uploadModel, this.configurationService.findAsInteger(UploadCenterConstants.CONF_UPLOADCENTER_MAXFILES)));
		uploadForm.setMaxSize(Bytes.kilobytes(this.configurationService.findAsInteger(UploadCenterConstants.CONF_UPLOADCENTER_MAXSIZE)));
		this.add(uploadForm);
		uploadForm.add(new UploadProgressBar("progress", uploadForm));

	}

	private void deleteFile(final File newFile) {
		if (newFile.exists()) {
			if (!Files.remove(newFile)) {
				throw new IllegalStateException("Unable to overwrite " + newFile.getAbsolutePath());
			}
		}
	}

	private Folder getUploadFolder() {
		return new Folder(this.uploadFolder.getAbsolutePath());
	}
}
