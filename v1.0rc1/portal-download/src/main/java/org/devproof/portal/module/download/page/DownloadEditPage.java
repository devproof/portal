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

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
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

	public DownloadEditPage(final DownloadEntity download) {
		super(new PageParameters());
		final RightGridPanel viewRights = new RightGridPanel("viewRights", "download.view", download.getAllRights());
		final RightGridPanel downloadRights = new RightGridPanel("downloadRights", "download.download", download.getAllRights());
		final RightGridPanel voteRights = new RightGridPanel("voteRights", "download.vote", download.getAllRights());
		final TagField<DownloadTagEntity> tagField = new TagField<DownloadTagEntity>("tags", download.getTags(), this.downloadTagService);

		final Form<DownloadEntity> form = new Form<DownloadEntity>("form", new CompoundPropertyModel<DownloadEntity>(download)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				DownloadEditPage.this.setVisible(false);
				final DownloadEntity download = getModelObject();
				final List<RightEntity> selectedRights = new ArrayList<RightEntity>();
				selectedRights.addAll(viewRights.getSelectedRights());
				selectedRights.addAll(downloadRights.getSelectedRights());
				selectedRights.addAll(voteRights.getSelectedRights());
				download.setAllRights(selectedRights);
				download.setTags(tagField.getTagsAndStore());
				download.setBroken(Boolean.FALSE);
				DownloadEditPage.this.downloadService.save(download);
				setRedirect(false);
				info(this.getString("msg.saved"));
				this.setResponsePage(new DownloadPage(new PageParameters("id=" + download.getId())));
			}
		};
		form.setOutputMarkupId(true);
		form.add(tagField);
		form.add(viewRights);
		form.add(downloadRights);
		form.add(voteRights);
		this.add(form);

		FormComponent<String> fc;

		fc = new RequiredTextField<String>("title");
		fc.add(StringValidator.minimumLength(3));
		form.add(fc);
		fc = new RichTextArea("description");
		fc.add(StringValidator.minimumLength(3));
		form.add(fc);
		fc = new RequiredTextField<String>("url");
		form.add(fc);
		fc = new RequiredTextField<String>("hits");
		form.add(fc);
		fc = new RequiredTextField<String>("numberOfVotes");
		form.add(fc);
		fc = new RequiredTextField<String>("sumOfRating");
		form.add(fc);
		fc = new TextField<String>("softwareVersion");
		form.add(fc);
		fc = new TextField<String>("downloadSize");
		form.add(fc);
		fc = new TextField<String>("manufacturer");
		form.add(fc);
		fc = new TextField<String>("manufacturerHomepage");
		form.add(fc);
		fc = new TextField<String>("licence");
		form.add(fc);
		fc = new TextField<String>("price");
		form.add(fc);
	}

}
