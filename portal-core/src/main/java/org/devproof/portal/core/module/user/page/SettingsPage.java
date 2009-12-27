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
	private EmailService emailService;
	@SpringBean(name = "userService")
	private UserService userService;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;
	private UserEntity user;
	private String currentEmail;
	private PasswordTextField currentPassword;
	private PasswordTextField newPassword1;
	private PasswordTextField newPassword2;

	public SettingsPage(PageParameters params) {
		super(params);
		setUser();
		setCurrentEmail();
		add(createSettingsForm());
	}

	private Form<UserEntity> createSettingsForm() {
		Form<UserEntity> form = new Form<UserEntity>("form", new CompoundPropertyModel<UserEntity>(user));
		form.add(createUsernameField());
		form.add(createFirstnameField());
		form.add(createLastnameField());
		form.add(createBirthdayField());
		form.add(createEmailField());
		form.add(createEnableContactFormCheckBox());
		form.add(createCurrentPasswordField());
		form.add(createPasswordField1());
		form.add(createPasswordField2());
		form.add(createMissingCurrentPasswordValidator());
		form.add(createEqualPasswordValidator());
		form.add(createSaveButton());
		form.setOutputMarkupId(true);
		return form;
	}

	private Button createSaveButton() {
		return new Button("saveButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				if (StringUtils.isNotEmpty(newPassword1.getValue())) {
					user.setEncryptedPassword(PortalUtil.generateMd5(newPassword1.getValue()));
				}
				user.setChangedAt(PortalUtil.now());
				info(SettingsPage.this.getString("saved"));
				if (!currentEmail.equals(user.getEmail())
						&& configurationService.findAsBoolean(UserConstants.CONF_EMAIL_VALIDATION)) {
					user.setConfirmed(false);
					user.setConfirmationCode(UUID.randomUUID().toString());
					user.setConfirmationRequestedAt(PortalUtil.now());

					EmailPlaceholderBean placeholder = PortalUtil.createEmailPlaceHolderByUser(user);

					String requestUrl = getRequestURL();
					// url.append("/").append(PARAM_KEY).append("/").append(user.getConfirmationCode());
					PageParameters param = new PageParameters();
					param.add(RegisterPage.PARAM_USER, user.getUsername());
					param.add(RegisterPage.PARAM_KEY, user.getConfirmationCode());
					StringBuffer url = new StringBuffer(StringUtils.substringBeforeLast(requestUrl, "/")).append("/");
					url.append(SettingsPage.this.getWebRequestCycle().urlFor(RegisterPage.class, param));
					placeholder.setConfirmationLink(url.toString());

					emailService.sendEmail(configurationService.findAsInteger(UserConstants.CONF_RECONFIRMATION_EMAIL),
							placeholder);
					setResponsePage(MessagePage.getMessagePageWithLogout(getString("reconfirm.email")));
				}
				userService.save(user);
			}
		};
	}

	private EqualPasswordInputValidator createEqualPasswordValidator() {
		return new EqualPasswordInputValidator(newPassword1, newPassword2);
	}

	private PasswordTextField createPasswordField1() {
		newPassword1 = new PasswordTextField("newPassword1", new Model<String>());
		newPassword1.setRequired(false);
		return newPassword1;
	}

	private PasswordTextField createPasswordField2() {
		newPassword2 = new PasswordTextField("newPassword2", new Model<String>());
		newPassword2.setRequired(false);
		return newPassword2;
	}

	private PasswordTextField createCurrentPasswordField() {
		currentPassword = new PasswordTextField("currentPassword", new Model<String>());
		currentPassword.add(createCurrentPasswordValidator());
		currentPassword.setRequired(false);
		return currentPassword;
	}

	private AbstractValidator<String> createCurrentPasswordValidator() {
		return new AbstractValidator<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onValidate(IValidatable<String> ivalidatable) {
				if (StringUtils.isNotEmpty(ivalidatable.getValue())
						&& !user.getEncryptedPassword().equals(PortalUtil.generateMd5(ivalidatable.getValue()))) {
					error(ivalidatable, "wrong.currentPassword");
				}
			}
		};
	}

	private AbstractFormValidator createMissingCurrentPasswordValidator() {
		return new AbstractFormValidator() {
			private static final long serialVersionUID = 1L;

			@Override
			public FormComponent<?>[] getDependentFormComponents() {
				return new FormComponent[] { currentPassword, newPassword1 };
			}

			@Override
			public void validate(Form<?> form) {
				if (StringUtils.isNotEmpty(newPassword1.getValue()) && StringUtils.isEmpty(currentPassword.getValue())) {
					error(currentPassword, "oldPassword.required");
				}
			}
		};
	}

	private CheckBox createEnableContactFormCheckBox() {
		return new CheckBox("enableContactForm");
	}

	private FormComponent<String> createEmailField() {
		FormComponent<String> fc = new RequiredTextField<String>("email");
		fc.add(EmailAddressValidator.getInstance());
		fc.add(StringValidator.maximumLength(100));
		return fc;
	}

	private DateTextField createBirthdayField() {
		String dateFormat = configurationService.findAsString("date_format");
		DateTextField dateTextField = new DateTextField("birthday", dateFormat);
		dateTextField.add(new DatePicker());
		dateTextField
				.setRequired(configurationService.findAsBoolean(UserConstants.CONF_REGISTRATION_REQUIRED_BIRTHDAY));
		return dateTextField;
	}

	private FormComponent<String> createLastnameField() {
		FormComponent<String> fc = new TextField<String>("lastname");
		fc.add(StringValidator.maximumLength(100));
		fc.setRequired(configurationService.findAsBoolean(UserConstants.CONF_REGISTRATION_REQUIRED_NAME));
		return fc;
	}

	private FormComponent<String> createFirstnameField() {
		FormComponent<String> fc = new TextField<String>("firstname");
		fc.add(StringValidator.maximumLength(100));
		fc.setRequired(configurationService.findAsBoolean(UserConstants.CONF_REGISTRATION_REQUIRED_NAME));
		return fc;
	}

	private FormComponent<String> createUsernameField() {
		FormComponent<String> fc = new RequiredTextField<String>("username");
		fc.setEnabled(false);
		return fc;
	}

	private void setCurrentEmail() {
		currentEmail = user.getEmail();
	}

	private void setUser() {
		PortalSession session = (PortalSession) getSession();
		user = userService.findById(session.getUser().getId());
	}
}
