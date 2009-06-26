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
package org.devproof.portal.core.module.theme.page;

import java.io.File;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
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
	private transient ConfigurationService configurationService;
	@SpringBean(name = "themeService")
	private transient ThemeService themeService;

	public ThemePage(final PageParameters params) {
		super(params);
		build();
		final ModalWindow modalWindow = new ModalWindow("modalWindow");
		modalWindow.setTitle("Portal");
		this.add(modalWindow);
		// upload files link
		AjaxLink<ModalWindow> uploadLink = new AjaxLink<ModalWindow>("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				modalWindow.setInitialHeight(250);
				modalWindow.setInitialWidth(600);

				modalWindow.setPageCreator(new ModalWindow.PageCreator() {
					private static final long serialVersionUID = 1L;

					public Page createPage() {
						return new UploadThemePage();
					}
				});
				modalWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
					private static final long serialVersionUID = 1L;

					public void onClose(final AjaxRequestTarget target) {
						ThemePage.this.build();
						target.addComponent(ThemePage.this);
					}
				});
				modalWindow.show(target);
			}
		};
		uploadLink.add(new Label("linkName", this.getString("uploadLink")));
		addPageAdminBoxLink(uploadLink);

		// Download link for complete default theme
		InternalDownloadLink completeTheme = new InternalDownloadLink("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			protected File getFile() {
				return ThemePage.this.themeService.createCompleteDefaultTheme();
			}
		};
		completeTheme.add(new Label("linkName", this.getString("completeThemeLink")));
		addPageAdminBoxLink(completeTheme);
		// Download link for small default theme
		InternalDownloadLink smallTheme = new InternalDownloadLink("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			protected File getFile() {
				return ThemePage.this.themeService.createSmallDefaultTheme();
			}
		};
		smallTheme.add(new Label("linkName", this.getString("smallThemeLink")));
		addPageAdminBoxLink(smallTheme);
	}

	public void build() {
		RepeatingView tableRow = new RepeatingView("tableRow");
		addOrReplace(tableRow);
		List<ThemeBean> themes = this.themeService.findAllThemes();
		String selectedThemeUuid = this.configurationService.findAsString(ThemeConstants.CONF_SELECTED_THEME_UUID);
		for (final ThemeBean theme : themes) {
			boolean selected = selectedThemeUuid.equals(theme.getUuid());
			String key = selected ? "selectedLink" : "selectLink";
			WebMarkupContainer row = new WebMarkupContainer(tableRow.newChildId());
			row.add(new Label("theme", theme.getTheme()));
			row.add(new ExternalLink("authorLink", theme.getUrl(), theme.getAuthor()));
			Link<Void> selectLink = new Link<Void>("selectLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick() {
					ThemePage.this.themeService.selectTheme(theme);
					info(new StringResourceModel("msg.selected", this, null, new Object[] { theme.getTheme() }).getString());
					((PortalApplication) getApplication()).setThemeUuid(theme.getUuid());
					ThemePage.this.build();
				}
			};
			selectLink.setEnabled(!selected);
			selectLink.add(new Label("selectLabel", this.getString(key)));
			row.add(selectLink);

			row.add(new Link<Void>("uninstallLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick() {
					ThemePage.this.themeService.uninstall(theme);
					info(new StringResourceModel("msg.uninstalled", this, null, new Object[] { theme.getTheme() }).getString());
					((PortalApplication) getApplication()).setThemeUuid(ThemeConstants.CONF_SELECTED_THEME_DEFAULT);
					ThemePage.this.build();
				}
			}.setVisible(!ThemeConstants.CONF_SELECTED_THEME_DEFAULT.equals(theme.getUuid())));
			tableRow.add(row);
		}
	}
}
