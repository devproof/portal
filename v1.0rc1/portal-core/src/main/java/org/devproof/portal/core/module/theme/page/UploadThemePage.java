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
package org.devproof.portal.core.module.theme.page;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.theme.service.ThemeService;
import org.devproof.portal.core.module.theme.service.ThemeService.ValidationKey;

/**
 * Upload form for themes
 * 
 * @author Carsten Hufe
 */
public class UploadThemePage extends WebPage {
	@SpringBean(name = "themeService")
	private ThemeService themeService;

	public UploadThemePage() {
		this.add(CSSPackageResource.getHeaderContribution(CommonConstants.class, "css/default.css"));
		final FeedbackPanel uploadFeedback = new FeedbackPanel("uploadFeedback");
		this.add(uploadFeedback);
		final FileUploadField uploadField = new FileUploadField("fileInput");
		uploadField.setRequired(true);
		final Form<FileUpload> uploadForm = new Form<FileUpload>("uploadForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				FileUpload fileUpload = uploadField.getFileUpload();
				try {
					File tmpFile = fileUpload.writeToTempFile();
					ValidationKey key = UploadThemePage.this.themeService.validateTheme(tmpFile);
					if (key == ValidationKey.VALID) {
						UploadThemePage.this.themeService.install(tmpFile);
						info(this.getString("msg.installed"));
					} else if (key == ValidationKey.INVALID_DESCRIPTOR_FILE) {
						this.error(this.getString("msg.invalid_descriptor_file"));
					} else if (key == ValidationKey.MISSING_DESCRIPTOR_FILE) {
						this.error(this.getString("msg.missing_descriptor_file"));
					} else if (key == ValidationKey.NOT_A_JARFILE) {
						this.error(this.getString("msg.not_a_jarfile"));
					} else if (key == ValidationKey.WRONG_VERSION) {
						this.error(this.getString("wrong_version"));
					}
					tmpFile.delete();
				} catch (IOException e) {
					throw new UnhandledException(e);
				}
				super.onSubmit();
			}
		};
		// set this form to multipart mode (allways needed for uploads!)
		uploadForm.setMultiPart(true);
		uploadForm.add(uploadField);
		this.add(uploadForm);
		uploadForm.add(new UploadProgressBar("progress", uploadForm));
	}
}
