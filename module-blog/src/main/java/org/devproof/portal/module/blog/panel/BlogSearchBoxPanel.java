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
package org.devproof.portal.module.blog.panel;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.devproof.portal.core.module.box.panel.BoxTitleVisibility;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.panel.BaseSearchBoxPanel;
import org.devproof.portal.core.module.common.query.SearchQuery;
import org.devproof.portal.module.blog.query.BlogQuery;

/**
 * @author Carsten Hufe
 */
public class BlogSearchBoxPanel extends Panel implements BoxTitleVisibility {

	private static final long serialVersionUID = 1L;
	private WebMarkupContainer titleContainer;
    private IModel<BlogQuery> queryModel;

    public BlogSearchBoxPanel(String id, IModel<BlogQuery> queryModel) {
		super(id, queryModel);
        this.queryModel = queryModel;
		add(createTitleContainer());
        add(createSearchForm());
	}

	private Component createSearchForm() {
		Form<SearchQuery> form = newSearchForm();
		form.add(createSearchTextField());
		return form;
	}

	private Form<SearchQuery> newSearchForm() {
		CompoundPropertyModel<SearchQuery> formModel = new CompoundPropertyModel<SearchQuery>(queryModel);
		return new Form<SearchQuery>("searchForm", formModel);
	}

	private TextField<String> createSearchTextField() {
		return new TextField<String>("allTextFields");
	}

	private WebMarkupContainer createTitleContainer() {
		titleContainer = new WebMarkupContainer("title");
		return titleContainer;
	}

	@Override
	public void setTitleVisible(boolean visible) {
		titleContainer.setVisible(visible);
	}
}
