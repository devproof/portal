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
package org.devproof.portal.core.module.user;

/**
 * @author Carsten Hufe
 */
public class UserConstants {
	private UserConstants() {
	}

	public static final String UNKNOWN_USERNAME = "unknown";
	public static final String CONF_REGISTRATION_EMAIL = "spring.emailTemplateDao.findAll.subject.id.regemail";
	public static final String CONF_RECONFIRMATION_EMAIL = "spring.emailTemplateDao.findAll.subject.id.reconfirmemail";
	public static final String CONF_PASSWORDFORGOT_EMAIL = "spring.emailTemplateDao.findAll.subject.id.forgotemail";
	public static final String CONF_NOTIFY_USER_REGISTRATION = "spring.emailTemplateDao.findAll.subject.id.registereduser";
	public static final String CONF_EMAIL_VALIDATION = "email_validation";
	public static final String CONF_REGISTRATION_REQUIRED_NAME = "registration_required_name";
	public static final String CONF_REGISTRATION_REQUIRED_BIRTHDAY = "registration_required_birthday";
	public static final String ENTITY_CACHE_REGION = "entity.user";
	public static final String QUERY_CACHE_REGION = "query.user";
}
