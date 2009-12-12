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

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.panel.BaseSearchBoxPanel;
import org.devproof.portal.module.blog.query.BlogQuery;

/**
 * @author Carsten Hufe
 */
public class BlogSearchBoxPanel extends BaseSearchBoxPanel {

	private static final long serialVersionUID = 1L;
	private WebMarkupContainer titleContainer;

	public BlogSearchBoxPanel(String id, BlogQuery query, QueryDataProvider<?> dataProvider, TemplatePage parent,
			IPageable dataview, PageParameters params) {
		super(id, query, dataProvider, "page.BlogEditPage", parent, dataview, params);
		TextField<String> fc = new TextField<String>("allTextFields");
		getForm().add(fc);
		add(titleContainer = new WebMarkupContainer("title"));
	}

	@Override
	public void setTitleVisible(boolean visible) {
		titleContainer.setVisible(visible);
	}
}
