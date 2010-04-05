/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.module.bookmark.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.component.richtext.FullRichTextArea;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.right.panel.RightGridPanel;
import org.devproof.portal.core.module.tag.component.TagField;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity.Source;
import org.devproof.portal.module.bookmark.entity.BookmarkTagEntity;
import org.devproof.portal.module.bookmark.service.BookmarkService;

import java.util.List;

/**
 * @author Carsten Hufe
 */
public class BookmarkEditPage extends BookmarkBasePage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "bookmarkService")
	private BookmarkService bookmarkService;
	@SpringBean(name = "bookmarkTagService")
	private TagService<BookmarkTagEntity> bookmarkTagService;
    private IModel<BookmarkEntity> bookmarkModel;


    public BookmarkEditPage(IModel<BookmarkEntity> bookmarkModel) {
		super(new PageParameters());
        this.bookmarkModel = bookmarkModel;
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
		return new RequiredTextField<String>("title");
	}

	private FormComponent<String> createDescriptionField() {
		return new FullRichTextArea("description");
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
		IModel<List<RightEntity>> rightsListModel = new PropertyModel<List<RightEntity>>(bookmarkModel, "allRights");
		return new RightGridPanel("viewRights", "bookmark.view", rightsListModel);
	}

	private RightGridPanel createVisitRightPanel() {
		IModel<List<RightEntity>> rightsListModel = new PropertyModel<List<RightEntity>>(bookmarkModel, "allRights");
		return new RightGridPanel("visitRights", "bookmark.visit", rightsListModel);
	}

	private RightGridPanel createVoteRightPanel() {
		IModel<List<RightEntity>> rightsListModel = new PropertyModel<List<RightEntity>>(bookmarkModel, "allRights");
		return new RightGridPanel("voteRights", "bookmark.vote", rightsListModel);
	}

	private TagField<BookmarkTagEntity> createTagField() {
		IModel<List<BookmarkTagEntity>> listModel = new PropertyModel<List<BookmarkTagEntity>>(bookmarkModel, "tags");
		return new TagField<BookmarkTagEntity>("tags", listModel, bookmarkTagService);
	}

	private Form<BookmarkEntity> newBookmarkEditForm() {
        IModel<BookmarkEntity> compoundModel = new CompoundPropertyModel<BookmarkEntity>(bookmarkModel);
        return new Form<BookmarkEntity>("form", compoundModel) {
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
