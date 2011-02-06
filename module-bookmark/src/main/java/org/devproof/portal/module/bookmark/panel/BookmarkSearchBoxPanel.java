/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.module.bookmark.panel;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOption;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.devproof.portal.core.module.box.panel.BoxTitleVisibility;
import org.devproof.portal.core.module.common.query.SearchQuery;
import org.devproof.portal.module.bookmark.query.BookmarkQuery;

/**
 * @author Carsten Hufe
 */
public abstract class BookmarkSearchBoxPanel extends Panel implements BoxTitleVisibility {

    private static final long serialVersionUID = 1L;
    private WebMarkupContainer titleContainer;
    private IModel<BookmarkQuery> queryModel;

    public BookmarkSearchBoxPanel(String id, IModel<BookmarkQuery> queryModel) {
        super(id, queryModel);
        this.queryModel = queryModel;
        add(createTitleContainer());
        add(createSearchForm());
    }

    private Component createSearchForm() {
        Form<SearchQuery> form = newSearchForm();
        form.add(createSearchTextField());
        form.add(createBrokenDropDown());
        return form;
    }

    private Form<SearchQuery> newSearchForm() {
        CompoundPropertyModel<SearchQuery> formModel = new CompoundPropertyModel<SearchQuery>(queryModel);
        return new Form<SearchQuery>("searchForm", formModel);
    }

    private WebMarkupContainer createTitleContainer() {
        titleContainer = new WebMarkupContainer("title");
        return titleContainer;
    }

    private TextField<String> createSearchTextField() {
        return new TextField<String>("allTextFields");
    }

    private Select createBrokenDropDown() {
        Select selectBroken = newBrokenDropDown();
        selectBroken.add(new SelectOption<Boolean>("chooseBroken", new Model<Boolean>()));
        selectBroken.add(new SelectOption<Boolean>("brokenTrue", Model.of(Boolean.TRUE)));
        selectBroken.add(new SelectOption<Boolean>("brokenFalse", Model.of(Boolean.FALSE)));
        selectBroken.add(new SimpleAttributeModifier("onchange", "submit();"));
        return selectBroken;
    }

    private Select newBrokenDropDown() {
        return new Select("broken") {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isVisible() {
                return isAuthor();
            }
        };
    }

    @Override
    public void setTitleVisible(boolean visible) {
        titleContainer.setVisible(visible);
    }

    protected abstract boolean isAuthor();
}
