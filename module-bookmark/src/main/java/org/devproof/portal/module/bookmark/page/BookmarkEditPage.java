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
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.core.module.right.panel.RightGridPanel;
import org.devproof.portal.core.module.tag.component.TagField;
import org.devproof.portal.module.bookmark.entity.Bookmark;
import org.devproof.portal.module.bookmark.entity.Bookmark.Source;
import org.devproof.portal.module.bookmark.entity.BookmarkTag;
import org.devproof.portal.module.bookmark.service.BookmarkService;
import org.devproof.portal.module.bookmark.service.BookmarkTagService;

import java.util.List;

/**
 * @author Carsten Hufe
 */
public class BookmarkEditPage extends BookmarkBasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "bookmarkService")
    private BookmarkService bookmarkService;
    @SpringBean(name = "bookmarkTagService")
    private BookmarkTagService bookmarkTagService;
    private IModel<Bookmark> bookmarkModel;


    public BookmarkEditPage(IModel<Bookmark> bookmarkModel) {
        super(new PageParameters());
        this.bookmarkModel = bookmarkModel;
        add(createBookmarkEditForm());
    }

    private Form<Bookmark> createBookmarkEditForm() {
        Form<Bookmark> form = newBookmarkEditForm();
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
        IModel<List<Right>> rightsListModel = new PropertyModel<List<Right>>(bookmarkModel, "allRights");
        return new RightGridPanel("viewRights", "bookmark.view", rightsListModel);
    }

    private RightGridPanel createVisitRightPanel() {
        IModel<List<Right>> rightsListModel = new PropertyModel<List<Right>>(bookmarkModel, "allRights");
        return new RightGridPanel("visitRights", "bookmark.visit", rightsListModel);
    }

    private RightGridPanel createVoteRightPanel() {
        IModel<List<Right>> rightsListModel = new PropertyModel<List<Right>>(bookmarkModel, "allRights");
        return new RightGridPanel("voteRights", "bookmark.vote", rightsListModel);
    }

    private TagField<BookmarkTag> createTagField() {
        IModel<List<BookmarkTag>> listModel = new PropertyModel<List<BookmarkTag>>(bookmarkModel, "tags");
        return new TagField<BookmarkTag>("tags", listModel, bookmarkTagService);
    }

    private Form<Bookmark> newBookmarkEditForm() {
        IModel<Bookmark> compoundModel = new CompoundPropertyModel<Bookmark>(bookmarkModel);
        return new Form<Bookmark>("form", compoundModel) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                BookmarkEditPage.this.setVisible(false);
                Bookmark bookmark = getModelObject();
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
