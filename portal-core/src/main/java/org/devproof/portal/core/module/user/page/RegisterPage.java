package org.devproof.portal.core.module.user.page;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.captcha.CaptchaImageResource;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.user.UserConstants;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.service.UserService;

/**
 * @author Carsten Hufe
 */
public class RegisterPage extends TemplatePage {

	private static final long serialVersionUID = 1L;
	public static final String PARAM_USER = "user";
	public static final String PARAM_KEY = "confirm";

	@SpringBean(name = "userService")
	private UserService userService;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;

	public RegisterPage(final PageParameters params) {
		super(params);
		if (params.containsKey(PARAM_USER) && params.containsKey(PARAM_KEY)) {
			activateUser(params);
		}
		final UserEntity user = userService.newUserEntity();
		Form<UserEntity> form = new Form<UserEntity>("form", new CompoundPropertyModel<UserEntity>(user));
		form.setOutputMarkupId(true);
		this.add(form);

		FormComponent<String> fc;

		fc = new RequiredTextField<String>("username");
		fc.add(StringValidator.lengthBetween(3, 30));
		fc.add(new AbstractValidator<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onValidate(final IValidatable<String> ivalidatable) {
				if (userService.existsUsername(ivalidatable.getValue())) {
					this.error(ivalidatable);
				}
			}

			@Override
			protected String resourceKey() {
				return "existing.username";
			}
		});
		fc.add(new PatternValidator("[A-Za-z0-9\\.]*"));
		form.add(fc);

		fc = new TextField<String>("firstname");
		fc.add(StringValidator.maximumLength(100));
		fc.setRequired(configurationService.findAsBoolean(UserConstants.CONF_REGISTRATION_REQUIRED_NAME));
		form.add(fc);

		fc = new TextField<String>("lastname");
		fc.add(StringValidator.maximumLength(100));
		fc.setRequired(configurationService.findAsBoolean(UserConstants.CONF_REGISTRATION_REQUIRED_NAME));
		form.add(fc);

		String dateFormat = configurationService.findAsString("date_format");
		DateTextField dateTextField = new DateTextField("birthday", dateFormat);
		dateTextField.add(new DatePicker());
		dateTextField
				.setRequired(configurationService.findAsBoolean(UserConstants.CONF_REGISTRATION_REQUIRED_BIRTHDAY));
		form.add(dateTextField);

		fc = new RequiredTextField<String>("email");
		fc.add(EmailAddressValidator.getInstance());
		fc.add(StringValidator.maximumLength(100));
		form.add(fc);

		final PasswordTextField password1 = new PasswordTextField("password1", new Model<String>());
		password1.add(StringValidator.minimumLength(5));
		password1.setRequired(true);
		form.add(password1);
		final PasswordTextField password2 = new PasswordTextField("password2", new Model<String>());
		password2.add(StringValidator.minimumLength(5));
		password2.setRequired(true);
		form.add(password2);

		form.add(new EqualPasswordInputValidator(password1, password2));

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
						error(ivalidatable);
					}
				}

				@Override
				protected String resourceKey() {
					return "wrong.captchacode";
				}
			});
		}

		form.add(new Button("registerButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				String requestUrl = RegisterPage.this.getRequestURL();
				String confirmationCode = UUID.randomUUID().toString();
				PageParameters param = new PageParameters();
				param.add(PARAM_USER, user.getUsername());
				param.add(PARAM_KEY, confirmationCode);
				StringBuffer url = new StringBuffer(StringUtils.substringBeforeLast(requestUrl, "/")).append("/");
				url.append(getWebRequestCycle().urlFor(RegisterPage.class, param));

				UserEntity user = (UserEntity) getForm().getModelObject();
				String msg = "success";
				if (configurationService.findAsBoolean(UserConstants.CONF_EMAIL_VALIDATION)) {
					msg = "confirm.email";
				}
				userService.registerUser(user, password1.getValue(), url.toString(), confirmationCode);
				setResponsePage(MessagePage.getMessagePage(getString(msg)));
			}
		});
	}

	private void activateUser(final PageParameters params) {
		String username = params.getString(PARAM_USER);
		String key = params.getString(PARAM_KEY);
		if (userService.activateUser(username, key)) {
			setResponsePage(MessagePage.getMessagePage(this.getString("confirmed")));
		} else {
			setResponsePage(MessagePage.getMessagePage(this.getString("notconfirmed")));
		}
	}
}
