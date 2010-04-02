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
package org.devproof.portal.core.module.user.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOption;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;
import org.devproof.portal.core.module.user.query.UserQuery;

/**
 * @author Carsten Hufe
 */
public abstract class UserSearchBoxPanel extends Panel {
	private static final long serialVersionUID = 1L;

	private IModel<UserQuery> queryModel;

	public UserSearchBoxPanel(String id, IModel<UserQuery> queryModel) {
		super(id);
		this.queryModel = queryModel;
		add(createUserSearchForm());

	}

	private Form<UserQuery> createUserSearchForm() {
		Form<UserQuery> form = new Form<UserQuery>("searchForm", new CompoundPropertyModel<UserQuery>(queryModel));
		form.add(createSearchTextField());
		form.add(createActiveDropDown());
		form.add(createConfirmedDropDown());
		form.setOutputMarkupId(true);
		return form;
	}

	private Select createConfirmedDropDown() {
		Select confirmed = new Select("confirmed");
		confirmed.add(new SelectOption<Boolean>("chooseConfirmed", new Model<Boolean>()));
		confirmed.add(new SelectOption<Boolean>("confirmedTrue", Model.of(Boolean.TRUE)));
		confirmed.add(new SelectOption<Boolean>("confirmedFalse", Model.of(Boolean.FALSE)));
		confirmed.add(new AjaxRefresh("onchange"));
		return confirmed;
	}

	private Select createActiveDropDown() {
		Select active = new Select("active");
		active.add(new SelectOption<Boolean>("chooseActive", new Model<Boolean>()));
		active.add(new SelectOption<Boolean>("activeTrue", Model.of(Boolean.TRUE)));
		active.add(new SelectOption<Boolean>("activeFalse", Model.of(Boolean.FALSE)));
		active.add(new AjaxRefresh("onchange"));
		return active;
	}

	private FormComponent<String> createSearchTextField() {
		FormComponent<String> fc = new TextField<String>("allnames");
		fc.add(new AjaxRefresh("onkeyup"));
		return fc;
	}

	protected abstract void onSubmit(AjaxRequestTarget target);

	private class AjaxRefresh extends AjaxFormSubmitBehavior {
		private static final long serialVersionUID = 1L;

		public AjaxRefresh(String event) {
			super(event);
			setThrottleDelay(Duration.ONE_SECOND);
		}

		@Override
		protected void onError(AjaxRequestTarget target) {
		}

		@Override
		protected void onSubmit(AjaxRequestTarget target) {
			UserSearchBoxPanel.this.onSubmit(target);
		}
	}
}
