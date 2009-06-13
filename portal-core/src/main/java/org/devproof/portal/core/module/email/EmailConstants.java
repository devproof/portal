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

	final public static String EMAIL_PLACEHOLDER_USERNAME = "#USERNAME#";
	final public static String EMAIL_PLACEHOLDER_FIRSTNAME = "#FIRSTNAME#";
	final public static String EMAIL_PLACEHOLDER_LASTNAME = "#LASTNAME#";
	final public static String EMAIL_PLACEHOLDER_PAGENAME = "#PAGENAME#";
	final public static String EMAIL_PLACEHOLDER_EMAIL = "#EMAIL#";
	final public static String EMAIL_PLACEHOLDER_BIRTHDAY = "#BIRTHDAY#";
	final public static String EMAIL_PLACEHOLDER_CONFIRMATIONLINK = "#CONFIRMATIONLINK#";
	final public static String EMAIL_PLACEHOLDER_PASSWORDRESETLINK = "#PASSWORDRESETLINK#";
	final public static String EMAIL_PLACEHOLDER_CONTENT = "#CONTENT#";
	final public static String EMAIL_PLACEHOLDER_CONTACT_FULLNAME = "#CONTACT_FULLNAME#";
	final public static String EMAIL_PLACEHOLDER_CONTACT_EMAIL = "#CONTACT_EMAIL#";
	final public static String EMAIL_PLACEHOLDER_CONTACT_IP = "#CONTACT_IP#";

	final public static String CONF_PAGE_NAME = "page_name";
	final public static String CONF_FROM_EMAIL_ADDRESS = "from_email_address";
	final public static String CONF_FROM_EMAIL_NAME = "from_email_name";
}
