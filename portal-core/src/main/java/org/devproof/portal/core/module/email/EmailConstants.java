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
package org.devproof.portal.core.module.email;

/**
 * @author Carsten Hufe
 */
public class EmailConstants {
	private EmailConstants() {
	}

	public static final String EMAIL_PLACEHOLDER_USERNAME = "#USERNAME#";
	public static final String EMAIL_PLACEHOLDER_FIRSTNAME = "#FIRSTNAME#";
	public static final String EMAIL_PLACEHOLDER_LASTNAME = "#LASTNAME#";
	public static final String EMAIL_PLACEHOLDER_PAGENAME = "#PAGENAME#";
	public static final String EMAIL_PLACEHOLDER_EMAIL = "#EMAIL#";
	public static final String EMAIL_PLACEHOLDER_BIRTHDAY = "#BIRTHDAY#";
	public static final String EMAIL_PLACEHOLDER_CONFIRMATIONLINK = "#CONFIRMATIONLINK#";
	public static final String EMAIL_PLACEHOLDER_PASSWORDRESETLINK = "#PASSWORDRESETLINK#";
	public static final String EMAIL_PLACEHOLDER_CONTENT = "#CONTENT#";
	public static final String EMAIL_PLACEHOLDER_CONTACT_FULLNAME = "#CONTACT_FULLNAME#";
	public static final String EMAIL_PLACEHOLDER_CONTACT_EMAIL = "#CONTACT_EMAIL#";
	public static final String EMAIL_PLACEHOLDER_CONTACT_IP = "#CONTACT_IP#";

	public static final String CONF_PAGE_NAME = "page_name";
	public static final String CONF_FROM_EMAIL_ADDRESS = "from_email_address";
	public static final String CONF_FROM_EMAIL_NAME = "from_email_name";
}
