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
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.module.common.component.ValidationDisplayBehaviour;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.core.module.common.panel.captcha.CaptchaAjaxButton;
import org.devproof.portal.core.module.user.service.UrlCallback;
import org.devproof.portal.core.module.user.service.UserService;

import java.io.Serializable;

/**
 * @author Carsten Hufe
 */
@ModulePage(mountPath = "/forgotPassword")
public class ForgotPasswordPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    @SpringBean(name = "userService")
    private UserService userService;
    private BubblePanel bubblePanel;
    private String emailOrUser = "";
    private Form<Void> form;

    public ForgotPasswordPage(PageParameters params) {
        super(params);
        add(createBubblePanel());
        add(createForgotPasswordForm());
    }

    private Component createBubblePanel() {
        bubblePanel = new BubblePanel("bubblePanel");
        return bubblePanel;
    }

    private Form<Void> createForgotPasswordForm() {
        form = new Form<Void>("form");
        form.add(createEmailOrUsernameField());
        form.add(createRequestButton());
        form.setOutputMarkupId(true);
        return form;
    }

    private Component createRequestButton() {
        return new CaptchaAjaxButton("requestButton", bubblePanel) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClickAndCaptchaValidated(AjaxRequestTarget target) {
                userService.sendForgotPasswordCode(emailOrUser, createForgotPasswordUrlCallback());
                setResponsePage(MessagePage.getMessagePage(getString("email.sent")));
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(getFeedback());
                target.addComponent(form);
            }

            private UrlCallback createForgotPasswordUrlCallback() {
                return new UrlCallback() {
                    @Override
                    public String getUrl(String generatedCode) {
                        String requestUrl = getRequestURL();
                        PageParameters param = new PageParameters();
                        param.add(ResetPasswordPage.PARAM_USER, emailOrUser);
                        param.add(ResetPasswordPage.PARAM_CONFIRMATION_CODE, generatedCode);
                        StringBuffer url = new StringBuffer(StringUtils.substringBeforeLast(requestUrl, "/")).append("/");
                        url.append(ForgotPasswordPage.this.getWebRequestCycle().urlFor(ResetPasswordPage.class, param));
                        return url.toString();
                    }
                };
            }
        };
    }

    private TextField<String> createEmailOrUsernameField() {
        RequiredTextField<String> tf = new RequiredTextField<String>("emailoruser", new PropertyModel<String>(this, "emailOrUser"));
        tf.add(new ValidationDisplayBehaviour());
        return tf;
    }
}
