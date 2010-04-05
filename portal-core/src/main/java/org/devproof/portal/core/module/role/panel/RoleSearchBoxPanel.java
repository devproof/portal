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
package org.devproof.portal.core.module.role.panel;

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
import org.devproof.portal.core.module.role.query.RoleQuery;

/**
 * @author Carsten Hufe
 */
public abstract class RoleSearchBoxPanel extends Panel {
	private static final long serialVersionUID = 1L;

	private IModel<RoleQuery> queryModel;

	public RoleSearchBoxPanel(String id, IModel<RoleQuery> queryModel) {
		super(id);
		this.queryModel = queryModel;
		add(createRoleSearchForm());
	}

	private Form<RoleQuery> createRoleSearchForm() {
		Form<RoleQuery> form = new Form<RoleQuery>("searchForm", new CompoundPropertyModel<RoleQuery>(queryModel));
		form.add(createSearchTextField());
		form.add(createActiveDropDown());
		form.setOutputMarkupId(true);
		return form;
	}

	private FormComponent<String> createSearchTextField() {
		FormComponent<String> fc = new TextField<String>("description");
		fc.add(new AjaxRefresh("onkeyup"));
		return fc;
	}

	private Select createActiveDropDown() {
		Select active = new Select("active");
		active.add(new SelectOption<Boolean>("chooseActive", new Model<Boolean>()));
		active.add(new SelectOption<Boolean>("activeTrue", Model.of(Boolean.TRUE)));
		active.add(new SelectOption<Boolean>("activeFalse", Model.of(Boolean.FALSE)));
		active.add(new AjaxRefresh("onchange"));
		return active;
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
			RoleSearchBoxPanel.this.onSubmit(target);
		}
	}
}
