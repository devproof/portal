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
package org.devproof.portal.core.module.common;

import org.apache.wicket.ResourceReference;
import org.devproof.portal.core.module.common.component.richtext.RichTextArea;

/**
 * Contains common constants
 * 
 * @author Carsten Hufe
 */
public class CommonConstants {
	private CommonConstants() {
	}

	final public static String JNDI_DATASOURCE = "java:comp/env/jdbc/devproof/portal";
	final public static String JNDI_MAIL_SESSION = "java:comp/env/mail/Session";

	final public static String JNDI_PROP_HIBERNATE_DIALECT = "java:comp/env/prop/hibernate_dialect";
	final public static String JNDI_PROP_HIBERNATE_SHOW_SQL = "java:comp/env/prop/hibernate_show_sql";
	final public static String JNDI_PROP_HIBERNATE_FORMAT_SQL = "java:comp/env/prop/hibernate_format_sql";
	final public static String JNDI_PROP_HIBERNATE_HBM2DDL_AUTO = "java:comp/env/prop/hibernate_hbm2ddl_auto";

	final public static String HIBERNATE_DEFAULT_DIALECT = "org.hibernate.dialect.MySQLDialect";
	final public static String HIBERNATE_DEFAULT_SHOW_SQL = "false";
	final public static String HIBERNATE_DEFAULT_FORMAT_SQL = "false";
	final public static String HIBERNATE_DEFAULT_HBM2DDL_AUTO = "false";

	final public static String CONF_GOOGLE_ANALYTICS_ENABLED = "google_analytics_enabled";
	final public static String CONF_GOOGLE_WEBPROPERTY_ID = "google_webproperty_id";
	final public static ResourceReference REF_SYNTAXHIGHLIGHTER_SWF = new ResourceReference(CommonConstants.class, "js/SyntaxHighlighter/flash/clipboard.swf");

	final public static String GLOBAL_ADMIN_BOX_LINK_LABEL = "adminLinkLabel";
	final public static String MAIN_NAVIGATION_LINK_LABEL = "mainNavigationLinkLabel";
	final public static String CONTENT_TITLE_LABEL = "contentTitle";

	final public static String SESSION_ID_COOKIE = "dpPortalSessionId";
	final public static String CONF_SHOW_REAL_AUTHOR = "show_real_author";
	final public static String CONF_SHOW_MODIFIED_BY = "show_modified_by";
	final public static String CONF_PAGE_TITLE = "page_title";
	final public static String CONF_COPYRIGHT_OWNER = "copyright_owner";

	final public static String CONF_STRING2IMG_FONT = "spring.fontService.findAllSystemFonts.name.name.string2image";
	final public static String CONF_UNKNOWN_ERROR_EMAIL = "spring.emailTemplateDao.findAll.subject.id.unknownerror";
	final public static ResourceReference REF_DEFAULT_CSS = new ResourceReference(CommonConstants.class, "css/default.css");
	final public static ResourceReference REF_EDIT_IMG = new ResourceReference(CommonConstants.class, "img/edit.gif");
	final public static ResourceReference REF_DELETE_IMG = new ResourceReference(CommonConstants.class, "img/delete.gif");
	final public static ResourceReference REF_VIEW_IMG = new ResourceReference(CommonConstants.class, "img/view.gif");
	final public static ResourceReference REF_DOWN_IMG = new ResourceReference(CommonConstants.class, "img/down.gif");
	final public static ResourceReference REF_UP_IMG = new ResourceReference(CommonConstants.class, "img/up.gif");
	final public static ResourceReference REF_ICONCODE_IMG = new ResourceReference(RichTextArea.class, "img/iconcode.gif");
	final public static ResourceReference REF_STRING2IMG_IMG = new ResourceReference(RichTextArea.class, "img/string2img.gif");

}
