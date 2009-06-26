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
package org.devproof.portal.core.module.user.page;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.email.service.EmailService;
import org.devproof.portal.core.module.user.UserConstants;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.service.UserService;

/**
 * @author Carsten Hufe
 */
public class SettingsPage extends TemplatePage {

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "emailService")
	private transient EmailService emailService;
	@SpringBean(name = "userService")
	private transient UserService userService;
	@SpringBean(name = "configurationService")
	private transient ConfigurationService configurationService;

	public SettingsPage(final PageParameters params) {
		super(params);
		PortalSession session = (PortalSession) getSession();
		final UserEntity user = this.userService.findById(session.getUser().getId());
		final String oldEmail = user.getEmail();
		final Form<UserEntity> form = new Form<UserEntity>("form");
		form.setOutputMarkupId(true);
		this.add(form);
		form.setModel(new CompoundPropertyModel<UserEntity>(user));
		FormComponent<String> fc;

		fc = new RequiredTextField<String>("username");
		fc.setEnabled(false);
		form.add(fc);

		fc = new TextField<String>("firstname");
		fc.add(StringValidator.maximumLength(100));
		fc.setRequired(this.configurationService.findAsBoolean(UserConstants.CONF_REGISTRATION_REQUIRED_NAME));
		form.add(fc);

		fc = new TextField<String>("lastname");
		fc.add(StringValidator.maximumLength(100));
		fc.setRequired(this.configurationService.findAsBoolean(UserConstants.CONF_REGISTRATION_REQUIRED_NAME));
		form.add(fc);

		String dateFormat = this.configurationService.findAsString("date_format");
		DateTextField dateTextField = new DateTextField("birthday", dateFormat);
		dateTextField.add(new DatePicker());
		dateTextField.setRequired(this.configurationService.findAsBoolean(UserConstants.CONF_REGISTRATION_REQUIRED_BIRTHDAY));
		form.add(dateTextField);

		fc = new RequiredTextField<String>("email");
		fc.add(EmailAddressValidator.getInstance());
		fc.add(StringValidator.maximumLength(100));
		form.add(fc);

		CheckBox cb = new CheckBox("enableContactForm");
		form.add(cb);

		final PasswordTextField oldPassword = new PasswordTextField("oldPassword", new Model<String>());
		oldPassword.setRequired(false);
		oldPassword.add(new AbstractValidator<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onValidate(final IValidatable<String> ivalidatable) {
				if (StringUtils.isNotEmpty(ivalidatable.getValue()) && !user.getPasswordMD5().equals(PortalUtil.generateMd5(ivalidatable.getValue()))) {
					this.error(ivalidatable, "wrong.oldPassword");
				}
			}
		});
		form.add(oldPassword);
		final PasswordTextField password1 = new PasswordTextField("password1", new Model<String>());
		password1.setRequired(false);
		form.add(password1);
		final PasswordTextField password2 = new PasswordTextField("password2", new Model<String>());
		password2.setRequired(false);
		form.add(password2);
		form.add(new AbstractFormValidator() {
			private static final long serialVersionUID = 1L;

			@Override
			public FormComponent<?>[] getDependentFormComponents() {
				return new FormComponent[] { oldPassword, password1 };
			}

			@Override
			public void validate(final Form<?> form) {
				if (StringUtils.isNotEmpty(password1.getValue()) && StringUtils.isEmpty(oldPassword.getValue())) {
					this.error(oldPassword, "oldPassword.required");
				}
			}

		});

		form.add(new EqualPasswordInputValidator(password1, password2));

		form.add(new Button("saveButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				UserEntity user = form.getModelObject();
				if (StringUtils.isNotEmpty(password1.getValue())) {
					user.setPasswordMD5(PortalUtil.generateMd5(password1.getValue()));
				}

				user.setChangedAt(PortalUtil.now());
				info(SettingsPage.this.getString("saved"));
				if (!oldEmail.equals(user.getEmail()) && SettingsPage.this.configurationService.findAsBoolean(UserConstants.CONF_EMAIL_VALIDATION)) {
					user.setConfirmed(false);
					user.setConfirmationCode(UUID.randomUUID().toString());
					user.setConfirmationRequestedAt(PortalUtil.now());

					EmailPlaceholderBean placeholder = PortalUtil.getEmailPlaceHolderByUser(user);

					String requestUrl = SettingsPage.this.getRequestURL();
					// url.append("/").append(PARAM_KEY).append("/").append(user.getConfirmationCode());
					PageParameters param = new PageParameters();
					param.add(RegisterPage.PARAM_USER, user.getUsername());
					param.add(RegisterPage.PARAM_KEY, user.getConfirmationCode());
					StringBuffer url = new StringBuffer(StringUtils.substringBeforeLast(requestUrl, "/")).append("/");
					url.append(SettingsPage.this.getWebRequestCycle().urlFor(RegisterPage.class, param));
					placeholder.setConfirmationLink(url.toString());

					SettingsPage.this.emailService.sendEmail(SettingsPage.this.configurationService.findAsInteger(UserConstants.CONF_RECONFIRMATION_EMAIL), placeholder);
					this.setResponsePage(MessagePage.getMessagePageWithLogout(this.getString("reconfirm.email")));
				}
				SettingsPage.this.userService.save(user);
			}
		});
	}
}
