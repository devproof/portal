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
package org.devproof.portal.core.module.theme.page;

import java.io.File;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.PageCreator;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.WindowClosedCallback;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalApplication;
import org.devproof.portal.core.module.common.component.InternalDownloadLink;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.theme.ThemeConstants;
import org.devproof.portal.core.module.theme.bean.ThemeBean;
import org.devproof.portal.core.module.theme.service.ThemeService;

/**
 * @author Carsten Hufe
 */
public class ThemePage extends TemplatePage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;
	@SpringBean(name = "themeService")
	private ThemeService themeService;

	private RepeatingView themeRepeater;
	private ModalWindow modalWindow;

	public ThemePage(PageParameters params) {
		super(params);
		add(createModalWindow());
		add(createThemeRepeater());
		addPageAdminBoxLink(createUploadLink());
		addPageAdminBoxLink(createCompleteThemeDownloadLink());
		addPageAdminBoxLink(createSmallThemeDownloadLink());
	}

	private void reloadThemeRepeater() {
		ThemePage.this.replace(createThemeRepeater());
	}

	private RepeatingView createThemeRepeater() {
		themeRepeater = new RepeatingView("tableRow");
		List<ThemeBean> themes = themeService.findAllThemes();
		for (ThemeBean theme : themes) {
			themeRepeater.add(createThemeRow(theme));
		}
		return themeRepeater;
	}

	private WebMarkupContainer createThemeRow(ThemeBean theme) {
		WebMarkupContainer row = new WebMarkupContainer(themeRepeater.newChildId());
		row.add(createThemeNameLabel(theme));
		row.add(createThemeAuthorHomepageLink(theme));
		row.add(createSelectionLink(theme));
		row.add(createUninstallLink(theme));
		return row;
	}

	private ExternalLink createThemeAuthorHomepageLink(ThemeBean theme) {
		return new ExternalLink("authorLink", theme.getUrl(), theme.getAuthor());
	}

	private Label createThemeNameLabel(ThemeBean theme) {
		return new Label("theme", theme.getTheme());
	}

	private Link<Void> createSelectionLink(ThemeBean theme) {
		String selectedThemeUuid = configurationService.findAsString(ThemeConstants.CONF_SELECTED_THEME_UUID);
		boolean selected = selectedThemeUuid.equals(theme.getUuid());
		String key = selected ? "selectedLink" : "selectLink";
		Link<Void> selectLink = newSelectionTheme(theme);
		selectLink.setEnabled(!selected);
		selectLink.add(new Label("selectLabel", getString(key)));
		return selectLink;
	}

	private Link<Void> newSelectionTheme(final ThemeBean theme) {
		return new Link<Void>("selectLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				themeService.selectTheme(theme);
				info(new StringResourceModel("msg.selected", this, null, new Object[] { theme.getTheme() }).getString());
				setTheme(theme);
				reloadThemeRepeater();
			}

			private void setTheme(ThemeBean theme) {
				((PortalApplication) getApplication()).setThemeUuid(theme.getUuid());
			}
		};
	}

	private Link<Void> createUninstallLink(ThemeBean theme) {
		Link<Void> uninstallLink = newUninstallLink(theme);
		uninstallLink.setVisible(!ThemeConstants.CONF_SELECTED_THEME_DEFAULT.equals(theme.getUuid()));
		return uninstallLink;
	}

	private Link<Void> newUninstallLink(final ThemeBean theme) {
		Link<Void> uninstallLink = new Link<Void>("uninstallLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				themeService.uninstall(theme);
				info(new StringResourceModel("msg.uninstalled", this, null, new Object[] { theme.getTheme() })
						.getString());
				setDefaultTheme();
				reloadThemeRepeater();
			}

			private void setDefaultTheme() {
				((PortalApplication) getApplication()).setThemeUuid(ThemeConstants.CONF_SELECTED_THEME_DEFAULT);
			}
		};
		return uninstallLink;
	}

	private InternalDownloadLink createSmallThemeDownloadLink() {
		// Download link for small default theme
		InternalDownloadLink smallTheme = newSmallThemeDownloadLink();
		smallTheme.add(createSmallThemeDownloadLinkLabel());
		return smallTheme;
	}

	private Label createSmallThemeDownloadLinkLabel() {
		return new Label("linkName", getString("smallThemeLink"));
	}

	private InternalDownloadLink newSmallThemeDownloadLink() {
		return new InternalDownloadLink("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			protected File getFile() {
				return themeService.createSmallDefaultTheme();
			}
		};
	}

	private InternalDownloadLink createCompleteThemeDownloadLink() {
		InternalDownloadLink completeTheme = newCompleteThemeDownloadLink();
		completeTheme.add(createCompleteThemeDownloadLinkLabel());
		return completeTheme;
	}

	private Label createCompleteThemeDownloadLinkLabel() {
		return new Label("linkName", getString("completeThemeLink"));
	}

	private InternalDownloadLink newCompleteThemeDownloadLink() {
		return new InternalDownloadLink("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			protected File getFile() {
				return themeService.createCompleteDefaultTheme();
			}
		};
	}

	private AjaxLink<ModalWindow> createUploadLink() {
		AjaxLink<ModalWindow> uploadLink = newUploadLink();
		uploadLink.add(createUploadLinkLabel());
		return uploadLink;
	}

	private Label createUploadLinkLabel() {
		return new Label("linkName", getString("uploadLink"));
	}

	private AjaxLink<ModalWindow> newUploadLink() {
		return new AjaxLink<ModalWindow>("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				modalWindow.setInitialHeight(250);
				modalWindow.setInitialWidth(600);
				modalWindow.setPageCreator(createUploadThemePageCreator());
				modalWindow.setWindowClosedCallback(createWindowCloseCallback());
				modalWindow.show(target);
			}

			private PageCreator createUploadThemePageCreator() {
				return new ModalWindow.PageCreator() {
					private static final long serialVersionUID = 1L;

					public Page createPage() {
						return new UploadThemePage();
					}
				};
			}

			private WindowClosedCallback createWindowCloseCallback() {
				return new ModalWindow.WindowClosedCallback() {
					private static final long serialVersionUID = 1L;

					public void onClose(AjaxRequestTarget target) {
						reloadThemeRepeater();
						target.addComponent(ThemePage.this);
					}
				};
			}
		};
	}

	private ModalWindow createModalWindow() {
		modalWindow = new ModalWindow("modalWindow");
		modalWindow.setTitle("Portal");
		return modalWindow;
	}
	/*
	 * public void build() { tableRow = new RepeatingView("tableRow");
	 * List<ThemeBean> themes = themeService.findAllThemes(); String
	 * selectedThemeUuid =
	 * configurationService.findAsString(ThemeConstants.CONF_SELECTED_THEME_UUID
	 * ); for (final ThemeBean theme : themes) { boolean selected =
	 * selectedThemeUuid.equals(theme.getUuid()); String key = selected ?
	 * "selectedLink" : "selectLink"; WebMarkupContainer row = new
	 * WebMarkupContainer(tableRow.newChildId()); row.add(new Label("theme",
	 * theme.getTheme())); row.add(new ExternalLink("authorLink",
	 * theme.getUrl(), theme.getAuthor())); Link<Void> selectLink = new
	 * Link<Void>("selectLink") { private static final long serialVersionUID =
	 * 1L;
	 * 
	 * @Override public void onClick() { themeService.selectTheme(theme);
	 * info(new StringResourceModel("msg.selected", this, null, new Object[] {
	 * theme.getTheme() }) .getString()); ((PortalApplication)
	 * getApplication()).setThemeUuid(theme.getUuid()); //
	 * ThemePage.this.build(); tableRow.renderComponent(); } };
	 * selectLink.setEnabled(!selected); selectLink.add(new Label("selectLabel",
	 * getString(key))); row.add(selectLink);
	 * 
	 * row.add(new Link<Void>("uninstallLink") { private static final long
	 * serialVersionUID = 1L;
	 * 
	 * @Override public void onClick() { themeService.uninstall(theme); info(new
	 * StringResourceModel("msg.uninstalled", this, null, new Object[] {
	 * theme.getTheme() }) .getString()); ((PortalApplication)
	 * getApplication()).
	 * setThemeUuid(ThemeConstants.CONF_SELECTED_THEME_DEFAULT); //
	 * ThemePage.this.build(); tableRow.renderComponent(); }
	 * }.setVisible(!ThemeConstants
	 * .CONF_SELECTED_THEME_DEFAULT.equals(theme.getUuid())));
	 * tableRow.add(row); } // tableRow.renderComponent();
	 * addOrReplace(tableRow); }
	 */
}
