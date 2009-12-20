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
	private Boolean captchaEnabled;
	private PageParameters params;
	private UserEntity user;
	private PasswordTextField password1;
	private PasswordTextField password2;
	private String captchaChallengeCode;
	private CaptchaImageResource captchaImageResource;

	public RegisterPage(PageParameters params) {
		super(params);
		this.params = params;
		activateUserIfParamsGiven();
		setCaptchaEnabled();
		setCaptchaChallengeCode();
		setCaptchaImageResource();
		setUser();
		add(createRegisterForm());
	}

	private Form<UserEntity> createRegisterForm() {
		Form<UserEntity> form = new Form<UserEntity>("form", new CompoundPropertyModel<UserEntity>(user));
		form.add(createUsernameField());
		form.add(createFirstnameField());
		form.add(createLastnameField());
		form.add(createBirthdayField());
		form.add(createEmailField());
		form.add(createPasswordField1());
		form.add(createPasswordField2());
		form.add(createCaptchaImageContainer());
		form.add(createCaptchaFieldContainer());
		form.add(createRegisterButton());
		form.add(createEqualPasswordValidator());
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

	private FormComponent<String> createCaptchaCodeField() {
		FormComponent<String> fc = new TextField<String>("captchacode", Model.of(""));
		if (captchaEnabled) {
			fc.add(createCaptchaValidator());
		}
		fc.setRequired(captchaEnabled);
		return fc;
	}

	private Image createCaptchaImage() {
		return new Image("captchacodeimage", captchaImageResource);
	}

	private void setCaptchaImageResource() {
		captchaImageResource = new CaptchaImageResource(captchaChallengeCode);
	}

	private void setCaptchaChallengeCode() {
		captchaChallengeCode = PortalUtil.randomString(6, 8);
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

	private void setCaptchaEnabled() {
		captchaEnabled = configurationService.findAsBoolean(UserConstants.CONF_REGISTRATION_CAPTCHA);
	}

	private Button createRegisterButton() {
		return new Button("registerButton") {
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

	private DateTextField createBirthdayField() {
		String dateFormat = configurationService.findAsString("date_format");
		DateTextField dateTextField = new DateTextField("birthday", dateFormat);
		dateTextField.add(new DatePicker());
		dateTextField
				.setRequired(configurationService.findAsBoolean(UserConstants.CONF_REGISTRATION_REQUIRED_BIRTHDAY));
		return dateTextField;
	}

	private FormComponent<String> createEmailField() {
		FormComponent<String> fc = new RequiredTextField<String>("email");
		fc.add(EmailAddressValidator.getInstance());
		fc.add(StringValidator.maximumLength(100));
		return fc;
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
		fc.add(StringValidator.lengthBetween(3, 30));
		fc.add(createExistingUsernameValidator());
		fc.add(new PatternValidator("[A-Za-z0-9\\.]*"));
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

	private void setUser() {
		user = userService.newUserEntity();
	}

	private void activateUserIfParamsGiven() {
		if (params.containsKey(PARAM_USER) && params.containsKey(PARAM_KEY)) {
			activateUser();
		}
	}

	private void activateUser() {
		String username = params.getString(PARAM_USER);
		String confirmationCode = params.getString(PARAM_KEY);
		if (userService.activateUser(username, confirmationCode)) {
			setResponsePage(MessagePage.getMessagePage(getString("confirmed")));
		} else {
			setResponsePage(MessagePage.getMessagePage(getString("notconfirmed")));
		}
	}
}
