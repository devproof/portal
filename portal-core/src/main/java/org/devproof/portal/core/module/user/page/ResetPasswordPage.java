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
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.user.entity.User;
import org.devproof.portal.core.module.user.service.UserService;

/**
 * @author Carsten Hufe
 */
@ModulePage(mountPath = "/resetPassword")
public class ResetPasswordPage extends TemplatePage {

    private static final long serialVersionUID = 1L;
    public static final String PARAM_USER = "user";
    public static final String PARAM_CONFIRMATION_CODE = "forgot";

    @SpringBean(name = "userService")
    private UserService userService;
    private PageParameters params;
    private IModel<User> userModel;
    private PasswordTextField password1;
    private PasswordTextField password2;

    public ResetPasswordPage(PageParameters params) {
        super(params);
        this.params = params;
        validateParameter();
        this.userModel = createUserModel();
        add(createResetPasswordForm());
    }

    private Form<User> createResetPasswordForm() {
        Form<User> form = new Form<User>("form", new CompoundPropertyModel<User>(userModel));
        form.add(createUsernameField());
        form.add(createPasswordField1());
        form.add(createPasswordField2());
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
                if (params.getString(PARAM_CONFIRMATION_CODE).equals(user.getForgotPasswordCode())) {
                    userService.saveNewPassword(user.getUsername(), password1.getValue());
                    setResponsePage(MessagePage.getMessagePage(getString("changed")));
                }
            }
        };
    }

    private EqualPasswordInputValidator createEqualPasswordValidator() {
        return new EqualPasswordInputValidator(password1, password2);
    }

    private PasswordTextField createPasswordField1() {
        password1 = createPasswordField("password1");
        return password1;
    }

    private PasswordTextField createPasswordField2() {
        password2 = createPasswordField("password2");
        return password2;
    }

    private PasswordTextField createPasswordField(String id) {
        PasswordTextField password = new PasswordTextField(id, new Model<String>());
        password.add(StringValidator.minimumLength(5));
        password.setRequired(true);
        return password;
    }

    private FormComponent<String> createUsernameField() {
        FormComponent<String> fc;
        fc = new RequiredTextField<String>("username");
        fc.setEnabled(false);
        return fc;
    }

    private IModel<User> createUserModel() {
        return new LoadableDetachableModel<User>() {
            private static final long serialVersionUID = 4622636378084141707L;

            @Override
            protected User load() {
                User user = userService.findUserByUsername(params.getString(PARAM_USER));
                if (user == null) {
                    throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(getString("user.notregistered")));
                } else if (isConfirmationCodeNotCorrect(user)) {
                    throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(getString("wrong.key")));
                }
                return user;
            }


            private boolean isConfirmationCodeNotCorrect(User user) {
                return StringUtils.isNotEmpty(user.getForgotPasswordCode()) && !params.getString(PARAM_CONFIRMATION_CODE).equals(user.getForgotPasswordCode());
            }
        };
    }

    private void validateParameter() {
        if (!params.containsKey(PARAM_USER) || !params.containsKey(PARAM_CONFIRMATION_CODE)) {
            throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(getString("missing.params")));
        }
    }
}
