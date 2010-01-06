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
package org.devproof.portal.core.module.common;

import org.apache.wicket.ResourceReference;
import org.devproof.portal.core.module.common.component.richtext.RichTextArea;
import org.hibernate.dialect.MySQL5Dialect;

/**
 * Contains common constants
 * 
 * @author Carsten Hufe
 */
public class CommonConstants {
	private CommonConstants() {
	}

	public static final String JNDI_DATASOURCE = "java:comp/env/jdbc/devproof/portal";
	public static final String JNDI_MAIL_SESSION = "java:comp/env/mail/Session";

	public static final String JNDI_PROP_HIBERNATE_DIALECT = "java:comp/env/config/devproof/hibernate_dialect";
	public static final String JNDI_PROP_HIBERNATE_SHOW_SQL = "java:comp/env/config/devproof/hibernate_show_sql";
	public static final String JNDI_PROP_HIBERNATE_FORMAT_SQL = "java:comp/env/config/devproof/hibernate_format_sql";
	public static final String JNDI_PROP_HIBERNATE_HBM2DDL_AUTO = "java:comp/env/config/devproof/hibernate_hbm2ddl_auto";
	public static final String JNDI_PROP_HIBERNATE_CONNECTION_ISOLATION = "java:comp/env/config/devproof/hibernate_connection_isolation";

	public static final String HIBERNATE_DEFAULT_DIALECT = MySQL5Dialect.class.getName();
	public static final String HIBERNATE_DEFAULT_SHOW_SQL = "false";
	public static final String HIBERNATE_DEFAULT_FORMAT_SQL = "false";
	public static final String HIBERNATE_DEFAULT_HBM2DDL_AUTO = "none";
	public static final String HIBERNATE_DEFAULT_CONNECTION_ISOLATION = "2";

	public static final String CONF_GOOGLE_ANALYTICS_ENABLED = "google_analytics_enabled";
	public static final String CONF_GOOGLE_WEBPROPERTY_ID = "google_webproperty_id";
	public static final ResourceReference REF_SYNTAXHIGHLIGHTER_SWF = new ResourceReference(CommonConstants.class,
			"js/SyntaxHighlighter/clipboard.swf");

	public static final String GLOBAL_ADMIN_BOX_LINK_LABEL = "adminLinkLabel";
	public static final String MAIN_NAVIGATION_LINK_LABEL = "mainNavigationLinkLabel";
	public static final String CONTENT_TITLE_LABEL = "contentTitle";

	public static final String SESSION_ID_COOKIE = "dpPortalSessionId";
	public static final String CONF_SHOW_REAL_AUTHOR = "show_real_author";
	public static final String CONF_SHOW_MODIFIED_BY = "show_modified_by";
	public static final String CONF_PAGE_TITLE = "page_title";
	public static final String CONF_COPYRIGHT_OWNER = "copyright_owner";

	public static final String CONF_STRING2IMG_FONT = "spring.fontService.findAllSystemFonts.name.name.string2image";
	public static final String CONF_UNKNOWN_ERROR_EMAIL = "spring.emailTemplateDao.findAll.subject.id.unknownerror";
	public static final ResourceReference REF_DEFAULT_CSS = new ResourceReference(CommonConstants.class,
			"css/default.css");
	public static final ResourceReference REF_EDIT_IMG = new ResourceReference(CommonConstants.class, "img/edit.gif");
	public static final ResourceReference REF_DELETE_IMG = new ResourceReference(CommonConstants.class,
			"img/delete.gif");
	public static final ResourceReference REF_VIEW_IMG = new ResourceReference(CommonConstants.class, "img/view.gif");
	public static final ResourceReference REF_DOWN_IMG = new ResourceReference(CommonConstants.class, "img/down.gif");
	public static final ResourceReference REF_UP_IMG = new ResourceReference(CommonConstants.class, "img/up.gif");
	public static final ResourceReference REF_ICONCODE_IMG = new ResourceReference(RichTextArea.class,
			"img/iconcode.gif");
	public static final ResourceReference REF_STRING2IMG_IMG = new ResourceReference(RichTextArea.class,
			"img/string2img.gif");

}
