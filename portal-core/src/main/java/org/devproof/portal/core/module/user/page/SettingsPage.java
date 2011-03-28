/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.user.page;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.devproof.portal.core.app.PortalApplication;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.module.common.component.ValidationDisplayBehaviour;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.user.UserConstants;
import org.devproof.portal.core.module.user.entity.User;
import org.devproof.portal.core.module.user.service.UrlCallback;
import org.devproof.portal.core.module.user.service.UserService;

/**
 * @author Carsten Hufe
 */
@ModulePage(mountPath = "/settings")
public class SettingsPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    @SpringBean(name = "userService")
    private UserService userService;
    @SpringBean(name = "configurationService")
    private ConfigurationService configurationService;
    private IModel<User> userModel;
    private IModel<String> currentEmailModel;
    private PasswordTextField currentPassword;
    private PasswordTextField newPassword1;
    private PasswordTextField newPassword2;

    public SettingsPage(PageParameters params) {
        super(params);
        userModel = createUserModel();
        currentEmailModel = createCurrentEmailModel();
        add(createSettingsForm());
    }

    private Form<User> createSettingsForm() {
        Form<User> form = new Form<User>("form", new CompoundPropertyModel<User>(userModel));
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
                User user = userModel.getObject();
                if (StringUtils.isNotEmpty(newPassword1.getValue())) {
                    user.setPlainPassword(newPassword1.getValue());
                }
                userService.save(user);
                if (isReconfirmationRequired()) {
                    userService.resendConfirmationCode(user, createConfirmationUrlCallback());
                    setResponsePage(MessagePage.getMessagePageWithLogout(getString("reconfirm.email")));
                } else {
                    info(SettingsPage.this.getString("saved"));
                }
            }

            private boolean isReconfirmationRequired() {
                User user = userModel.getObject();
                String currentEmail = currentEmailModel.getObject();
                return !currentEmail.equals(user.getEmail()) && configurationService.findAsBoolean(UserConstants.CONF_EMAIL_VALIDATION);
            }

            private UrlCallback createConfirmationUrlCallback() {
                return new UrlCallback() {
                    @Override
                    public String getUrl(String generatedCode) {
                        User user = userModel.getObject();
                        String requestUrl = getRequestURL();
                        PageParameters param = new PageParameters();
                        param.add(RegisterPage.PARAM_USER, user.getUsername());
                        param.add(RegisterPage.PARAM_KEY, user.getConfirmationCode());
                        StringBuffer url = new StringBuffer(StringUtils.substringBeforeLast(requestUrl, "/")).append("/");
                        url.append(urlFor(RegisterPage.class, param));
                        return url.toString();
                    }
                };
            }
        };
    }

    private EqualPasswordInputValidator createEqualPasswordValidator() {
        return new EqualPasswordInputValidator(newPassword1, newPassword2);
    }

    private PasswordTextField createPasswordField1() {
        newPassword1 = new PasswordTextField("newPassword1", new Model<String>());
        newPassword1.setRequired(false);
        newPassword1.add(new ValidationDisplayBehaviour());
        return newPassword1;
    }

    private PasswordTextField createPasswordField2() {
        newPassword2 = new PasswordTextField("newPassword2", new Model<String>());
        newPassword2.setRequired(false);
        newPassword2.add(new ValidationDisplayBehaviour());
        return newPassword2;
    }

    private PasswordTextField createCurrentPasswordField() {
        currentPassword = new PasswordTextField("currentPassword", new Model<String>());
        currentPassword.add(createCurrentPasswordValidator());
        currentPassword.setRequired(false);
        currentPassword.add(new ValidationDisplayBehaviour());
        return currentPassword;
    }

    private AbstractValidator<String> createCurrentPasswordValidator() {
        return new AbstractValidator<String>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onValidate(IValidatable<String> ivalidatable) {
                User user = userModel.getObject();
                if (StringUtils.isNotEmpty(ivalidatable.getValue()) && !user.equalPassword(ivalidatable.getValue())) {
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
                return new FormComponent[]{currentPassword, newPassword1};
            }

            @Override
            public void validate(Form<?> form) {
                if (StringUtils.isNotEmpty(newPassword1.getValue()) && StringUtils.isEmpty(currentPassword.getValue())) {
                    error(currentPassword, "currentPassword.required");
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
        fc.add(new ValidationDisplayBehaviour());
        return fc;
    }

    private DateTextField createBirthdayField() {
        String dateFormat = configurationService.findAsString("input_date_format");
        DateTextField dateTextField = new DateTextField("birthday", dateFormat);
        dateTextField.add(new DatePicker());
        dateTextField.setRequired(configurationService.findAsBoolean(UserConstants.CONF_REGISTRATION_REQUIRED_BIRTHDAY));
        dateTextField.add(new ValidationDisplayBehaviour());
        return dateTextField;
    }

    private FormComponent<String> createLastnameField() {
        FormComponent<String> fc = new TextField<String>("lastname");
        fc.add(StringValidator.maximumLength(100));
        fc.setRequired(configurationService.findAsBoolean(UserConstants.CONF_REGISTRATION_REQUIRED_NAME));
        fc.add(new ValidationDisplayBehaviour());
        return fc;
    }

    private FormComponent<String> createFirstnameField() {
        FormComponent<String> fc = new TextField<String>("firstname");
        fc.add(StringValidator.maximumLength(100));
        fc.setRequired(configurationService.findAsBoolean(UserConstants.CONF_REGISTRATION_REQUIRED_NAME));
        fc.add(new ValidationDisplayBehaviour());
        return fc;
    }

    private FormComponent<String> createUsernameField() {
        FormComponent<String> fc = new RequiredTextField<String>("username");
        fc.setEnabled(false);
        return fc;
    }

    private IModel<String> createCurrentEmailModel() {
        // this model is ok, want a copy
        return Model.of(userModel.getObject().getEmail());
    }

    private IModel<User> createUserModel() {
        return new LoadableDetachableModel<User>() {
            private static final long serialVersionUID = -3255916962719155935L;

            @Override
            protected User load() {
                PortalSession session = (PortalSession) getSession();
                if(!session.isSignedIn()) {
                    Class<? extends Page> accessDeniedPage = PortalApplication.get().getApplicationSettings().getAccessDeniedPage();
                    throw new RestartResponseException(accessDeniedPage);
                }
                return userService.findById(session.getUser().getId());
            }
        };
    }
}
