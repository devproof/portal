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
package org.devproof.portal.core.module.theme;

/**
 * @author Carsten Hufe
 */
public class ThemeConstants {
	private ThemeConstants() {
	}

	final public static String SMALL_THEME_PATHS[] = { "theme", "org/devproof/portal/core/module/common/page/", "org/devproof/portal/core/module/common/css/",
			"org/devproof/portal/core/module/common/img/", "org/devproof/portal/core/module/common/page/" };
	final public static String COMPLETE_THEME_PATHS[] = { "/" }; // everything
	final public static String ALLOWED_THEME_EXT[] = { ".html", ".css", ".gif", ".jpg", ".png", ".properties" };
	final public static String FILTER_PATHS[] = { "META-INF", "portal.properties", "portal-test.properties", "log4j.properties", "org/devproof/portal/core/module/common/component/richtext" };

	final public static String CONF_SELECTED_THEME_UUID = "hidden.selected_theme_uuid";
	final public static String CONF_SELECTED_THEME_DEFAULT = "_default_";
	final public static String PORTAL_THEME_VERSION = "1.0-rc1";
}
