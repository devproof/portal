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

	public static final String SMALL_THEME_PATHS[] = { "theme", "org/devproof/portal/core/module/common/page/",
			"org/devproof/portal/core/module/common/css/", "org/devproof/portal/core/module/common/img/",
			"org/devproof/portal/core/module/common/page/" };
	public static final String COMPLETE_THEME_PATHS[] = { "/" }; // everything
	public static final String ALLOWED_THEME_EXT[] = { ".html", ".css", ".gif", ".jpg", ".png", ".properties" };
	public static final String FILTER_PATHS[] = { "META-INF", "log4j.properties",
			"org/devproof/portal/core/module/common/component/richtext" };

	public static final String CONF_SELECTED_THEME_UUID = "hidden.selected_theme_uuid";
	public static final String CONF_SELECTED_THEME_DEFAULT = "_default_";
	// public static final String PORTAL_THEME_VERSION = "${devproof.version}";
}
