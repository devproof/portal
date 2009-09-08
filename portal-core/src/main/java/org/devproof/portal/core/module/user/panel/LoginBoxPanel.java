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
package org.devproof.portal.core.module.user.panel;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.value.ValueMap;
import org.devproof.portal.core.app.PortalApplication;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.box.panel.BoxTitleVisibility;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.user.exception.UserNotConfirmedException;
import org.devproof.portal.core.module.user.page.ForgotPasswordPage;
import org.devproof.portal.core.module.user.page.ReenterEmailPage;
import org.devproof.portal.core.module.user.page.RegisterPage;

/**
 * @author Carsten Hufe
 */
public class LoginBoxPanel extends Panel implements BoxTitleVisibility {

	private static final long serialVersionUID = 1L;
	private final ValueMap properties = new ValueMap();
	private WebMarkupContainer titleContainer;

	public LoginBoxPanel(final String id, final PageParameters params) {
		super(id);
		add(titleContainer = new WebMarkupContainer("title"));
		final HiddenField<String> hiddenParam = new HiddenField<String>("optparam", Model.of(params.getString("0")));
		final StatelessForm<ValueMap> form = new StatelessForm<ValueMap>("loginForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				final PortalSession session = (PortalSession) getSession();
				try {

					final String message = session.authenticate(properties.getString("username"), properties
							.getString("password"));
					if (message == null) {

						info(getString("logged.in"));
						// redirect to the same page so that the rights will be
						// rechecked!
						if (getPage() instanceof MessagePage) {
							MessagePage msgPage = (MessagePage) getPage();
							String redirectUrl = msgPage.getRedirectURLAfterLogin();
							if (redirectUrl != null) {
								setResponsePage(new RedirectPage(redirectUrl));
							} else {
								@SuppressWarnings("unchecked")
								Class<? extends Page> homePage = ((PortalApplication) getApplication()).getHomePage();
								setResponsePage(homePage);
							}
						} else {
							setResponsePage(getPage().getClass(), new PageParameters("0=" + hiddenParam.getValue()));
						}
					} else {
						error(getString(message));
					}
				} catch (final UserNotConfirmedException e) {
					setResponsePage(new ReenterEmailPage(properties.getString("username")));
				}
			}

			@Override
			protected void onValidate() {
				if (getPage().getClass().equals(MessagePage.class)) {
					setRedirect(false);
					@SuppressWarnings("unchecked")
					Class<? extends Page> homePage = ((PortalApplication) getApplication()).getHomePage();
					setResponsePage(homePage);
				}
				super.onValidate();
			}

		};
		form.setModel(new CompoundPropertyModel<ValueMap>(properties));
		form.add(new RequiredTextField<String>("username"));
		form.add(new PasswordTextField("password"));
		// View for ArticleViewPage and OtherPageViewPage
		form.add(hiddenParam);
		add(form);
		add(new BookmarkablePageLink<Void>("registerLink", RegisterPage.class));
		add(new BookmarkablePageLink<Void>("forgotPasswordLink", ForgotPasswordPage.class));
	}

	@Override
	public void setTitleVisible(final boolean visible) {
		titleContainer.setVisible(visible);
	}
}
