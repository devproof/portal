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
package org.devproof.portal.module.bookmark.page;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
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
	private BookmarkService bookmarkService;
	@SpringBean(name = "bookmarkTagService")
	private TagService<BookmarkTagEntity> bookmarkTagService;

	private BookmarkEntity bookmark;

	public BookmarkEditPage(BookmarkEntity bookmark) {
		super(new PageParameters());
		this.bookmark = bookmark;
		add(createBookmarkEditForm());
	}

	private Form<BookmarkEntity> createBookmarkEditForm() {
		Form<BookmarkEntity> form = newBookmarkEditForm();
		form.add(createTitleField());
		form.add(createDescriptionField());
		form.add(createUrlField());
		form.add(createHitsField());
		form.add(createNumberOfVotesField());
		form.add(createSumOfRatingField());
		form.add(createTagField());
		form.add(createViewRightPanel());
		form.add(createVisitRightPanel());
		form.add(createVoteRightPanel());
		form.setOutputMarkupId(true);
		return form;
	}

	private FormComponent<String> createUrlField() {
		return new RequiredTextField<String>("url");
	}

	private FormComponent<String> createTitleField() {
		FormComponent<String> fc = new RequiredTextField<String>("title");
		fc.add(StringValidator.minimumLength(3));
		return fc;
	}

	private FormComponent<String> createDescriptionField() {
		FormComponent<String> fc = new RichTextArea("description");
		fc.add(StringValidator.minimumLength(3));
		return fc;
	}

	private FormComponent<String> createHitsField() {
		return new RequiredTextField<String>("hits");
	}

	private FormComponent<String> createNumberOfVotesField() {
		return new RequiredTextField<String>("numberOfVotes");
	}

	private FormComponent<String> createSumOfRatingField() {
		return new RequiredTextField<String>("sumOfRating");
	}

	private RightGridPanel createViewRightPanel() {
		ListModel<RightEntity> rightsListModel = new ListModel<RightEntity>(bookmark.getAllRights());
		return new RightGridPanel("viewRights", "bookmark.view", rightsListModel);
	}

	private RightGridPanel createVisitRightPanel() {
		ListModel<RightEntity> rightsListModel = new ListModel<RightEntity>(bookmark.getAllRights());
		return new RightGridPanel("visitRights", "bookmark.visit", rightsListModel);
	}

	private RightGridPanel createVoteRightPanel() {
		ListModel<RightEntity> rightsListModel = new ListModel<RightEntity>(bookmark.getAllRights());
		return new RightGridPanel("voteRights", "bookmark.vote", rightsListModel);
	}

	private TagField<BookmarkTagEntity> createTagField() {
		IModel<List<BookmarkTagEntity>> listModel = new PropertyModel<List<BookmarkTagEntity>>(bookmark, "tags");
		return new TagField<BookmarkTagEntity>("tags", listModel, bookmarkTagService);
	}

	private Form<BookmarkEntity> newBookmarkEditForm() {
		return new Form<BookmarkEntity>("form", new CompoundPropertyModel<BookmarkEntity>(bookmark)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				BookmarkEditPage.this.setVisible(false);
				BookmarkEntity bookmark = getModelObject();
				bookmark.setBroken(Boolean.FALSE);
				bookmark.setSource(Source.MANUAL);
				bookmarkService.save(bookmark);
				setRedirect(false);
				info(getString("msg.saved"));
				setResponsePage(new BookmarkPage(new PageParameters("id=" + bookmark.getId())));
			}
		};
	}
}
