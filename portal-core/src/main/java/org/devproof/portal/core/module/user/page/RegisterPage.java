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
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.module.common.component.ValidationDisplayBehaviour;
import org.devproof.portal.core.module.common.factory.CommonMarkupContainerFactory;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.core.module.common.panel.captcha.CaptchaAjaxButton;
import org.devproof.portal.core.module.common.registry.SharedRegistry;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.user.UserConstants;
import org.devproof.portal.core.module.user.entity.User;
import org.devproof.portal.core.module.user.service.UrlCallback;
import org.devproof.portal.core.module.user.service.UserService;

/**
 * @author Carsten Hufe
 */
@ModulePage(mountPath = "/register")
public class RegisterPage extends TemplatePage {

    private static final long serialVersionUID = 1L;
    public static final String PARAM_USER = "user";
    public static final String PARAM_KEY = "confirm";

    @SpringBean(name = "userService")
    private UserService userService;
    @SpringBean(name = "configurationService")
    private ConfigurationService configurationService;
    @SpringBean(name = "sharedRegistry")
    private SharedRegistry sharedRegistry;
    private PageParameters params;
    private IModel<User> userModel;
    private PasswordTextField password1;
    private PasswordTextField password2;
    private BubblePanel bubblePanel;
    private Form<User> registerForm;

    public RegisterPage(PageParameters params) {
        super(params);
        this.params = params;
        this.userModel = createUserModel();
        activateUserIfParamsGiven();
        add(createBubblePanel());
        add(createRegisterForm());
    }

    private Component createBubblePanel() {
        bubblePanel = new BubblePanel("bubblePanel");
        return bubblePanel;
    }

    private Form<User> createRegisterForm() {
        registerForm = new Form<User>("form", new CompoundPropertyModel<User>(userModel));
        registerForm.add(createUsernameField());
        registerForm.add(createFirstnameField());
        registerForm.add(createLastnameField());
        registerForm.add(createBirthdayField());
        registerForm.add(createEmailField());
        registerForm.add(createPasswordField1());
        registerForm.add(createPasswordField2());
        registerForm.add(createEqualPasswordValidator());
        registerForm.add(createTermsOfUseCheckBox());
        registerForm.add(createTermsOfUseLink());
        registerForm.add(createRegisterButton());
        registerForm.setOutputMarkupId(true);
        return registerForm;
    }

    private Component createTermsOfUseCheckBox() {
        CheckBox checkBox = new CheckBox("termsOfUse", Model.of(Boolean.FALSE));
        checkBox.add(createTermsOfUseValidator());
        return checkBox;
    }

    private AbstractValidator<Boolean> createTermsOfUseValidator() {
        return new AbstractValidator<Boolean>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onValidate(IValidatable<Boolean> ivalidatable) {
                if (!ivalidatable.getValue()) {
                    error(ivalidatable);
                }
            }

            @Override
            protected String resourceKey() {
                return "termsOfUse.mustAccepted";
            }
        };
    }

    private Component createTermsOfUseLink() {
        CommonMarkupContainerFactory factory = sharedRegistry.getResource("termsOfUseLink");
        MarkupContainer termsOfUseLink;
        if (factory != null) {
            termsOfUseLink = factory.newInstance("termsOfUseLink");
        } else {
            termsOfUseLink = new WebMarkupContainer("termsOfUseLink");
        }
        return termsOfUseLink;
    }

    private CaptchaAjaxButton createRegisterButton() {
        return new CaptchaAjaxButton("registerButton", bubblePanel) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClickAndCaptchaValidated(AjaxRequestTarget target) {
                User user = userModel.getObject();
                String msg = "success";
                if (configurationService.findAsBoolean(UserConstants.CONF_EMAIL_VALIDATION)) {
                    msg = "confirm.email";
                }
                user.setPlainPassword(password1.getValue());
                userService.registerUser(user, createRegisterUrlCallback());
                setResponsePage(MessagePage.getMessagePage(getString(msg)));
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(registerForm);
                target.add(getFeedback());
            }

            private UrlCallback createRegisterUrlCallback() {
                return new UrlCallback() {
                    @Override
                    public String getUrl(String generatedCode) {
                        User user = userModel.getObject();
                        String requestUrl = getRequestURL();
                        PageParameters param = new PageParameters();
                        param.add(PARAM_USER, user.getUsername());
                        param.add(PARAM_KEY, generatedCode);
                        StringBuffer url = new StringBuffer(StringUtils.substringBeforeLast(requestUrl, "/")).append("/");
                        url.append(urlFor(RegisterPage.class, param));
                        return url.toString();
                    }
                };
            }
        };
    }

    private EqualPasswordInputValidator createEqualPasswordValidator() {
        return new EqualPasswordInputValidator(password1, password2);
    }

    private PasswordTextField createPasswordField1() {
        password1 = createPasswordField("password1");
        password1.add(new ValidationDisplayBehaviour());
        return password1;
    }

    private PasswordTextField createPasswordField2() {
        password2 = createPasswordField("password2");
        password2.add(new ValidationDisplayBehaviour());
        return password2;
    }

    private PasswordTextField createPasswordField(String id) {
        PasswordTextField password = new PasswordTextField(id, new Model<String>());
        password.add(StringValidator.minimumLength(5));
        password.setRequired(true);
        return password;
    }

    private DateTextField createBirthdayField() {
        String dateFormat = configurationService.findAsString("input_date_format");
        DateTextField dateTextField = new DateTextField("birthday", dateFormat);
        dateTextField.add(new DatePicker());
        dateTextField.setRequired(configurationService.findAsBoolean(UserConstants.CONF_REGISTRATION_REQUIRED_BIRTHDAY));
        dateTextField.add(new ValidationDisplayBehaviour());
        return dateTextField;
    }

    private FormComponent<String> createEmailField() {
        FormComponent<String> fc = new RequiredTextField<String>("email");
        fc.add(EmailAddressValidator.getInstance());
        fc.add(StringValidator.maximumLength(100));
        fc.add(new ValidationDisplayBehaviour());
        return fc;
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
        fc.add(new ValidationDisplayBehaviour());
        fc.add(StringValidator.maximumLength(100));
        fc.setRequired(configurationService.findAsBoolean(UserConstants.CONF_REGISTRATION_REQUIRED_NAME));
        return fc;
    }

    private FormComponent<String> createUsernameField() {
        FormComponent<String> fc = new RequiredTextField<String>("username");
        fc.add(StringValidator.lengthBetween(3, 30));
        fc.add(createExistingUsernameValidator());
        fc.add(new PatternValidator("[A-Za-z0-9\\.]*"));
        fc.add(new ValidationDisplayBehaviour());
        return fc;
    }

    private AbstractValidator<String> createExistingUsernameValidator() {
        return new AbstractValidator<String>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onValidate(IValidatable<String> ivalidatable) {
                if (userService.existsUsername(ivalidatable.getValue())) {
                    error(ivalidatable);
                }
            }

            @Override
            protected String resourceKey() {
                return "existing.username";
            }
        };
    }

    private IModel<User> createUserModel() {
        return Model.of(userService.newUserEntity());
    }

    private void activateUserIfParamsGiven() {
        if (!params.get(PARAM_USER).isEmpty() && !params.get(PARAM_KEY).isEmpty()) {
            activateUser();
        }
    }

    private void activateUser() {
        String username = params.get(PARAM_USER).toString();
        String confirmationCode = params.get(PARAM_KEY).toString();
        if (userService.activateUser(username, confirmationCode)) {
            setResponsePage(MessagePage.getMessagePage(getString("confirmed")));
        } else {
            setResponsePage(MessagePage.getErrorPage(getString("notconfirmed")));
        }
    }
}
