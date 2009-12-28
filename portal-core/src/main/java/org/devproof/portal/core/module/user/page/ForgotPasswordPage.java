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

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.captcha.CaptchaImageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.user.UserConstants;
import org.devproof.portal.core.module.user.service.UrlCallback;
import org.devproof.portal.core.module.user.service.UserService;

/**
 * @author Carsten Hufe
 */
public class ForgotPasswordPage extends TemplatePage {

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "userService")
	private UserService userService;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;
	private String captchaChallengeCode;
	private CaptchaImageResource captchaImageResource;
	private Boolean captchaEnabled;
	private TextField<String> emailOrUser;

	public ForgotPasswordPage(PageParameters params) {
		super(params);
		setCaptchaEnabled();
		setCaptchaChallengeCode();
		setCaptchaImageResource();
		add(createForgotPasswordForm());
	}

	private Form<Serializable> createForgotPasswordForm() {
		Form<Serializable> form = new Form<Serializable>("form");
		form.add(createEmailOrUsernameField());
		form.add(createCaptchaImageContainer());
		form.add(createCaptchaFieldContainer());
		form.add(createRequestButton());
		form.setOutputMarkupId(true);
		return form;
	}

	private WebMarkupContainer createCaptchaFieldContainer() {
		WebMarkupContainer trCaptcha2 = new WebMarkupContainer("trCaptcha2");
		trCaptcha2.add(createCaptchaCodeField());
		trCaptcha2.setVisible(captchaEnabled);
		return trCaptcha2;
	}

	private WebMarkupContainer createCaptchaImageContainer() {
		WebMarkupContainer trCaptcha1 = new WebMarkupContainer("trCaptcha1");
		trCaptcha1.add(createCaptchaImage());
		trCaptcha1.setVisible(captchaEnabled);
		return trCaptcha1;
	}

	private void setCaptchaImageResource() {
		captchaImageResource = new CaptchaImageResource(captchaChallengeCode);
	}

	private void setCaptchaChallengeCode() {
		captchaChallengeCode = PortalUtil.randomString(6, 8);
	}

	private Image createCaptchaImage() {
		return new Image("captchacodeimage", captchaImageResource);
	}

	private FormComponent<String> createCaptchaCodeField() {
		FormComponent<String> fc;
		fc = new TextField<String>("captchacode", Model.of(""));
		fc.setRequired(captchaEnabled);
		if (captchaEnabled) {
			fc.add(createCaptchaValidator());
		}
		return fc;
	}

	private AbstractValidator<String> createCaptchaValidator() {
		return new AbstractValidator<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onValidate(IValidatable<String> ivalidatable) {
				if (!captchaChallengeCode.equalsIgnoreCase(ivalidatable.getValue())) {
					captchaImageResource.invalidate();
					error(ivalidatable);
				}
			}

			@Override
			protected String resourceKey() {
				return "wrong.captchacode";
			}
		};
	}

	private Button createRequestButton() {
		return new Button("requestButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				userService.sendForgotPasswordCode(emailOrUser.getValue(), createForgotPasswordUrlCallback());
				setResponsePage(MessagePage.getMessagePage(getString("email.sent")));
			}

			private UrlCallback createForgotPasswordUrlCallback() {
				return new UrlCallback() {
					@Override
					public String getUrl(String generatedCode) {
						String requestUrl = getRequestURL();
						PageParameters param = new PageParameters();
						param.add(ResetPasswordPage.PARAM_USER, emailOrUser.getValue());
						param.add(ResetPasswordPage.PARAM_CONFIRMATION_CODE, generatedCode);
						StringBuffer url = new StringBuffer(StringUtils.substringBeforeLast(requestUrl, "/"))
								.append("/");
						url.append(ForgotPasswordPage.this.getWebRequestCycle().urlFor(ResetPasswordPage.class, param));
						return url.toString();
					}
				};
			}
		};
	}

	private TextField<String> createEmailOrUsernameField() {
		emailOrUser = new RequiredTextField<String>("emailoruser", Model.of(""));
		return emailOrUser;
	}

	private void setCaptchaEnabled() {
		captchaEnabled = configurationService.findAsBoolean(UserConstants.CONF_REGISTRATION_CAPTCHA);
	}
}
