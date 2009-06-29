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
package org.devproof.portal.core.module.common.panel;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.query.IQuery;

/**
 * Base panel for search boxes
 * 
 * @author Carsten Hufe
 * 
 */
public abstract class BaseSearchBoxPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private final StatelessForm<IQuery<?>> form;
	private List<BaseSearchBoxListener> listener = new ArrayList<BaseSearchBoxListener>();
	private final boolean isAuthor;

	public BaseSearchBoxPanel(final String id, final IQuery<?> query, final QueryDataProvider<?> dataProvider, final String authorRight, final TemplatePage parent, final IPageable dataview,
			final PageParameters params) {
		super(id);
		final PortalSession session = (PortalSession) getSession();
		this.isAuthor = session.hasRight(authorRight);
		this.form = new StatelessForm<IQuery<?>>("searchForm", new CompoundPropertyModel<IQuery<?>>(query)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				params.clear();
				if (query.getAllTextFields() != null) {
					params.put("search", query.getAllTextFields());
				}
				for (BaseSearchBoxListener l : BaseSearchBoxPanel.this.listener) {
					l.onSearch();
				}
				query.clearSelection();
				params.remove("tag");
				dataProvider.setQueryObject(query);
				parent.addOrReplace(new BookmarkablePagingPanel("paging", dataview, parent.getPageClass(), params));
				parent.cleanTagSelection();
			}

		};
		this.form.setOutputMarkupId(true);
		this.add(this.form);

		if (params != null && params.containsKey("search")) {
			query.setAllTextFields(params.getString("search"));
		}
		if (params != null && params.containsKey("id")) {
			query.setId(params.getAsInteger("id"));
		}
	}

	public StatelessForm<IQuery<?>> getForm() {
		return this.form;
	}

	public boolean isAuthor() {
		return this.isAuthor;
	}

	public void addListener(final BaseSearchBoxListener listener) {
		this.listener.add(listener);
	}
}
