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
package org.devproof.portal.module.download.panel;

import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOption;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.model.Model;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.panel.BaseSearchBoxListener;
import org.devproof.portal.core.module.common.panel.BaseSearchBoxPanel;
import org.devproof.portal.module.deadlinkcheck.query.IBrokenQuery;
import org.devproof.portal.module.download.query.DownloadQuery;

/**
 * @author Carsten Hufe
 */
public class DownloadSearchBoxPanel extends BaseSearchBoxPanel {

	private static final long serialVersionUID = 1L;

	public DownloadSearchBoxPanel(final String id, final DownloadQuery query, final QueryDataProvider<?> dataProvider,
			final TemplatePage parent, final IPageable dataview, final PageParameters params) {
		super(id, query, dataProvider, "page.DownloadEditPage", parent, dataview, params);
		TextField<String> fc = new TextField<String>("allTextFields");
		getForm().add(fc);

		Select selectBroken = new Select("broken");
		selectBroken.add(new SelectOption<Boolean>("chooseBroken", new Model<Boolean>()));
		selectBroken.add(new SelectOption<Boolean>("brokenTrue", Model.of(Boolean.TRUE)));
		selectBroken.add(new SelectOption<Boolean>("brokenFalse", Model.of(Boolean.FALSE)));
		selectBroken.add(new SimpleAttributeModifier("onchange", "submit();"));
		selectBroken.setVisible(isAuthor());
		getForm().add(selectBroken);
		addListener(new BaseSearchBoxListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSearch() {
				final IBrokenQuery brokenQuery = query;
				if (brokenQuery.getBroken() != null && DownloadSearchBoxPanel.this.isAuthor()) {
					params.put("broken", brokenQuery.getBroken().toString());
				}
			}
		});
		if (params != null) {
			if (params.containsKey("broken")) {
				query.setBroken(params.getAsBoolean("broken"));
			}
		}
	}
}
