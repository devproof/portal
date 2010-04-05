/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.email;

/**
 * @author Carsten Hufe
 */
public interface EmailConstants {
	String EMAIL_PLACEHOLDER_USERNAME = "#USERNAME#";
	String EMAIL_PLACEHOLDER_FIRSTNAME = "#FIRSTNAME#";
	String EMAIL_PLACEHOLDER_LASTNAME = "#LASTNAME#";
	String EMAIL_PLACEHOLDER_PAGENAME = "#PAGENAME#";
	String EMAIL_PLACEHOLDER_EMAIL = "#EMAIL#";
	String EMAIL_PLACEHOLDER_BIRTHDAY = "#BIRTHDAY#";
	String EMAIL_PLACEHOLDER_CONFIRMATIONLINK = "#CONFIRMATIONLINK#";
	String EMAIL_PLACEHOLDER_PASSWORDRESETLINK = "#PASSWORDRESETLINK#";
	String EMAIL_PLACEHOLDER_CONTENT = "#CONTENT#";
	String EMAIL_PLACEHOLDER_CONTACT_FULLNAME = "#CONTACT_FULLNAME#";
	String EMAIL_PLACEHOLDER_CONTACT_EMAIL = "#CONTACT_EMAIL#";
	String EMAIL_PLACEHOLDER_CONTACT_IP = "#CONTACT_IP#";

	String CONF_PAGE_NAME = "page_name";
	String CONF_FROM_EMAIL_ADDRESS = "from_email_address";
	String CONF_FROM_EMAIL_NAME = "from_email_name";
}
