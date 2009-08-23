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

import java.text.SimpleDateFormat;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.user.entity.UserEntity;

/**
 * @author Carsten Hufe
 */
public class UserInfoPanel extends Panel {

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "dateFormat")
	private SimpleDateFormat dateFormat;
	@SpringBean(name = "dateTimeFormat")
	private SimpleDateFormat dateTimeFormat;

	public UserInfoPanel(final String id, final UserEntity user) {
		super(id);

		add(new Label("username", user.getUsername()));
		add(new Label("firstname", user.getFirstname()));
		add(new Label("lastname", user.getLastname()));
		add(new Label("birthday", user.getBirthday() != null ? dateFormat.format(user.getBirthday()) : ""));
		add(new Label("email", user.getEmail()));
		add(new Label("active", user.getActive() != null ? getString("active." + user.getActive().toString()) : ""));
		add(new Label("confirmed", user.getConfirmed() != null ? getString("confirmed."
				+ user.getConfirmed().toString()) : ""));
		add(new Label("registeredAt", user.getRegistrationDate() != null ? dateTimeFormat.format(user
				.getRegistrationDate()) : ""));
		add(new Label("lastLoginAt", user.getLastLoginAt() != null ? dateTimeFormat.format(user.getLastLoginAt()) : ""));
		add(new Label("lastIp", user.getLastIp()));
	}
}
