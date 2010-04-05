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
package org.devproof.portal.core.module.common.page;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.resource.loader.ClassStringResourceLoader;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.box.entity.BoxEntity;
import org.devproof.portal.core.module.box.panel.BoxTitleVisibility;
import org.devproof.portal.core.module.box.registry.BoxRegistry;
import org.devproof.portal.core.module.box.service.BoxService;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.factory.CommonMarkupContainerFactory;
import org.devproof.portal.core.module.common.panel.GlobalAdminBoxPanel;
import org.devproof.portal.core.module.common.panel.OtherBoxPanel;
import org.devproof.portal.core.module.common.panel.PageAdminBoxPanel;
import org.devproof.portal.core.module.common.panel.SearchBoxPanel;
import org.devproof.portal.core.module.common.registry.MainNavigationRegistry;
import org.devproof.portal.core.module.common.registry.SharedRegistry;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.feed.component.Atom1Link;
import org.devproof.portal.core.module.feed.component.Rss2Link;
import org.devproof.portal.core.module.feed.panel.FeedBoxPanel;
import org.devproof.portal.core.module.tag.entity.BaseTagEntity;
import org.devproof.portal.core.module.tag.panel.TagCloudBoxPanel;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.core.module.user.page.LoginPage;
import org.devproof.portal.core.module.user.panel.LoginBoxPanel;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Base class for all pages.
 *
 * @author Carsten Hufe
 */
public abstract class TemplatePage extends WebPage {

    @SpringBean(name = "configurationService")
    private ConfigurationService configurationService;
    @SpringBean(name = "boxService")
    private BoxService boxService;
    @SpringBean(name = "mainNavigationRegistry")
    private MainNavigationRegistry mainNavigationRegistry;
    @SpringBean(name = "boxRegistry")
    private BoxRegistry boxRegistry;
    @SpringBean(name = "sharedRegistry")
    private SharedRegistry sharedRegistry;

    private FeedbackPanel feedback;
    private Component filterBox;
    private Component tagCloudBox;
    private PageAdminBoxPanel pageAdminBox;
    private boolean filterBoxHideTitle;
    private boolean tagCloudBoxHideTitle;
    private PageParameters params;

    public TemplatePage(PageParameters params) {
        this.params = params;
        add(createDefaultCSSHeaderContributor());
        add(createBodyCSSHeaderContributor());
        add(createRss2LinkReference());
        add(createAtom1LinkReference());
        add(createPageTitleLabel());
        add(createFeedbackPanel());
        add(createGoogleAnalyticsPart1());
        add(createGoogleAnalyticsPart2());
        add(createFooterLink());
        add(createCopyrightLabel());
        add(createRepeatingMainNavigation());
        add(createRepeatingBoxes());
        setOutputMarkupId(true);
    }

    @Override
    protected void onBeforeRender() {
        handleInfoMessageParams();
        handleErrorMessageParams();
        super.onBeforeRender();
    }

    private WebMarkupContainer createCopyrightLabel() {
        WebMarkupContainer copyright = new WebMarkupContainer("copyright");
        copyright.add(new SimpleAttributeModifier("content", configurationService.findAsString(CommonConstants.CONF_COPYRIGHT_OWNER)));
        return copyright;
    }

    private Label createPageTitleLabel() {
        IModel<String> pageTitleModel = createPageTitleModel();
        return new Label("pageTitle", pageTitleModel);
    }

    private IModel<String> createPageTitleModel() {
        return new LoadableDetachableModel<String>() {
            private static final long serialVersionUID = 2389565755564804646L;

            @Override
            protected String load() {
                String localPageTitle = getPageTitle();
                if (StringUtils.isBlank(localPageTitle)) {
                    localPageTitle = getString("contentTitle", null, "");
                }
                if (StringUtils.isNotEmpty(localPageTitle)) {
                    localPageTitle += " - ";
                }
                return localPageTitle + configurationService.findAsString(CommonConstants.CONF_PAGE_TITLE);
            }
        };
    }

    private WebComponent createGoogleAnalyticsPart2() {
        boolean googleEnabled = configurationService.findAsBoolean(CommonConstants.CONF_GOOGLE_ANALYTICS_ENABLED);
        WebComponent googleAnalytics2 = newGoogleAnalyticsPart2();
        googleAnalytics2.setVisible(googleEnabled);
        return googleAnalytics2;
    }

    private WebComponent newGoogleAnalyticsPart2() {
        return new WebComponent("googleAnalytics2") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
                StringBuilder buf = new StringBuilder();
                buf.append("try {\n");
                buf.append("var pageTracker = _gat._getTracker(\"");
                buf.append(configurationService.findAsString(CommonConstants.CONF_GOOGLE_WEBPROPERTY_ID));
                buf.append("\");\n");
                buf.append("pageTracker._trackPageview();");
                buf.append("} catch(err) {}");
                replaceComponentTagBody(markupStream, openTag, buf.toString());
            }
        };
    }

    private WebMarkupContainer createGoogleAnalyticsPart1() {
        boolean googleEnabled = configurationService.findAsBoolean(CommonConstants.CONF_GOOGLE_ANALYTICS_ENABLED);
        WebMarkupContainer googleAnalytics1 = new WebMarkupContainer("googleAnalytics1");
        googleAnalytics1.setVisible(googleEnabled);
        return googleAnalytics1;
    }

    private FeedbackPanel createFeedbackPanel() {
        feedback = new FeedbackPanel("feedbackPanel");
        feedback.setOutputMarkupId(true);
        return feedback;
    }

    private void handleErrorMessageParams() {
        if (params.containsKey("errorMsg")) {
            info(params.getString("errorMsg"));
        }
    }

    private void handleInfoMessageParams() {
        if (params.containsKey("infoMsg")) {
            info(params.getString("infoMsg"));
        }
    }

    private MarkupContainer createFooterLink() {
        String footerContent = configurationService.findAsString("footer_content");
        CommonMarkupContainerFactory factory = sharedRegistry.getResource("footerLink");
        MarkupContainer footerLink;
        if (factory != null) {
            footerLink = factory.newInstance("footerLink");
        } else {
            footerLink = new WebMarkupContainer("footerLink");
        }
        footerLink.add(new Label("footerLabel", footerContent).setEscapeModelStrings(false));
        return footerLink;
    }

    private HeaderContributor createBodyCSSHeaderContributor() {
        return CSSPackageResource.getHeaderContribution(CommonConstants.class, "css/body.css");
    }

    private HeaderContributor createDefaultCSSHeaderContributor() {
        return CSSPackageResource.getHeaderContribution(CommonConstants.class, "css/default.css");
    }

    private Atom1Link createAtom1LinkReference() {
        return new Atom1Link("atom1reference", getClass());
    }

    private Rss2Link createRss2LinkReference() {
        return new Rss2Link("rss2reference", getClass());
    }

    /**
     * Adds the Css and JavaScript of the SyntaxHighligher to the page
     */
    protected void addSyntaxHighlighter() {
        String theme = configurationService.findAsString(CommonConstants.CONF_SYNTAXHL_THEME);
        PortalUtil.addSyntaxHightlighter(this, theme);
    }

    /**
     * creates the Main Navigation on the top
     */
    private RepeatingView createRepeatingMainNavigation() {
        RepeatingView repeating = new RepeatingView("repeatingMainNav");
        List<Class<? extends Page>> registeredPages = mainNavigationRegistry.getRegisteredPages();
        for (Class<? extends Page> pageClass : registeredPages) {
            repeating.add(createMenuItem(repeating.newChildId(), pageClass));
        }
        return repeating;
    }

    private WebMarkupContainer createMenuItem(String id, Class<? extends Page> pageClass) {
        WebMarkupContainer item = new WebMarkupContainer(id);
        item.add(createMenuLink(pageClass));
        return item;
    }

    private BookmarkablePageLink<Void> createMenuLink(Class<? extends Page> pageClass) {
        BookmarkablePageLink<Void> link = new BookmarkablePageLink<Void>("mainNavigationLink", pageClass);
        link.add(createMenuLinkLabel(pageClass));
        return link;
    }

    private Label createMenuLinkLabel(Class<? extends Page> pageClass) {
        String label = new ClassStringResourceLoader(pageClass).loadStringResource(null, CommonConstants.MAIN_NAVIGATION_LINK_LABEL);
        if (StringUtils.isEmpty(label)) {
            label = new ClassStringResourceLoader(pageClass).loadStringResource(null, CommonConstants.CONTENT_TITLE_LABEL);
        }
        return new Label("mainNavigationLinkLabel", label);
    }

    /**
     * Create the boxes on the right hand side
     */
    private RepeatingView createRepeatingBoxes() {
        RepeatingView repeating = new RepeatingView("repeatingSideNav");
        List<BoxEntity> boxes = boxService.findAllOrderedBySort();
        for (BoxEntity box : boxes) {
            repeating.add(createBoxItem(repeating.newChildId(), box));
        }
        return repeating;
    }

    private WebMarkupContainer createBoxItem(String id, BoxEntity box) {
        WebMarkupContainer item = new WebMarkupContainer(id);
        Class<? extends Component> boxClazz = boxRegistry.getClassBySimpleClassName(box.getBoxType());
        Component boxInstance = null;
        if (boxClazz == null) {
            item.add(boxInstance = createBoxNotFoundPanel());
        } else if (boxClazz.isAssignableFrom(SearchBoxPanel.class)) {
            if (filterBox == null) {
                item.add(filterBox = createEmptyFilterBox());
                filterBoxHideTitle = box.getHideTitle();
            }
        } else if (boxClazz.isAssignableFrom(OtherBoxPanel.class)) {
            item.add(boxInstance = createOtherBox(box));
        } else if (boxClazz.isAssignableFrom(PageAdminBoxPanel.class)) {
            item.add(pageAdminBox = createPageAdminBox());
            boxInstance = pageAdminBox;
        } else if (boxClazz.isAssignableFrom(LoginBoxPanel.class)) {
            item.add(boxInstance = createLoginBox());
            item.setVisible(isNotLoginPage());
        } else if (boxClazz.isAssignableFrom(TagCloudBoxPanel.class)) {
            item.add(tagCloudBox = createEmptyFilterBox());
            tagCloudBoxHideTitle = box.getHideTitle();
            boxInstance = tagCloudBox;
        } else if (boxClazz.isAssignableFrom(GlobalAdminBoxPanel.class)) {
            item.add(boxInstance = new GlobalAdminBoxPanel("box"));
        } else if (boxClazz.isAssignableFrom(FeedBoxPanel.class)) {
            item.add(boxInstance = new FeedBoxPanel("box", getPageClass()));
        } else {
            boxInstance = createGenericBoxInstance(boxClazz);
            if (boxInstance == null) {
                throw new IllegalArgumentException("The box class " + boxClazz + " does not have a default constructor with a String id parameter or String and PageParameters");
            } else {
                item.add(boxInstance);
            }
        }

        setBoxTitleVisibility(box, boxInstance);
        return item;
    }

    private Component createEmptyFilterBox() {
        return new WebMarkupContainer("box").setVisible(false);
    }

    private Component createGenericBoxInstance(Class<? extends Component> boxClazz) {
        for (Constructor<?> constr : boxClazz.getConstructors()) {
            Class<?> param[] = constr.getParameterTypes();
            if (param.length == 2 && param[0] == String.class && param[1] == PageParameters.class) {
                try {
                    return (Component) constr.newInstance("box", params);
                } catch (Exception e) {
                    throw new UnhandledException(e);
                }
            } else if (param.length == 1 && param[0] == String.class) {
                try {
                    return (Component) constr.newInstance("box");
                } catch (Exception e) {
                    throw new UnhandledException(e);
                }
            }
        }
        return null;
    }

    private void setBoxTitleVisibility(BoxEntity box, Component boxInstance) {
        if (boxInstance instanceof BoxTitleVisibility) {
            ((BoxTitleVisibility) boxInstance).setTitleVisible(!box.getHideTitle());
        }
    }

    private boolean isNotLoginPage() {
        return !(this instanceof LoginPage);
    }

    private LoginBoxPanel createLoginBox() {
        return new LoginBoxPanel("box", params);
    }

    private PageAdminBoxPanel createPageAdminBox() {
        return new PageAdminBoxPanel("box");
    }

    private OtherBoxPanel createOtherBox(BoxEntity box) {
        return new OtherBoxPanel("box", Model.of(box));
    }

    private Component createBoxNotFoundPanel() {
        Component boxInstance;
        BoxEntity error = new BoxEntity();
        error.setTitle("!Error!");
        error.setContent("Box type is not available!");
        boxInstance = createOtherBox(error);
        return boxInstance;
    }

    /**
     * Set the Filter Box e.g. search or tags
     */
    public void addFilterBox(Panel filterBox) {
        if (this.filterBox != null) {
            if (filterBox instanceof BoxTitleVisibility) {
                ((BoxTitleVisibility) filterBox).setTitleVisible(!filterBoxHideTitle);
            }
            this.filterBox.replaceWith(filterBox);
            this.filterBox = filterBox;
        }
    }

    /**
     * Set the TagCloud Box e.g. search or tags
     */
    public <T extends BaseTagEntity<?>> void addTagCloudBox(TagService<T> tagService, Class<? extends Page> page) {
        TagCloudBoxPanel<?> newTagCloudBox = new TagCloudBoxPanel<T>("box", tagService, page);
        newTagCloudBox.setTitleVisible(!tagCloudBoxHideTitle);
        tagCloudBox.replaceWith(newTagCloudBox);
        tagCloudBox = newTagCloudBox;
    }

    /**
     * Add a link to the page admin panel
     */
    public void addPageAdminBoxLink(Component link) {
        if (pageAdminBox != null) {
            pageAdminBox.addLink(link);
        }
    }

    public String getPageAdminBoxLinkId() {
        return "adminLink";
    }

    public String getPageAdminBoxLinkLabelId() {
        return "linkName";
    }

    public String getBoxId() {
        return "box";
    }

    public String getPageTitle() {
        return "";
    }
//
//	/**
//	 * Change the page title
//	 */
//	public void setPageTitle(IModel<String> title) {
//		pageTitle.setObject(title + " - " + configurationService.findAsString(CommonConstants.CONF_PAGE_TITLE));
//	}

    public FeedbackPanel getFeedback() {
        return feedback;
    }

    public String getRequestURL() {
        StringBuffer url = getWebRequestCycle().getWebRequest().getHttpServletRequest().getRequestURL();
        return url.toString();
    }
}
