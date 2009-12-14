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
package org.devproof.portal.module.bookmark.panel;

import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOption;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.model.Model;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.panel.BaseSearchBoxListener;
import org.devproof.portal.core.module.common.panel.BaseSearchBoxPanel;
import org.devproof.portal.module.bookmark.query.BookmarkQuery;
import org.devproof.portal.module.deadlinkcheck.query.IBrokenQuery;

/**
 * @author Carsten Hufe
 */
public class BookmarkSearchBoxPanel extends BaseSearchBoxPanel {

	private static final long serialVersionUID = 1L;
	private WebMarkupContainer titleContainer;
	private PageParameters params;
	private BookmarkQuery query;

	public BookmarkSearchBoxPanel(String id, BookmarkQuery query, QueryDataProvider<?> dataProvider,
			TemplatePage parent, IPageable dataview, PageParameters params) {
		super(id, query, dataProvider, "bookmark.view", parent, dataview, params);
		this.params = params;
		this.query = query;
		addToForm(createSearchTextField());
		addToForm(createBrokenDropDown());
		add(createTitleContainer());
		addListener(createSearchBoxListener());
		setBrokenParamInQuery();
	}

	private void setBrokenParamInQuery() {
		if (params != null) {
			if (params.containsKey("broken")) {
				query.setBroken(params.getAsBoolean("broken"));
			}
		}
	}

	private BaseSearchBoxListener createSearchBoxListener() {
		return new BaseSearchBoxListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSearch() {
				IBrokenQuery brokenQuery = query;
				if (brokenQuery.getBroken() != null && BookmarkSearchBoxPanel.this.isAuthor()) {
					params.put("broken", brokenQuery.getBroken().toString());
				}
			}
		};
	}

	private WebMarkupContainer createTitleContainer() {
		titleContainer = new WebMarkupContainer("title");
		return titleContainer;
	}

	private TextField<String> createSearchTextField() {
		return new TextField<String>("allTextFields");
	}

	private Select createBrokenDropDown() {
		Select selectBroken = new Select("broken");
		selectBroken.add(new SelectOption<Boolean>("chooseBroken", new Model<Boolean>()));
		selectBroken.add(new SelectOption<Boolean>("brokenTrue", Model.of(Boolean.TRUE)));
		selectBroken.add(new SelectOption<Boolean>("brokenFalse", Model.of(Boolean.FALSE)));
		selectBroken.add(new SimpleAttributeModifier("onchange", "submit();"));
		selectBroken.setVisible(isAuthor());
		return selectBroken;
	}

	@Override
	public void setTitleVisible(boolean visible) {
		titleContainer.setVisible(visible);
	}
}
