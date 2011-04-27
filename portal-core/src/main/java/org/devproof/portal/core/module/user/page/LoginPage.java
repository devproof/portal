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

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.value.ValueMap;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.module.common.component.ValidationDisplayBehaviour;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.user.exception.UserNotConfirmedException;

/**
 * @author Carsten Hufe
 */
@ModulePage(mountPath = "/login")
public class LoginPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    private String username = "";
    private String password = "";

    public LoginPage(PageParameters params) {
        super(params);
        add(createLoginForm());
    }

    private Form<ValueMap> createLoginForm() {
        Form<ValueMap> form = newLoginForm();
        form.add(createUsernameField());
        form.add(createPasswordField());
        form.add(createRegisterLink());
        form.add(createForgotPasswordLink());
        form.setOutputMarkupId(true);
        return form;
    }

    private PasswordTextField createPasswordField() {
        IModel<String> passwordModel = new PropertyModel<String>(this, "password");
        PasswordTextField tf = new PasswordTextField("password", passwordModel);
        tf.add(new ValidationDisplayBehaviour());
        return tf;
    }

    private RequiredTextField<String> createUsernameField() {
        IModel<String> usernameModel = new PropertyModel<String>(this, "username");
        RequiredTextField<String> tf = new RequiredTextField<String>("username", usernameModel);
        tf.add(new ValidationDisplayBehaviour());
        return tf;
    }

    private Form<ValueMap> newLoginForm() {
        return new Form<ValueMap>("loginForm") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                PortalSession session = (PortalSession) getSession();
                try {
                    String message = session.authenticate(username, password);
                    if (message == null) {
                        redirectToPortalHomePage();
                    } else {
                        error(getString(message));
                    }
                } catch (UserNotConfirmedException e) {
                    setResponsePage(new ReenterEmailPage(Model.of(username)));
                }
            }

            private void redirectToPortalHomePage() {
                Class<? extends Page> homePage = getApplication().getHomePage();
                info(getString("logged.in"));
                // TODO test it should be done automatically
//                setRedirect(false);
                setResponsePage(homePage);
            }
        };
    }

    private BookmarkablePageLink<Void> createForgotPasswordLink() {
        return new BookmarkablePageLink<Void>("forgotPasswordLink", ForgotPasswordPage.class);
    }

    private BookmarkablePageLink<Void> createRegisterLink() {
        return new BookmarkablePageLink<Void>("registerLink", RegisterPage.class);
    }
}
