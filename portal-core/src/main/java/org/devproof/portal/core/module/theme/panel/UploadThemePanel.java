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
package org.devproof.portal.core.module.theme.panel;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.UnhandledException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.theme.service.ThemeService;
import org.devproof.portal.core.module.theme.service.ThemeService.ValidationKey;

/**
 * Upload form for themes
 * 
 * @author Carsten Hufe
 */
public abstract class UploadThemePanel extends Panel {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(UploadThemePanel.class);

	@SpringBean(name = "themeService")
	private ThemeService themeService;
	private Form<FileUpload> uploadForm;
	private FileUploadField uploadField;

	public UploadThemePanel(String id) {
		super(id);
		add(createCSSHeaderContributor());
		add(createFeedbackPanel());
		add(createUploadForm());
	}

	private Form<FileUpload> createUploadForm() {
		Form<FileUpload> uploadForm = newUploadForm();
		uploadForm.add(createUploadField());
		uploadForm.add(createUploadProgressBar());
		uploadForm.add(createCancelButton());
		uploadForm.setMultiPart(true);
		return uploadForm;
	}

	private UploadProgressBar createUploadProgressBar() {
		return new UploadProgressBar("progress", uploadForm);
	}

	private Form<FileUpload> newUploadForm() {
		uploadForm = new Form<FileUpload>("uploadForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				FileUpload fileUpload = uploadField.getFileUpload();
				try {
					File tmpFile = fileUpload.writeToTempFile();
					ValidationKey key = themeService.validateTheme(tmpFile);
					if (key == ValidationKey.VALID) {
						themeService.install(tmpFile);
						info(getString("msg.installed"));
					} else {
						handleErrorMessage(key);
					}
					if (!tmpFile.delete()) {
						LOG.error("Could not delete " + tmpFile);
					}
				} catch (IOException e) {
					throw new UnhandledException(e);
				}
				UploadThemePanel.this.onSubmit();
				super.onSubmit();
			}

			private void handleErrorMessage(ValidationKey key) {
				if (key == ValidationKey.INVALID_DESCRIPTOR_FILE) {
					error(getString("msg.invalid_descriptor_file"));
				} else if (key == ValidationKey.MISSING_DESCRIPTOR_FILE) {
					error(getString("msg.missing_descriptor_file"));
				} else if (key == ValidationKey.NOT_A_JARFILE) {
					error(getString("msg.not_a_jarfile"));
				} else if (key == ValidationKey.WRONG_VERSION) {
					error(getString("wrong_version"));
				} else {
					throw new IllegalArgumentException("Unknown ValidationKey: " + key);
				}
			}
		};
		return uploadForm;
	}

	private FileUploadField createUploadField() {
		uploadField = new FileUploadField("fileInput");
		uploadField.setRequired(true);
		return uploadField;
	}

	private AjaxLink<Void> createCancelButton() {
		return new AjaxLink<Void>("cancelButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				onCancel(target);
			}
		};
	}

	private FeedbackPanel createFeedbackPanel() {
		FeedbackPanel uploadFeedback = new FeedbackPanel("uploadFeedback");
		return uploadFeedback;
	}

	private HeaderContributor createCSSHeaderContributor() {
		return CSSPackageResource.getHeaderContribution(CommonConstants.class, "css/default.css");
	}

	public abstract void onSubmit();

	public abstract void onCancel(AjaxRequestTarget target);
}
