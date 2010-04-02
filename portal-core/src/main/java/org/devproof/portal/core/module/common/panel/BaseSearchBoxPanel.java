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

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.devproof.portal.core.module.box.panel.BoxTitleVisibility;
import org.devproof.portal.core.module.common.query.SearchQuery;

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
// TODO remove class
public abstract class BaseSearchBoxPanel<T extends SearchQuery> extends Panel implements BoxTitleVisibility {

	private static final long serialVersionUID = 1L;
	private Form<SearchQuery> form;
	private IModel<T> searchQueryModel;

	public BaseSearchBoxPanel(String id, IModel<T> searchQueryModel) {
		super(id);
		this.searchQueryModel = searchQueryModel;
		add(createSearchForm());
		// copyParameterToQuery();
	}

	// TODO wie vorbelegen mit den werten?

	// private void copyParameterToQuery() {
	// if (params != null && params.containsKey("search")) {
	// query.setAllTextFields(params.getString("search"));
	// }
	// if (params != null && params.containsKey("id")) {
	// query.setId(params.getAsInteger("id"));
	// }
	// }

	private Form<SearchQuery> createSearchForm() {
		form = newForm();
		form.setOutputMarkupId(true);
		return form;
	}

	private Form<SearchQuery> newForm() {
		return new Form<SearchQuery>("searchForm", new CompoundPropertyModel<SearchQuery>(searchQueryModel)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				// searchQueryModel.getObject().clearSelection();
				// parent.addOrReplace(createPagingPanel());
				// parent.cleanTagSelection();
			}
		};
	}

	public void addToForm(Component component) {
		form.add(component);
	}

	// /**
	// * Params for paging
	// *
	// * @return
	// */
	// public Map<String, String> getParameters() {
	// Map<String, String> params = new HashMap<String, String>();
	// SearchQuery query = searchQueryModel.getObject();
	// if (query.getId() != null) {
	// params.put("id", String.valueOf(query.getId()));
	// }
	// if (query.getAllTextFields() != null) {
	// params.put("search", String.valueOf(query.getAllTextFields()));
	// }
	// return params;
	// }
	//
	// public boolean isAuthor() {
	// return false;
	// }
	//
	// public void addListener(BaseSearchBoxListener listener) {
	// this.listeners.add(listener);
	// }
}
