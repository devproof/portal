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
package org.devproof.portal.core.module.common.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.devproof.portal.core.app.PortalSession;

/**
 * @author Carsten Hufe
 */
public class MessagePage extends TemplatePage {

	private static final long serialVersionUID = 1L;

	public MessagePage(final PageParameters params) {
		super(params);
		String msg = params.getString("message");
		if (msg == null) {
			msg = "unknown.error";
		}
		addOrReplace(new Label("message", this.getString(msg)));
	}

	private MessagePage(final PageParameters params, final String message) {
		super(params);
		addOrReplace(new Label("message", message));
	}

	public static MessagePage getMessagePageByKey(final String messageKey) {
		PageParameters params = new PageParameters();
		params.add("message", messageKey);
		return new MessagePage(params);
	}

	public static MessagePage getMessagePage(final String message) {
		PageParameters params = new PageParameters();
		return new MessagePage(params, message);
	}

	public static MessagePage getMessagePage(final String message, final String redirectUrl) {
		PageParameters params = new PageParameters();
		return new MessagePage(params, message) {
			@Override
			public String getRedirectURLAfterLogin() {
				return redirectUrl;
			}
		};
	}

	public static MessagePage getMessagePageWithLogout(final String messageKey) {
		((PortalSession) Session.get()).logoutUser();
		PageParameters params = new PageParameters();
		return new MessagePage(params, messageKey);
	}

	public String getRedirectURLAfterLogin() {
		return null;
	}
}
