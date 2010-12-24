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
package org.devproof.portal.core.module.theme;

/**
 * @author Carsten Hufe
 */
public interface ThemeConstants {
    String SMALL_THEME_PATHS[] = {"theme", "org/devproof/portal/core/module/common/page/", "org/devproof/portal/core/module/common/css/", "org/devproof/portal/core/module/common/img/", "org/devproof/portal/core/module/common/page/"};
    String COMPLETE_THEME_PATHS[] = {"/"}; // everything
    String ALLOWED_THEME_EXT[] = {".html", ".css", ".gif", ".jpg", ".png", ".properties"};
    String FILTER_PATHS[] = {"META-INF", "log4j.properties", "org/devproof/portal/core/module/common/component/richtext"};

    String CONF_SELECTED_THEME_UUID = "hidden.selected_theme_uuid";
    String CONF_SELECTED_THEME_DEFAULT = "_default_";
    String ADMIN_RIGHT = "theme.admin";
    // String PORTAL_THEME_VERSION = "${devproof.version}";
}
