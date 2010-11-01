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
package org.devproof.portal.core.module.theme.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalApplication;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.module.common.component.InternalDownloadLink;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.theme.ThemeConstants;
import org.devproof.portal.core.module.theme.bean.ThemeBean;
import org.devproof.portal.core.module.theme.panel.UploadThemePanel;
import org.devproof.portal.core.module.theme.service.ThemeService;

import java.io.File;
import java.util.List;

/**
 * @author Carsten Hufe
 */
@ModulePage(mountPath = "/admin/themes", registerGlobalAdminLink = true)
public class ThemePage extends TemplatePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "configurationService")
    private ConfigurationService configurationService;
    @SpringBean(name = "themeService")
    private ThemeService themeService;

    private BubblePanel bubblePanel;

    public ThemePage(PageParameters params) {
        super(params);
        add(createBubblePanel());
        add(createRepeatingThemes());
        addPageAdminBoxLink(createUploadLink());
        addPageAdminBoxLink(createCompleteThemeDownloadLink());
        addPageAdminBoxLink(createSmallThemeDownloadLink());
    }

    private ListView<ThemeBean> createRepeatingThemes() {
        IModel<List<ThemeBean>> themeBeansModel = createThemeBeansModel();
        return new ListView<ThemeBean>("repeatingThemes", themeBeansModel) {
            private static final long serialVersionUID = -3440575235335312961L;

            @Override
            protected void populateItem(ListItem<ThemeBean> item) {
                IModel<ThemeBean> themeBeanModel = item.getModel();
                item.add(createThemeNameLabel(themeBeanModel));
                item.add(createThemeAuthorHomepageLink(themeBeanModel));
                item.add(createSelectionLink(themeBeanModel));
                item.add(createUninstallLink(themeBeanModel));
            }
        };
    }

    private IModel<List<ThemeBean>> createThemeBeansModel() {
        return new LoadableDetachableModel<List<ThemeBean>>() {
            private static final long serialVersionUID = 8637325307470287580L;

            @Override
            protected List<ThemeBean> load() {
                return themeService.findAllThemes();
            }
        };
    }

    private ExternalLink createThemeAuthorHomepageLink(IModel<ThemeBean> themeModel) {
        ThemeBean theme = themeModel.getObject();
        return new ExternalLink("authorLink", theme.getUrl(), theme.getAuthor());
    }

    private Label createThemeNameLabel(IModel<ThemeBean> themeModel) {
        IModel<String> themeNameModel = new PropertyModel<String>(themeModel, "theme");
        return new Label("theme", themeNameModel);
    }

    private Link<Void> createSelectionLink(final IModel<ThemeBean> themeModel) {
        Link<Void> selectLink = newSelectLink(themeModel);
        selectLink.add(createSelectLinkLabel(themeModel));
        return selectLink;
    }

    private Link<Void> newSelectLink(final IModel<ThemeBean> themeModel) {
        return new Link<Void>("selectLink") {
            private static final long serialVersionUID = -4733497680197408991L;

            @Override
            public void onClick() {
                ThemeBean theme = themeModel.getObject();
                themeService.selectTheme(theme);
                info(new StringResourceModel("msg.selected", this, null, new Object[]{theme.getTheme()}).getString());
                setTheme(theme);
            }

            private void setTheme(ThemeBean theme) {
                ((PortalApplication) getApplication()).setThemeUuid(theme.getUuid());
            }

            @Override
            public boolean isEnabled() {
                String selectedThemeUuid = configurationService.findAsString(ThemeConstants.CONF_SELECTED_THEME_UUID);
                ThemeBean theme = themeModel.getObject();
                return !selectedThemeUuid.equals(theme.getUuid());
            }
        };
    }

    private Label createSelectLinkLabel(IModel<ThemeBean> themeModel) {
        return new Label("selectLabel", createSelectedLinkLabelModel(themeModel));
    }

    private LoadableDetachableModel<String> createSelectedLinkLabelModel(final IModel<ThemeBean> themeModel) {
        return new LoadableDetachableModel<String>() {
            private static final long serialVersionUID = -5655831323982352436L;

            @Override
            protected String load() {
                String selectedThemeUuid = configurationService.findAsString(ThemeConstants.CONF_SELECTED_THEME_UUID);
                ThemeBean theme = themeModel.getObject();
                boolean selected = selectedThemeUuid.equals(theme.getUuid());
                return getString(selected ? "selectedLink" : "selectLink");
            }
        };
    }

    private Link<Void> createUninstallLink(final IModel<ThemeBean> themeModel) {
        return new Link<Void>("uninstallLink") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                ThemeBean theme = themeModel.getObject();
                themeService.uninstall(theme);
                info(new StringResourceModel("msg.uninstalled", this, null, new Object[]{theme.getTheme()}).getString());
                setDefaultTheme();
            }

            private void setDefaultTheme() {
                ((PortalApplication) getApplication()).setThemeUuid(ThemeConstants.CONF_SELECTED_THEME_DEFAULT);
            }

            @Override
            public boolean isVisible() {
                ThemeBean theme = themeModel.getObject();
                return !ThemeConstants.CONF_SELECTED_THEME_DEFAULT.equals(theme.getUuid());
            }
        };
    }

    private InternalDownloadLink createSmallThemeDownloadLink() {
        // Download link for small default theme
        InternalDownloadLink smallTheme = newSmallThemeDownloadLink();
        smallTheme.add(createSmallThemeDownloadLinkLabel());
        return smallTheme;
    }

    private Label createSmallThemeDownloadLinkLabel() {
        return new Label(getPageAdminBoxLinkLabelId(), getString("smallThemeLink"));
    }

    private InternalDownloadLink newSmallThemeDownloadLink() {
        return new InternalDownloadLink(getPageAdminBoxLinkId()) {
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
        return new Label(getPageAdminBoxLinkLabelId(), getString("completeThemeLink"));
    }

    private InternalDownloadLink newCompleteThemeDownloadLink() {
        return new InternalDownloadLink(getPageAdminBoxLinkId()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected File getFile() {
                return themeService.createCompleteDefaultTheme();
            }
        };
    }

    private AjaxLink<BubblePanel> createUploadLink() {
        AjaxLink<BubblePanel> uploadLink = newUploadLink();
        uploadLink.add(createUploadLinkLabel());
        return uploadLink;
    }

    private Label createUploadLinkLabel() {
        return new Label(getPageAdminBoxLinkLabelId(), getString("uploadLink"));
    }

    private AjaxLink<BubblePanel> newUploadLink() {
        return new AjaxLink<BubblePanel>(getPageAdminBoxLinkId()) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                bubblePanel.setContent(uploadThemePanel());
                bubblePanel.showModal(target);
            }

            private UploadThemePanel uploadThemePanel() {
                return new UploadThemePanel(bubblePanel.getContentId()) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onSubmit() {
                    }

                    @Override
                    public void onCancel(AjaxRequestTarget target) {
                        bubblePanel.hide(target);
                    }
                };
            }
        };
    }

    private BubblePanel createBubblePanel() {
        bubblePanel = new BubblePanel("bubblePanel");
        return bubblePanel;
    }
}
