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

		this.add(new Label("username", user.getUsername()));
		this.add(new Label("firstname", user.getFirstname()));
		this.add(new Label("lastname", user.getLastname()));
		this.add(new Label("birthday", user.getBirthday() != null ? this.dateFormat.format(user.getBirthday()) : ""));
		this.add(new Label("email", user.getEmail()));
		this.add(new Label("active", user.getActive() != null ? this.getString("active." + user.getActive().toString()) : ""));
		this.add(new Label("confirmed", user.getConfirmed() != null ? this.getString("confirmed." + user.getConfirmed().toString()) : ""));
		this.add(new Label("registeredAt", user.getRegistrationDate() != null ? this.dateTimeFormat.format(user.getRegistrationDate()) : ""));
		this.add(new Label("lastLoginAt", user.getLastLoginAt() != null ? this.dateTimeFormat.format(user.getLastLoginAt()) : ""));
		this.add(new Label("lastIp", user.getLastIp()));
	}
}
