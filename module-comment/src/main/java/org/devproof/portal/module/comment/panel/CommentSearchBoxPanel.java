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
package org.devproof.portal.module.comment.panel;

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
import org.devproof.portal.module.comment.query.CommentQuery;

/**
 * @author Carsten Hufe
 */
public abstract class CommentSearchBoxPanel extends Panel {
	private static final long serialVersionUID = 1L;

	private IModel<CommentQuery> queryModel;

	public CommentSearchBoxPanel(String id, IModel<CommentQuery> queryModel) {
		super(id);
		this.queryModel = queryModel;
		add(createCommentSearchForm());
	}

	private Form<CommentQuery> createCommentSearchForm() {
		CompoundPropertyModel<CommentQuery> compoundModel = new CompoundPropertyModel<CommentQuery>(queryModel);
		Form<CommentQuery> form = new Form<CommentQuery>("searchForm", compoundModel);
		form.add(createSearchTextField());
		form.add(createAcceptedDropDown());
		form.add(createReviewedDropDown());
		form.add(createAutomaticBlockedDropDown());
		form.setOutputMarkupId(true);
		return form;
	}

	private Select createAutomaticBlockedDropDown() {
		Select automaticBlocked = new Select("automaticBlocked");
		automaticBlocked.add(new SelectOption<Boolean>("chooseAutomaticBlocked", new Model<Boolean>()));
		automaticBlocked.add(new SelectOption<Boolean>("automaticBlockedTrue", Model.of(Boolean.TRUE)));
		automaticBlocked.add(new SelectOption<Boolean>("automaticBlockedFalse", Model.of(Boolean.FALSE)));
		automaticBlocked.add(new AjaxRefresh("onchange"));
		return automaticBlocked;
	}

	private Select createReviewedDropDown() {
		Select reviewed = new Select("reviewed");
		reviewed.add(new SelectOption<Boolean>("chooseReviewed", new Model<Boolean>()));
		reviewed.add(new SelectOption<Boolean>("reviewedTrue", Model.of(Boolean.TRUE)));
		reviewed.add(new SelectOption<Boolean>("reviewedFalse", Model.of(Boolean.FALSE)));
		reviewed.add(new AjaxRefresh("onchange"));
		return reviewed;
	}

	private Select createAcceptedDropDown() {
		Select accepted = new Select("accepted");
		accepted.add(new SelectOption<Boolean>("chooseAccepted", new Model<Boolean>()));
		accepted.add(new SelectOption<Boolean>("acceptedTrue", Model.of(Boolean.TRUE)));
		accepted.add(new SelectOption<Boolean>("acceptedFalse", Model.of(Boolean.FALSE)));
		accepted.add(new AjaxRefresh("onchange"));
		return accepted;
	}

	private FormComponent<String> createSearchTextField() {
		FormComponent<String> fc = new TextField<String>("allTextFields");
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
			CommentSearchBoxPanel.this.onSubmit(target);
		}
	}
}
