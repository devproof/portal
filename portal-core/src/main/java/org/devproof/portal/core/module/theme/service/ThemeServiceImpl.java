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
package org.devproof.portal.core.module.theme.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devproof.portal.core.module.configuration.entity.ConfigurationEntity;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.theme.ThemeConstants;
import org.devproof.portal.core.module.theme.bean.ThemeBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ServletContextAware;

public class ThemeServiceImpl implements ThemeService, ServletContextAware, ApplicationContextAware {
	private ServletContext servletContext;
	private ApplicationContext applicationContext;
	private ConfigurationService configurationService;
	private static final Log LOG = LogFactory.getLog(ThemeServiceImpl.class);

	@Override
	public List<ThemeBean> findAllThemes() {
		try {
			List<ThemeBean> themes = new ArrayList<ThemeBean>();
			ThemeBean defaultTheme = new ThemeBean();
			defaultTheme.setTheme("Default Theme");
			defaultTheme.setAuthor("devproof");
			defaultTheme.setUrl("http://www.devproof.org");
			defaultTheme.setUuid(ThemeConstants.CONF_SELECTED_THEME_DEFAULT);
			themes.add(defaultTheme);
			File folder = new File(servletContext.getRealPath("/WEB-INF/themes/"));
			if (!folder.exists()) {
				FileUtils.forceMkdir(folder);
			}
			for (File themeFolder : folder.listFiles()) {
				if (themeFolder.isDirectory()) {
					File theme = new File(themeFolder.getAbsolutePath() + File.separator + "theme.properties");
					if (theme.exists() && theme.isFile()) {
						FileInputStream fis = new FileInputStream(theme);
						themes.add(getBeanFromInputStream(themeFolder.getName(), fis));
						fis.close();
					}
				}
			}
			return themes;
		} catch (MalformedURLException e) {
			throw new UnhandledException(e);
		} catch (FileNotFoundException e) {
			throw new UnhandledException(e);
		} catch (IOException e) {
			throw new UnhandledException(e);
		}
	}

	@Override
	public void install(final File themeArchive) {
		String uuid = UUID.randomUUID().toString();
		try {
			File folder = new File(servletContext.getRealPath("/WEB-INF/themes"));
			folder = new File(folder.toString() + File.separator + uuid);
			FileUtils.forceMkdir(folder);
			ZipFile zipFile = new ZipFile(themeArchive);

			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				File out = new File(folder.getAbsolutePath() + File.separator + entry.getName());
				if (entry.isDirectory()) {
					FileUtils.forceMkdir(out);
				} else {
					FileUtils.touch(out);
					FileOutputStream fos = new FileOutputStream(out, false);
					IOUtils.copy(zipFile.getInputStream(entry), fos);
					fos.close();
				}
			}
			zipFile.close();
			LOG.info("New theme installed: " + uuid);
		} catch (MalformedURLException e) {
			LOG.warn("Unknown error", e);
		} catch (IOException e) {
			LOG.warn("Unknown error", e);
		}
	}

	@Override
	public void selectTheme(final ThemeBean theme) {
		ConfigurationEntity conf = configurationService.findById(ThemeConstants.CONF_SELECTED_THEME_UUID);
		conf.setValue(theme.getUuid());
		configurationService.save(conf);
		configurationService.refreshGlobalConfiguration();
		LOG.info("Another theme selected: " + theme.getUuid());
	}

	@Override
	public void uninstall(final ThemeBean theme) {
		try {
			File folder = new File(servletContext.getRealPath("/WEB-INF/themes/" + theme.getUuid()));
			FileUtils.deleteDirectory(folder);
			String uuid = configurationService.findAsString(ThemeConstants.CONF_SELECTED_THEME_UUID);
			if (uuid.equals(theme.getUuid())) {
				ConfigurationEntity conf = configurationService.findById(ThemeConstants.CONF_SELECTED_THEME_UUID);
				conf.setValue(ThemeConstants.CONF_SELECTED_THEME_DEFAULT);
				configurationService.save(conf);
				configurationService.refreshGlobalConfiguration();
			}
			LOG.info("Theme uninstalled: " + theme.getUuid());
		} catch (MalformedURLException e) {
			LOG.error("Mailformed URL on an installed theme", e);
		} catch (IOException e) {
			LOG.error("Deletion on theme failed", e);
		}
	}

	@Override
	public ValidationKey validateTheme(final File themeArchive) {
		try {
			ZipFile zip = new ZipFile(themeArchive);
			ZipEntry entry = zip.getEntry("theme.properties");
			if (entry != null) {
				InputStream is = zip.getInputStream(entry);
				ThemeBean bean = getBeanFromInputStream("", is);
				if (StringUtils.isBlank(bean.getAuthor()) || StringUtils.isBlank(bean.getUrl())
						|| StringUtils.isBlank(bean.getPortalThemeVersion())
						|| StringUtils.isBlank(bean.getPortalVersion()) || StringUtils.isBlank(bean.getTheme())) {
					return ValidationKey.INVALID_DESCRIPTOR_FILE;
				} else {
					if (ThemeConstants.PORTAL_THEME_VERSION.equals(bean.getPortalThemeVersion())) {
						return ValidationKey.VALID;
					} else {
						return ValidationKey.WRONG_VERSION;
					}
				}
			}
			return ValidationKey.MISSING_DESCRIPTOR_FILE;
		} catch (ZipException e) {
			LOG.warn(themeArchive.toString() + " was not valid", e);
			return ValidationKey.NOT_A_JARFILE;
		} catch (IOException e) {
			LOG.warn(themeArchive.toString() + " was not valid", e);
			return ValidationKey.NOT_A_JARFILE;
		}
	}

	@Override
	public File createCompleteDefaultTheme() {
		return createDefaultTheme(ThemeConstants.COMPLETE_THEME_PATHS, ThemeConstants.FILTER_PATHS);
	}

	@Override
	public File createSmallDefaultTheme() {
		return createDefaultTheme(ThemeConstants.SMALL_THEME_PATHS, ThemeConstants.FILTER_PATHS);
	}

	private File createDefaultTheme(final String[] themePaths, final String[] filterPaths) {
		try {
			@SuppressWarnings("unchecked")
			Set<String> libs = servletContext.getResourcePaths("/WEB-INF/lib");
			Set<String> zipResources = new HashSet<String>();
			File back = File.createTempFile("devproof-defaulttheme-", ".zip");
			FileOutputStream fos = new FileOutputStream(back);
			ZipOutputStream zos = new ZipOutputStream(fos);
			if (libs.isEmpty()) {
				// development variant
				Resource root[] = applicationContext.getResources("classpath:/");
				for (String ext : ThemeConstants.ALLOWED_THEME_EXT) {
					for (String themePath : themePaths) {
						Resource resources[] = null;
						if (themePath.endsWith("/")) {
							resources = applicationContext.getResources("classpath*:" + themePath + "**/*" + ext);
						} else {
							resources = applicationContext.getResources("classpath*:" + themePath + ext);
						}
						for (Resource r : resources) {
							String zipPath = getZipPath(root, r);
							if (!isFiltered(null, filterPaths, zipPath) && !zipResources.contains(zipPath)) {
								zipResources.add(zipPath);
								ZipEntry ze = new ZipEntry(zipPath);
								zos.putNextEntry(ze);
								InputStream is = r.getInputStream();
								byte[] file = new byte[is.available()];
								is.read(file);
								is.close();
								zos.write(file);
								zos.closeEntry();
							}
						}
					}
				}
			} else {
				// prod variant
				for (String lib : libs) {
					URL url = servletContext.getResource(lib);
					JarFile file = new JarFile(url.getFile());
					Enumeration<JarEntry> entries = file.entries();
					while (entries.hasMoreElements()) {
						JarEntry jarEntry = entries.nextElement();
						if (!isFiltered(themePaths, filterPaths, jarEntry.getName())
								&& !zipResources.contains(jarEntry.getName())) {
							zipResources.add(jarEntry.getName());
							ZipEntry ze = new ZipEntry(jarEntry.getName());
							InputStream is = file.getInputStream(jarEntry);
							byte[] content = new byte[is.available()];
							is.read(content);
							is.close();
							zos.putNextEntry(ze);
							zos.write(content);
							zos.closeEntry();
						}
					}
				}
			}
			zos.finish();
			zos.close();
			fos.close();
			return back;
		} catch (FileNotFoundException e) {
			LOG.error("Unknown: ", e);
		} catch (IOException e) {
			LOG.error("Unknown: ", e);
		}
		return null;
	}

	private boolean isFiltered(final String[] themePaths, final String filtered[], final String current) {
		if (themePaths != null) {
			boolean found = false;
			for (String themePath : themePaths) {
				if ("/".equals(themePath) || current.startsWith(themePath)) {
					found = true;
					break;
				}
			}
			if (!found) {
				// not found, filter it
				return true;
			}
		}
		for (String filter : filtered) {
			if (current.startsWith(filter)) {
				return true;
			}
		}
		for (String filter : ThemeConstants.ALLOWED_THEME_EXT) {
			if (current.endsWith(filter)) {
				return false;
			}
		}
		return true;
	}

	private String getZipPath(final Resource roots[], final Resource current) throws IOException {
		String currentPath = current.getURL().getPath();
		for (Resource root : roots) {
			String rootPath = root.getURL().getPath();
			if (currentPath.startsWith(rootPath)) {
				return StringUtils.removeStart(currentPath, rootPath);
			}
		}
		return null;
	}

	private ThemeBean getBeanFromInputStream(final String uuid, final InputStream fis) throws FileNotFoundException,
			IOException {
		Properties prop = new Properties();
		prop.load(fis);
		ThemeBean bean = new ThemeBean();
		bean.setUuid(uuid);
		bean.setAuthor(prop.getProperty("author.name"));
		bean.setPortalThemeVersion(prop.getProperty("portal.theme.version"));
		bean.setPortalVersion(prop.getProperty("portal.version"));
		bean.setTheme(prop.getProperty("theme.name"));
		bean.setUrl(prop.getProperty("author.url"));
		return bean;
	}

	public void setServletContext(final ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
