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
package org.devproof.portal.core.module.contact.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.extensions.markup.html.captcha.CaptchaImageResource;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.ClientProperties;
import org.apache.wicket.protocol.http.request.WebClientInfo;
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
import org.devproof.portal.core.module.contact.ContactConstants;
import org.devproof.portal.core.module.contact.bean.ContactBean;
import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.email.service.EmailService;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.user.UserConstants;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.service.UserService;

/**
 * @author Carsten Hufe
 */
public class ContactPage extends TemplatePage {

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "emailService")
	private EmailService emailService;
	@SpringBean(name = "userService")
	private UserService userService;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;

	public ContactPage(final PageParameters params) {
		super(params);
		this.add(CSSPackageResource.getHeaderContribution(ContactConstants.REF_CONTACT_CSS));
		String username = "§$$§";
		if (params != null && params.containsKey("0")) {
			username = params.getString("0");
		}
		UserEntity touser = userService.findUserByUsername(username);
		if (touser == null) {
			throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(this
					.getString("user.doesnotexist")));
		}
		if (!touser.getRole().getRights().contains(new RightEntity("contact.form.enable"))) {
			throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(this
					.getString("user.missing.right")));
		}
		if (!Boolean.TRUE.equals(touser.getEnableContactForm())) {
			throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(this
					.getString("user.contactform.disabled")));
		}

		PortalSession session = (PortalSession) getSession();
		final ContactBean contactBean = new ContactBean();
		contactBean.setTouser(username);
		if (session.isSignedIn()) {
			UserEntity user = session.getUser();
			if (user.getFirstname() != null && user.getLastname() != null) {
				contactBean.setFullname(user.getFirstname() + " " + user.getLastname());
			}
			if (user.getEmail() != null) {
				contactBean.setEmail(user.getEmail());
			}
		}
		Form<ContactBean> form = new Form<ContactBean>("form", new CompoundPropertyModel<ContactBean>(contactBean));
		form.setOutputMarkupId(true);
		this.add(form);

		FormComponent<String> fc;

		fc = new RequiredTextField<String>("touser");
		fc.setEnabled(false);
		form.add(fc);

		fc = new RequiredTextField<String>("fullname");
		fc.add(StringValidator.minimumLength(5));
		fc.add(StringValidator.maximumLength(100));
		form.add(fc);

		fc = new RequiredTextField<String>("email");
		fc.add(EmailAddressValidator.getInstance());
		fc.add(StringValidator.maximumLength(100));
		form.add(fc);

		fc = new TextArea<String>("content");
		fc.add(StringValidator.minimumLength(30));
		form.add(fc);

		Boolean enableCaptcha = configurationService.findAsBoolean(UserConstants.CONF_REGISTRATION_CAPTCHA);
		WebMarkupContainer trCaptcha1 = new WebMarkupContainer("trCaptcha1");
		WebMarkupContainer trCaptcha2 = new WebMarkupContainer("trCaptcha2");
		trCaptcha1.setVisible(enableCaptcha);
		trCaptcha2.setVisible(enableCaptcha);

		form.add(trCaptcha1);
		form.add(trCaptcha2);

		final CaptchaImageResource captchaImageResource = new CaptchaImageResource(PortalUtil.randomString(6, 8));
		trCaptcha1.add(new Image("captchacodeimage", captchaImageResource));

		fc = new TextField<String>("captchacode", Model.of(""));
		fc.setRequired(enableCaptcha);
		trCaptcha2.add(fc);

		if (enableCaptcha) {
			fc.add(new AbstractValidator<String>() {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onValidate(final IValidatable<String> ivalidatable) {
					if (!captchaImageResource.getChallengeId().equalsIgnoreCase(ivalidatable.getValue())) {
						captchaImageResource.invalidate();
						this.error(ivalidatable);
					}
				}

				@Override
				protected String resourceKey() {
					return "wrong.captchacode";
				}
			});
		}

		form.add(new Button("sendButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				// send notification
				UserEntity touser = userService.findUserByUsername(contactBean.getTouser());
				Integer templateId = configurationService.findAsInteger(ContactConstants.CONF_CONTACTFORM_EMAIL);
				EmailPlaceholderBean placeholder = PortalUtil.getEmailPlaceHolderByUser(touser);
				placeholder.setContactEmail(contactBean.getEmail());
				placeholder.setContactFullname(contactBean.getFullname());
				ClientProperties prop = ((WebClientInfo) ContactPage.this.getWebRequestCycle().getClientInfo())
						.getProperties();
				placeholder.setContactIp(prop.getRemoteAddress());
				placeholder.setContent(contactBean.getContent());

				emailService.sendEmail(templateId, placeholder);
				this.setResponsePage(MessagePage.getMessagePage(this.getString("mail.sent")));
			}
		});
	}
}
