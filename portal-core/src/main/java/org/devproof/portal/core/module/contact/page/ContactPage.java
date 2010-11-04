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
package org.devproof.portal.core.module.contact.page;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.ClientProperties;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.core.module.common.panel.captcha.CaptchaAjaxButton;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.contact.ContactConstants;
import org.devproof.portal.core.module.contact.bean.ContactBean;
import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.email.service.EmailService;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.service.UserService;

/**
 * @author Carsten Hufe
 */
@ModulePage(mountPath = "/contact", indexMountedPath = true)
public class ContactPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    @SpringBean(name = "emailService")
    private EmailService emailService;
    @SpringBean(name = "userService")
    private UserService userService;
    @SpringBean(name = "configurationService")
    private ConfigurationService configurationService;
    private PageParameters params;
    private IModel<UserEntity> toUserModel;
    private IModel<ContactBean> contactBeanModel;
    private BubblePanel bubblePanel;

    public ContactPage(PageParameters params) {
        super(params);
        this.params = params;
        this.toUserModel = createToUserModel();
        this.contactBeanModel = createContactBeanModel();
        add(createCSSHeaderContributor());
        add(createBubblePanel());
        add(createContactForm());
    }

    @Override
    protected void onBeforeRender() {
        validateToUser();
        super.onBeforeRender();
    }

    private Component createBubblePanel() {
        bubblePanel = new BubblePanel("bubblePanel");
        return bubblePanel;
    }

    private Form<ContactBean> createContactForm() {
        Form<ContactBean> form = new Form<ContactBean>("form", new CompoundPropertyModel<ContactBean>(contactBeanModel));
        form.add(createToUserField());
        form.add(createFullnameField());
        form.add(createEmailField());
        form.add(createContentField());
        form.add(createSendButton());
        form.setOutputMarkupId(true);
        return form;
    }

    private FormComponent<String> createContentField() {
        FormComponent<String> fc = new TextArea<String>("content");
        fc.add(StringValidator.minimumLength(30));
        fc.setRequired(true);
        return fc;
    }

    private FormComponent<String> createEmailField() {
        FormComponent<String> fc = new RequiredTextField<String>("email");
        fc.add(EmailAddressValidator.getInstance());
        fc.add(StringValidator.maximumLength(100));
        return fc;
    }

    private FormComponent<String> createFullnameField() {
        FormComponent<String> fc = new RequiredTextField<String>("fullname");
        fc.add(StringValidator.minimumLength(5));
        fc.add(StringValidator.maximumLength(100));
        return fc;
    }

    private FormComponent<String> createToUserField() {
        FormComponent<String> fc = new RequiredTextField<String>("touser");
        fc.setEnabled(false);
        return fc;
    }

    private IModel<ContactBean> createContactBeanModel() {
        PortalSession session = (PortalSession) getSession();
        ContactBean contactBean = new ContactBean();
        contactBean.setTouser(getToUsername());
        if (session.isSignedIn()) {
            UserEntity user = session.getUser();
            if (isFullnameGiven(user)) {
                contactBean.setFullname(user.getFirstname() + " " + user.getLastname());
            }
            if (isEmailGiven(user)) {
                contactBean.setEmail(user.getEmail());
            }
        }
        return Model.of(contactBean);
    }

    private boolean isEmailGiven(UserEntity user) {
        return user.getEmail() != null;
    }

    private boolean isFullnameGiven(UserEntity user) {
        return user.getFirstname() != null && user.getLastname() != null;
    }

    private void validateToUser() {
        UserEntity toUser = toUserModel.getObject();
        if (toUser == null) {
            throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(this.getString("user.doesnotexist")));
        }
        if (hasContactFormPermission(toUser)) {
            throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(this.getString("user.missing.right")));
        }
        if (isRecipientContactFormEnabled(toUser)) {
            throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(this.getString("user.contactform.disabled")));
        }
    }

    private IModel<UserEntity> createToUserModel() {
        return new LoadableDetachableModel<UserEntity>() {
            private static final long serialVersionUID = -1538236896675592045L;

            @Override
            protected UserEntity load() {
                return userService.findUserByUsername(getToUsername());
            }
        };
    }

    private boolean isRecipientContactFormEnabled(UserEntity touser) {
        return !Boolean.TRUE.equals(touser.getEnableContactForm());
    }

    private boolean hasContactFormPermission(UserEntity touser) {
        return !touser.getRole().getRights().contains(new Right("contact.form.enable"));
    }

    private HeaderContributor createCSSHeaderContributor() {
        return CSSPackageResource.getHeaderContribution(ContactConstants.REF_CONTACT_CSS);
    }

    private String getToUsername() {
        if (params != null && params.containsKey("0")) {
            return params.getString("0");
        }
        return "§$$§";
    }

    private Component createSendButton() {
        return new CaptchaAjaxButton("sendButton", bubblePanel) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClickAndCaptchaValidated(AjaxRequestTarget target) {
                // send notification
                Integer templateId = configurationService.findAsInteger(ContactConstants.CONF_CONTACTFORM_EMAIL);
                UserEntity toUser = toUserModel.getObject();
                EmailPlaceholderBean placeholder = createEmailPlaceholderBean(toUser);
                emailService.sendEmail(templateId, placeholder);
                setResponsePage(MessagePage.getMessagePage(getString("mail.sent")));
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(getFeedback());
            }

            private EmailPlaceholderBean createEmailPlaceholderBean(UserEntity touser) {
                EmailPlaceholderBean placeholder = PortalUtil.createEmailPlaceHolderByUser(touser);
                ContactBean contactBean = contactBeanModel.getObject();
                placeholder.setContactEmail(contactBean.getEmail());
                placeholder.setContactFullname(contactBean.getFullname());
                placeholder.setContactIp(getIpAddress());
                placeholder.setContent(contactBean.getContent());
                return placeholder;
            }

            private String getIpAddress() {
                ClientProperties prop = ((WebClientInfo) ContactPage.this.getWebRequestCycle().getClientInfo()).getProperties();
                return prop.getRemoteAddress();
            }
        };
    }
}
