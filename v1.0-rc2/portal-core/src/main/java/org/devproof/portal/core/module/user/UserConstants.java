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
package org.devproof.portal.core.module.user;

/**
 * @author Carsten Hufe
 */
public class UserConstants {
	private UserConstants() {
	}

	final public static String UNKNOWN_USERNAME = "unknown";
	final public static String CONF_REGISTRATION_EMAIL = "spring.emailTemplateDao.findAll.subject.id.regemail";
	final public static String CONF_RECONFIRMATION_EMAIL = "spring.emailTemplateDao.findAll.subject.id.reconfirmemail";
	final public static String CONF_PASSWORDFORGOT_EMAIL = "spring.emailTemplateDao.findAll.subject.id.forgotemail";
	final public static String CONF_NOTIFY_USER_REGISTRATION = "spring.emailTemplateDao.findAll.subject.id.registereduser";
	final public static String CONF_REGISTRATION_CAPTCHA = "registration_captcha";
	final public static String CONF_EMAIL_VALIDATION = "email_validation";
	final public static String CONF_REGISTRATION_REQUIRED_NAME = "registration_required_name";
	final public static String CONF_REGISTRATION_REQUIRED_BIRTHDAY = "registration_required_birthday";

}
