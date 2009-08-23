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
package org.devproof.portal.core.module.user.page;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.email.service.EmailService;
import org.devproof.portal.core.module.user.UserConstants;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.service.UserService;

/**
 * @author Carsten Hufe
 */
public class ReenterEmailPage extends TemplatePage {

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "emailService")
	private EmailService emailService;
	@SpringBean(name = "userService")
	private UserService userService;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;

	public ReenterEmailPage(final String username) {
		super(new PageParameters("username=" + username));
		UserEntity user = userService.findUserByUsername(username);
		Form<UserEntity> form = new Form<UserEntity>("form", new CompoundPropertyModel<UserEntity>(user));
		form.setOutputMarkupId(true);
		add(form);

		final RequiredTextField<?> email = new RequiredTextField<String>("email");
		form.add(email);

		form.add(new Button("requestButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				UserEntity user = (UserEntity) getForm().getModelObject();
				user.setChangedAt(PortalUtil.now());
				user.setConfirmed(false);
				user.setConfirmationCode(UUID.randomUUID().toString());
				user.setConfirmationRequestedAt(PortalUtil.now());

				EmailPlaceholderBean placeholder = PortalUtil.getEmailPlaceHolderByUser(user);

				String requestUrl = getRequestURL();
				PageParameters param = new PageParameters();
				param.add(RegisterPage.PARAM_USER, user.getUsername());
				param.add(RegisterPage.PARAM_KEY, user.getConfirmationCode());
				StringBuffer url = new StringBuffer(StringUtils.substringBeforeLast(requestUrl, "/")).append("/");
				url.append(ReenterEmailPage.this.getWebRequestCycle().urlFor(RegisterPage.class, param));
				placeholder.setConfirmationLink(url.toString());

				emailService.sendEmail(configurationService.findAsInteger(UserConstants.CONF_RECONFIRMATION_EMAIL),
						placeholder);
				setResponsePage(MessagePage.getMessagePageWithLogout(getString("rerequest.email")));
				userService.save(user);
			}
		});
	}
}
