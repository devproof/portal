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

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.value.ValueMap;
import org.devproof.portal.core.app.PortalApplication;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.user.exception.UserNotConfirmedException;

/**
 * @author Carsten Hufe
 */
public class LoginPage extends TemplatePage {

	private static final long serialVersionUID = 1L;

	public LoginPage(final PageParameters params) {
		super(params);
		final RequiredTextField<String> username = new RequiredTextField<String>("username");
		final PasswordTextField password = new PasswordTextField("password");
		final StatelessForm<ValueMap> form = new StatelessForm<ValueMap>("loginForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				final PortalSession session = (PortalSession) getSession();
				try {

					final String message = session.authenticate(username.getValue(), password.getValue());
					if (message == null) {
						boolean productionMode = ((PortalApplication) getApplication()).isProductionMode();
						// production mode check is for unit tests
						if (productionMode) {
							@SuppressWarnings("unchecked")
							Class<? extends Page> homePage = ((PortalApplication) getApplication()).getHomePage();
							setResponsePage(homePage, new PageParameters("infoMsg=" + getString("logged.in")));
						}
					} else {
						error(getString(message));
					}
				} catch (final UserNotConfirmedException e) {
					setResponsePage(new ReenterEmailPage(username.getValue()));
				}
			}

		};
		form.setModel(new CompoundPropertyModel<ValueMap>(new ValueMap()));
		form.setOutputMarkupId(true);
		form.add(username);
		form.add(password);
		add(form);
		add(new BookmarkablePageLink<Void>("registerLink", RegisterPage.class));
		add(new BookmarkablePageLink<Void>("forgotPasswordLink", ForgotPasswordPage.class));
	}
}
