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
package org.devproof.portal.core.module.theme.service;

import java.io.File;
import java.util.List;

import org.devproof.portal.core.module.theme.bean.ThemeBean;

/**
 * @author Carsten Hufe
 */
public interface ThemeService {
	public enum ValidationKey {
		VALID, MISSING_DESCRIPTOR_FILE, INVALID_DESCRIPTOR_FILE, WRONG_VERSION, NOT_A_JARFILE
	}

	/**
	 * Selects a theme
	 */
	public void selectTheme(ThemeBean theme);

	/**
	 * Validates a theme
	 */
	public ValidationKey validateTheme(File themeArchive);

	/**
	 * Installs a theme
	 */
	public void install(File themeArchive);

	/**
	 * Uninstalls a theme, if the theme is selected the default theme will be
	 * selected
	 */
	public void uninstall(ThemeBean theme);

	/**
	 * Returns all installed themes
	 */
	public List<ThemeBean> findAllThemes();

	/**
	 * Creates a zip file with the "small" theme template. That means only
	 * TemplatePage (Base Page) and the main css files
	 */
	public File createSmallDefaultTheme();

	/**
	 * Creates a zip file with the "complete" theme template. That means
	 * everything is exported, excepting tinymce stuff
	 */
	public File createCompleteDefaultTheme();
}
