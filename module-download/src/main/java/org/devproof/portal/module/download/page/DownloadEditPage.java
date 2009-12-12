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
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.devproof.portal.core.module.common.component.richtext.RichTextArea;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.right.panel.RightGridPanel;
import org.devproof.portal.core.module.tag.component.TagField;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.download.entity.DownloadEntity;
import org.devproof.portal.module.download.entity.DownloadTagEntity;
import org.devproof.portal.module.download.service.DownloadService;

/**
 * @author Carsten Hufe
 */
public class DownloadEditPage extends DownloadBasePage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "downloadService")
	private DownloadService downloadService;
	@SpringBean(name = "downloadTagService")
	private TagService<DownloadTagEntity> downloadTagService;

	private DownloadEntity download;

	public DownloadEditPage(DownloadEntity download) {
		super(new PageParameters());
		this.download = download;
		add(createDownloadEditForm());
	}

	private Form<DownloadEntity> createDownloadEditForm() {
		Form<DownloadEntity> form = newDownloadEditForm();
		form.setOutputMarkupId(true);
		form.add(createTitleField());
		form.add(createDescriptionField());
		form.add(createUrlField());
		form.add(createSoftwareVersionField());
		form.add(createDownloadSizeField());
		form.add(createManufacturerField());
		form.add(createLicenceField());
		form.add(createTagField());
		form.add(createPriceField());
		form.add(createHitsField());
		form.add(createNumberOfVotesField());
		form.add(createSumOfRatingField());
		form.add(createManufacturerHomepageField());
		form.add(createViewRightPanel());
		form.add(createDownloadRightPanel());
		form.add(createVoteRightPanel());
		return form;
	}

	private FormComponent<String> createSoftwareVersionField() {
		return new TextField<String>("softwareVersion");
	}

	private FormComponent<String> createSumOfRatingField() {
		return new RequiredTextField<String>("sumOfRating");
	}

	private FormComponent<String> createNumberOfVotesField() {
		return new RequiredTextField<String>("numberOfVotes");
	}

	private FormComponent<String> createHitsField() {
		return new RequiredTextField<String>("hits");
	}

	private FormComponent<String> createUrlField() {
		return new RequiredTextField<String>("url");
	}

	private FormComponent<String> createDescriptionField() {
		FormComponent<String> fc = new RichTextArea("description");
		fc.add(StringValidator.minimumLength(3));
		return fc;
	}

	private FormComponent<String> createTitleField() {
		FormComponent<String> fc = new RequiredTextField<String>("title");
		fc.add(StringValidator.minimumLength(3));
		return fc;
	}

	private FormComponent<String> createDownloadSizeField() {
		return new TextField<String>("downloadSize");
	}

	private FormComponent<String> createManufacturerField() {
		FormComponent<String> fc;
		fc = new TextField<String>("manufacturer");
		return fc;
	}

	private FormComponent<String> createManufacturerHomepageField() {
		return new TextField<String>("manufacturerHomepage");
	}

	private FormComponent<String> createLicenceField() {
		return new TextField<String>("licence");
	}

	private FormComponent<String> createPriceField() {
		return new TextField<String>("price");
	}

	private TagField<DownloadTagEntity> createTagField() {
		IModel<List<DownloadTagEntity>> downloadListModel = new PropertyModel<List<DownloadTagEntity>>(download, "tags");
		return new TagField<DownloadTagEntity>("tags", downloadListModel, downloadTagService);
	}

	private Form<DownloadEntity> newDownloadEditForm() {
		return new Form<DownloadEntity>("form", new CompoundPropertyModel<DownloadEntity>(download)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				DownloadEditPage.this.setVisible(false);
				final DownloadEntity download = getModelObject();
				download.setBroken(Boolean.FALSE);
				downloadService.save(download);
				setRedirect(false);
				info(getString("msg.saved"));
				setResponsePage(new DownloadPage(new PageParameters("id=" + download.getId())));
			}
		};
	}

	private RightGridPanel createVoteRightPanel() {
		ListModel<RightEntity> rightListModel = new ListModel<RightEntity>(download.getAllRights());
		return new RightGridPanel("voteRights", "download.vote", rightListModel);
	}

	private RightGridPanel createDownloadRightPanel() {
		ListModel<RightEntity> rightListModel = new ListModel<RightEntity>(download.getAllRights());
		return new RightGridPanel("downloadRights", "download.download", rightListModel);
	}

	private RightGridPanel createViewRightPanel() {
		ListModel<RightEntity> rightListModel = new ListModel<RightEntity>(download.getAllRights());
		return new RightGridPanel("viewRights", "download.view", rightListModel);
	}
}
