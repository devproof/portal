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
package org.devproof.portal.module.bookmark.page;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.devproof.portal.core.module.common.component.richtext.RichTextArea;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.right.panel.RightGridPanel;
import org.devproof.portal.core.module.tag.component.TagField;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity;
import org.devproof.portal.module.bookmark.entity.BookmarkTagEntity;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity.Source;
import org.devproof.portal.module.bookmark.service.BookmarkService;

/**
 * @author Carsten Hufe
 */
public class BookmarkEditPage extends BookmarkBasePage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "bookmarkService")
	private transient BookmarkService bookmarkService;
	@SpringBean(name = "bookmarkTagService")
	private transient TagService<BookmarkTagEntity> bookmarkTagService;

	public BookmarkEditPage(final BookmarkEntity bookmark) {
		super(new PageParameters());
		final RightGridPanel viewRights = new RightGridPanel("viewRights", "bookmark.view", bookmark.getAllRights());
		final RightGridPanel visitRights = new RightGridPanel("visitRights", "bookmark.visit", bookmark.getAllRights());
		final RightGridPanel voteRights = new RightGridPanel("voteRights", "bookmark.vote", bookmark.getAllRights());
		final TagField<BookmarkTagEntity> tagField = new TagField<BookmarkTagEntity>("tags", bookmark.getTags(), this.bookmarkTagService);

		final Form<BookmarkEntity> form = new Form<BookmarkEntity>("form", new CompoundPropertyModel<BookmarkEntity>(bookmark)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				BookmarkEditPage.this.setVisible(false);
				final BookmarkEntity bookmark = getModelObject();
				final List<RightEntity> selectedRights = new ArrayList<RightEntity>();
				selectedRights.addAll(viewRights.getSelectedRights());
				selectedRights.addAll(visitRights.getSelectedRights());
				selectedRights.addAll(voteRights.getSelectedRights());
				bookmark.setAllRights(selectedRights);
				bookmark.setTags(tagField.getTagsAndStore());
				bookmark.setBroken(Boolean.FALSE);
				bookmark.setSource(Source.MANUAL);
				BookmarkEditPage.this.bookmarkService.save(bookmark);
				setRedirect(false);
				info(this.getString("msg.saved"));
				this.setResponsePage(new BookmarkPage(new PageParameters("id=" + bookmark.getId())));
			}
		};
		form.setOutputMarkupId(true);
		form.add(tagField);
		form.add(viewRights);
		form.add(visitRights);
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
	}
}
