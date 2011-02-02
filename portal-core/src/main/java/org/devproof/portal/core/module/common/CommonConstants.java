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
package org.devproof.portal.core.module.common;

import org.apache.wicket.ResourceReference;
import org.devproof.portal.core.module.common.component.richtext.FullRichTextArea;
import org.hibernate.dialect.MySQL5Dialect;

/**
 * Contains common constants
 *
 * @author Carsten Hufe
 */
public interface CommonConstants {

    String JNDI_DATASOURCE = "java:comp/env/jdbc/devproof/portal";
    String JNDI_MAIL_SESSION = "java:comp/env/mail/Session";

    String JNDI_PROP_HIBERNATE_DIALECT = "java:comp/env/config/devproof/hibernate_dialect";
    String JNDI_PROP_HIBERNATE_SHOW_SQL = "java:comp/env/config/devproof/hibernate_show_sql";
    String JNDI_PROP_HIBERNATE_FORMAT_SQL = "java:comp/env/config/devproof/hibernate_format_sql";
    String JNDI_PROP_HIBERNATE_HBM2DDL_AUTO = "java:comp/env/config/devproof/hibernate_hbm2ddl_auto";
    String JNDI_PROP_HIBERNATE_CONNECTION_ISOLATION = "java:comp/env/config/devproof/hibernate_connection_isolation";
    String JNDI_PROP_HIBERNATE_SECOND_LEVEL_CACHE = "java:comp/env/config/devproof/hibernate.cache.use_second_level_cache";
    String JNDI_PROP_HIBERNATE_QUERY_CACHE = "java:comp/env/config/devproof/hibernate.cache.use_query_cache";

    String JNDI_PROP_EMAIL_DISABLED = "java:comp/env/config/devproof/disable_email";

    String EMAIL_DEFAULT_DISABLED = "false";
    String HIBERNATE_DEFAULT_DIALECT = MySQL5Dialect.class.getName();
    String HIBERNATE_DEFAULT_SHOW_SQL = "false";
    String HIBERNATE_DEFAULT_FORMAT_SQL = "false";
    String HIBERNATE_DEFAULT_HBM2DDL_AUTO = "none";
    String HIBERNATE_DEFAULT_CONNECTION_ISOLATION = "2";
    String HIBERNATE_DEFAULT_SECOND_LEVEL_CACHE = "true";
    String HIBERNATE_DEFAULT_QUERY_CACHE = "true";

    String CONF_GOOGLE_ANALYTICS_ENABLED = "google_analytics_enabled";
    String CONF_GOOGLE_WEBPROPERTY_ID = "google_webproperty_id";
    ResourceReference REF_SYNTAXHIGHLIGHTER_JS = new ResourceReference(CommonConstants.class, "js/SyntaxHighlighter/shCore.js");
    String GLOBAL_ADMIN_BOX_LINK_LABEL = "adminLinkLabel";
    String MAIN_NAVIGATION_LINK_LABEL = "mainNavigationLinkLabel";
    String CONTENT_TITLE_LABEL = "contentTitle";

    String SESSION_ID_COOKIE = "dpPortalSessionId";
    String CONF_SHOW_REAL_AUTHOR = "show_real_author";
    String CONF_SHOW_MODIFIED_BY = "show_modified_by";
    String CONF_SHOW_MODIFIED_AT_AS_CREATED_AT = "show_modified_at_as_created_at";

    String CONF_PAGE_TITLE = "page_title";
    String CONF_COPYRIGHT_OWNER = "copyright_owner";

    String CONF_STRING2IMG_FONT = "spring.fontService.findAllSystemFonts.name.name.string2image";
    String CONF_SYNTAXHL_THEME = "spring.fontService.findSyntaxHighlighterThemes.theme";
    String CONF_UNKNOWN_ERROR_EMAIL = "spring.emailService.findAll.subject.id.unknownerror";
    ResourceReference REF_DEFAULT_CSS = new ResourceReference(CommonConstants.class, "css/default.css");
    ResourceReference REF_ADD_IMG = new ResourceReference(CommonConstants.class, "img/add.gif");
    ResourceReference REF_EDIT_IMG = new ResourceReference(CommonConstants.class, "img/edit.png");
    ResourceReference REF_DELETE_IMG = new ResourceReference(CommonConstants.class, "img/delete.png");
    ResourceReference REF_VIEW_IMG = new ResourceReference(CommonConstants.class, "img/view.png");
    ResourceReference REF_DOWN_IMG = new ResourceReference(CommonConstants.class, "img/arrow_down.png");
    ResourceReference REF_UP_IMG = new ResourceReference(CommonConstants.class, "img/arrow_up.png");
    ResourceReference REF_INFORMATION_IMG = new ResourceReference(CommonConstants.class, "img/information.png");
    ResourceReference REF_ICONCODE_IMG = new ResourceReference(FullRichTextArea.class, "img/iconcode.gif");
    ResourceReference REF_STRING2IMG_IMG = new ResourceReference(FullRichTextArea.class, "img/string2img.gif");

    String ENTITY_CORE_CACHE_REGION = "entity.core";
    String QUERY_CORE_CACHE_REGION = "query.core";

}
