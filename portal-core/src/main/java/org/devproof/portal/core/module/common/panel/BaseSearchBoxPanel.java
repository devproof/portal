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
package org.devproof.portal.core.module.common.panel;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.box.panel.BoxTitleVisibility;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.query.IQuery;

/**
 * Base panel for search boxes
 * 
 * Should be refactored ... very ugly.
 * 
 * Search engines must be able to index nice urls!
 * 
 * @author Carsten Hufe
 * 
 */
public abstract class BaseSearchBoxPanel extends Panel implements BoxTitleVisibility {

	private static final long serialVersionUID = 1L;
	private Form<IQuery> form;
	private List<BaseSearchBoxListener> listeners = new ArrayList<BaseSearchBoxListener>();
	private boolean isAuthor;
	private IQuery query;
	private QueryDataProvider<?> dataProvider;
	private String authorRightName;
	private TemplatePage parent;
	private IPageable dataview;
	private PageParameters params;

	public BaseSearchBoxPanel(String id, IQuery query, QueryDataProvider<?> dataProvider, String authorRightName,
			TemplatePage parent, IPageable dataview, PageParameters params) {
		super(id);
		this.query = query;
		this.dataProvider = dataProvider;
		this.authorRightName = authorRightName;
		this.parent = parent;
		this.dataview = dataview;
		this.params = params;

		setAuthorRight();
		add(createSearchForm());
		copyParameterToQuery();
	}

	private void copyParameterToQuery() {
		if (params != null && params.containsKey("search")) {
			query.setAllTextFields(params.getString("search"));
		}
		if (params != null && params.containsKey("id")) {
			query.setId(params.getAsInteger("id"));
		}
	}

	private void setAuthorRight() {
		PortalSession session = (PortalSession) getSession();
		isAuthor = session.hasRight(this.authorRightName);
	}

	private Form<IQuery> createSearchForm() {
		form = newForm();
		form.setOutputMarkupId(true);
		return form;
	}

	private Form<IQuery> newForm() {
		return new Form<IQuery>("searchForm", new CompoundPropertyModel<IQuery>(query)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				copyQueryToParameter();
				for (BaseSearchBoxListener listenener : listeners) {
					listenener.onSearch();
				}
				query.clearSelection();
				dataProvider.setQueryObject(query);
				parent.addOrReplace(createPagingPanel());
				parent.cleanTagSelection();
			}

			private void copyQueryToParameter() {
				params.clear();
				if (query.getAllTextFields() != null) {
					params.put("search", query.getAllTextFields());
				}
				params.remove("tag");
			}

			private BookmarkablePagingPanel createPagingPanel() {
				return new BookmarkablePagingPanel("paging", dataview, parent.getPageClass(), params);
			}

		};
	}

	public Form<IQuery> getForm() {
		return form;
	}

	public void addToForm(Component component) {
		form.add(component);
	}

	public boolean isAuthor() {
		return isAuthor;
	}

	public void addListener(BaseSearchBoxListener listener) {
		this.listeners.add(listener);
	}
}
