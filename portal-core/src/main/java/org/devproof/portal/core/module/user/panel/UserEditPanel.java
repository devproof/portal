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
package org.devproof.portal.core.module.user.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.role.service.RoleService;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.service.UserService;

/**
 * @author Carsten Hufe
 */
public abstract class UserEditPanel extends Panel {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "roleService")
	private RoleService roleService;
	@SpringBean(name = "userService")
	private UserService userService;

	public UserEditPanel(final String id, final UserEntity user, final boolean isCreate) {
		super(id);
		final FeedbackPanel feedback = new FeedbackPanel("feedbackPanel");
		feedback.setOutputMarkupId(true);
		add(feedback);

		Form<UserEntity> form = new Form<UserEntity>("form");
		form.setOutputMarkupId(true);
		add(form);
		form.setModel(new CompoundPropertyModel<UserEntity>(user));
		FormComponent<String> fc;

		fc = new RequiredTextField<String>("username");
		fc.add(StringValidator.lengthBetween(3, 30));
		fc.add(new AbstractValidator<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onValidate(final IValidatable<String> ivalidatable) {
				if (userService.existsUsername(ivalidatable.getValue()) && isCreate) {
					this.error(ivalidatable);
				}
			}

			@Override
			protected String resourceKey() {
				return "existing.username";
			}
		});
		fc.add(new PatternValidator("[A-Za-z0-9\\.]*"));
		form.add(fc);

		fc = new TextField<String>("firstname");
		fc.add(StringValidator.maximumLength(100));
		form.add(fc);

		fc = new TextField<String>("lastname");
		fc.add(StringValidator.maximumLength(100));
		form.add(fc);

		DateTextField dateTextField = new DateTextField("birthday");
		dateTextField.add(new DatePicker());
		form.add(dateTextField);

		fc = new RequiredTextField<String>("email");
		fc.add(EmailAddressValidator.getInstance());
		fc.add(StringValidator.maximumLength(100));
		form.add(fc);

		IChoiceRenderer<RoleEntity> renderer = new ChoiceRenderer<RoleEntity>("description", "id");
		final DropDownChoice<?> role = new DropDownChoice<RoleEntity>("role", new PropertyModel<RoleEntity>(user,
				"role"), roleService.findAll(), renderer);
		role.setRequired(true);
		form.add(role);

		final PasswordTextField password1 = new PasswordTextField("password1", new Model<String>());
		password1.setRequired(isCreate);
		form.add(password1);
		final PasswordTextField password2 = new PasswordTextField("password2", new Model<String>());
		password2.setRequired(isCreate);
		form.add(password2);

		form.add(new EqualPasswordInputValidator(password1, password2));
		form.add(new CheckBox("active"));
		form.add(new CheckBox("confirmed"));

		form.add(new AjaxButton("saveButton", form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
				UserEntity user = (UserEntity) form.getModelObject();
				if (password1.getValue() != null && !"".equals(password1.getValue().trim())) {
					user.setPasswordMD5(PortalUtil.generateMd5(password1.getValue()));
				}
				if (user.getRegistrationDate() == null) {
					user.setRegistrationDate(PortalUtil.now());
				}
				user.setChangedAt(PortalUtil.now());
				userService.save(user);
				UserEditPanel.this.onSave(target);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, final Form<?> form) {
				target.addComponent(feedback);
			}
		});
	}

	public abstract void onSave(AjaxRequestTarget target);
}
