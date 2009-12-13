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
package org.devproof.portal.core.module.user.page;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.service.UserService;

/**
 * @author Carsten Hufe
 */
public class ResetPasswordPage extends TemplatePage {

	private static final long serialVersionUID = 1L;
	public static final String PARAM_USER = "user";
	public static final String PARAM_KEY = "forgot";

	@SpringBean(name = "userService")
	private UserService userService;

	public ResetPasswordPage(final PageParameters params) {
		super(params);
		if (!params.containsKey(PARAM_USER) || !params.containsKey(PARAM_KEY)) {
			setResponsePage(MessagePage.getMessagePage(getString("missing.params")));
			return;
		}

		final UserEntity user = userService.findUserByUsername(params.getString(PARAM_USER));
		if (user == null) {
			setResponsePage(MessagePage.getMessagePage(getString("user.notregistered")));
			return;
		} else if (StringUtils.isNotEmpty(user.getForgotPasswordCode())
				&& !params.getString(PARAM_KEY).equals(user.getForgotPasswordCode())) {
			setResponsePage(MessagePage.getMessagePage(getString("wrong.key")));
			return;
		}

		Form<UserEntity> form = new Form<UserEntity>("form");
		form.setOutputMarkupId(true);
		add(form);

		form.setModel(new CompoundPropertyModel<UserEntity>(user));
		FormComponent<?> fc;

		fc = new RequiredTextField<String>("username");
		fc.setEnabled(false);
		form.add(fc);

		final PasswordTextField password1 = new PasswordTextField("password1", new Model<String>());
		password1.add(StringValidator.minimumLength(5));
		password1.setRequired(true);
		form.add(password1);
		PasswordTextField password2 = new PasswordTextField("password2", new Model<String>());
		password2.add(StringValidator.minimumLength(5));
		password2.setRequired(true);
		form.add(password2);

		form.add(new EqualPasswordInputValidator(password1, password2));

		form.add(new Button("saveButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if (params.getString(PARAM_KEY).equals(user.getForgotPasswordCode())) {
					userService.setNewPassword(user.getUsername(), password1.getValue());
					setResponsePage(MessagePage.getMessagePage(getString("changed")));
				}
			}
		});
	}
}
