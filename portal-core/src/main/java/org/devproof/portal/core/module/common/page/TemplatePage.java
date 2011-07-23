/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.*;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.resource.loader.ClassStringResourceLoader;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;
import org.devproof.portal.core.app.PortalApplication;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.box.entity.Box;
import org.devproof.portal.core.module.box.panel.BoxTitleVisibility;
import org.devproof.portal.core.module.box.registry.BoxRegistry;
import org.devproof.portal.core.module.box.service.BoxService;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.component.PortalFeedbackPanel;
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
import org.devproof.portal.core.module.tag.panel.TagCloudBoxPanel;
import org.devproof.portal.core.module.user.entity.User;
import org.devproof.portal.core.module.user.page.LoginPage;
import org.devproof.portal.core.module.user.page.RegisterPage;
import org.devproof.portal.core.module.user.page.SettingsPage;
import org.devproof.portal.core.module.user.panel.LoginBoxPanel;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for all pages.
 *
 * @author Carsten Hufe
 */
public abstract class TemplatePage extends WebPage {

    private static final long serialVersionUID = -2077311506052517540L;
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
    private PageParameters params;

    public TemplatePage(PageParameters params) {
        this.params = params;
        add(createRss2LinkReference());
        add(createAtom1LinkReference());
        add(createLoginMessageLabel());
        add(createLoginLink());
        add(createRegisterLink());
        add(createSettingsLink());
        add(createLogoutLink());
        add(createPageTitleLabel());
        add(createFeedbackPanel());
        add(createGoogleAnalytics());
        add(createFooterLink());
        add(createMetaCopyrightLabel());
        add(createMetaRevisit());
        add(createMetaRobots());
        add(createRepeatingMainNavigation());
        add(createRepeatingBoxes());
        add(createSessionKeepAliveBehaviour());
        setOutputMarkupId(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderJavaScriptReference(new PackageResourceReference(CommonConstants.class, "css/body.css"));
        response.renderJavaScriptReference(new PackageResourceReference(CommonConstants.class, "css/default.css"));
    }

    private AbstractAjaxTimerBehavior createSessionKeepAliveBehaviour() {
        // 9 minutes session alive, so there are two chances to hit with 4 minutes
        return new AbstractAjaxTimerBehavior(Duration.minutes(4)) {
            private static final long serialVersionUID = -12307329320284540L;

            @Override
            protected void onTimer(AjaxRequestTarget target) {
                // Do nothing, just keep session alive
            }
        };
    }

    private Label createLoginMessageLabel() {
        return new Label("loginMessage", createLoginMessageModel());
    }

    private LoadableDetachableModel<String> createLoginMessageModel() {
        return new LoadableDetachableModel<String>() {

            private static final long serialVersionUID = 4418736008117489964L;

            @Override
            protected String load() {
                PortalSession session = PortalSession.get();
                if (session.isSignedIn()) {
                    return getString("loggedin", new PropertyModel<User>(session, "user"));
                }
                return getString("notloggedin");
            }
        };
    }

    private Link<Void> createLogoutLink() {
        return new Link<Void>("logoutLink") {
            private static final long serialVersionUID = 4418736008117489964L;

            @Override
            public void onClick() {
                PortalSession.get().logoutUser();
                info(getString("loggedout"));
                setResponsePage(PortalApplication.get().getHomePage());
            }

            @Override
            public boolean isVisible() {
                return PortalSession.get().isSignedIn();
            }
        };
    }

    private BookmarkablePageLink<SettingsPage> createSettingsLink() {
        return new BookmarkablePageLink<SettingsPage>("settingsLink", SettingsPage.class) {

            private static final long serialVersionUID = -6899608689638446592L;

            @Override
            public boolean isVisible() {
                return PortalSession.get().isSignedIn();
            }
        };
    }

    private BookmarkablePageLink<LoginPage> createRegisterLink() {
        return new BookmarkablePageLink<LoginPage>("registerLink", RegisterPage.class) {

            private static final long serialVersionUID = -554743907998591164L;

            @Override
            public boolean isVisible() {
                return !PortalSession.get().isSignedIn();
            }
        };
    }

    private BookmarkablePageLink<LoginPage> createLoginLink() {
        return new BookmarkablePageLink<LoginPage>("loginLink", LoginPage.class) {
            private static final long serialVersionUID = -5974189785426103270L;

            @Override
            public boolean isVisible() {
                return !PortalSession.get().isSignedIn();
            }
        };
    }

    @Override
    protected void onBeforeRender() {
        handleInfoMessageParams();
        handleErrorMessageParams();
        super.onBeforeRender();
    }

    private WebMarkupContainer createMetaCopyrightLabel() {
        WebMarkupContainer copyright = new WebMarkupContainer("metaCopyright");
        copyright.add(new SimpleAttributeModifier("content", configurationService.findAsString(CommonConstants.CONF_COPYRIGHT_OWNER)));
        return copyright;
    }

    private WebMarkupContainer createMetaRevisit() {
        WebMarkupContainer copyright = new WebMarkupContainer("metaRevisit");
        copyright.add(new SimpleAttributeModifier("content", getRevisitAfter()));
        return copyright;
    }

    protected String getRevisitAfter() {
        return getString("head.revisit");
    }

    private WebMarkupContainer createMetaRobots() {
        WebMarkupContainer copyright = new WebMarkupContainer("metaRobots");
        copyright.add(new SimpleAttributeModifier("content", getRobots()));
        return copyright;
    }

    protected String getRobots() {
        return getString("head.robots");
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

    private WebComponent createGoogleAnalytics() {
        boolean googleEnabled = configurationService.findAsBoolean(CommonConstants.CONF_GOOGLE_ANALYTICS_ENABLED);
        WebComponent googleAnalytics2 = newGoogleAnalytics();
        googleAnalytics2.setVisible(googleEnabled);
        return googleAnalytics2;
    }

    private WebComponent newGoogleAnalytics() {
        return new WebComponent("googleAnalytics") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
                String googleAnalytics = "  var _gaq = _gaq || [];\n" + "  _gaq.push(['_setAccount', '$WEBPROPERTYID']);\n" + "  _gaq.push(['_trackPageview']);\n" + "\n" + "  (function() {\n" + "    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\n" + "    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\n" + "    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\n" + "  })();";
                googleAnalytics = StringUtils.replace(googleAnalytics, "$WEBPROPERTYID", configurationService.findAsString(CommonConstants.CONF_GOOGLE_WEBPROPERTY_ID));
                replaceComponentTagBody(markupStream, openTag, googleAnalytics);
            }
        };
    }

    private FeedbackPanel createFeedbackPanel() {
        feedback = new PortalFeedbackPanel("feedbackPanel");
        feedback.setOutputMarkupId(true);
        return feedback;
    }

    private void handleErrorMessageParams() {
        if (params.getNamedKeys().contains("errorMsg")) {
            info(params.get("errorMsg"));
        }
    }

    private void handleInfoMessageParams() {
        if (params.getNamedKeys().contains("infoMsg")) {
            info(params.get("infoMsg"));
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

    private Atom1Link createAtom1LinkReference() {
        return new Atom1Link("atom1reference", getClass());
    }

    private Rss2Link createRss2LinkReference() {
        return new Rss2Link("rss2reference", getClass());
    }

    /**
     * Adds the Css and JavaScript of the SyntaxHighligher to the page
     */
    protected void addSyntaxHighlighter(IHeaderResponse response) {
        String theme = configurationService.findAsString(CommonConstants.CONF_SYNTAXHL_THEME);
        PortalUtil.addSyntaxHightlighter(response, theme);
    }

    /**
     * creates the Main Navigation on the top
     *
     * @return component
     */
    private Component createRepeatingMainNavigation() {
        IModel<List<Class<? extends Page>>> mainNavigationModel = createMainNavigationModel();
        return new ListView<Class<? extends Page>>("repeatingMainNav", mainNavigationModel) {
            private static final long serialVersionUID = 2629038838888554746L;

            @Override
            protected void populateItem(ListItem<Class<? extends Page>> item) {
                item.add(createMenuLink(item));
            }
        };
    }

    private IModel<List<Class<? extends Page>>> createMainNavigationModel() {
        return new LoadableDetachableModel<List<Class<? extends Page>>>() {
            private static final long serialVersionUID = -164465304408431579L;

            @Override
            protected List<Class<? extends Page>> load() {
                return mainNavigationRegistry.getRegisteredPages();
            }
        };
    }

    private BookmarkablePageLink<Void> createMenuLink(ListItem<Class<? extends Page>> item) {
        Class<? extends Page> pageClazz = item.getModelObject();
        BookmarkablePageLink<Void> link = new BookmarkablePageLink<Void>("mainNavigationLink", pageClazz);
        link.add(createMainNavigationClassModifier(item));
        link.add(createMenuLinkLabel(pageClazz));
        return link;
    }

    private AttributeModifier createMainNavigationClassModifier(ListItem<Class<? extends Page>> item) {
        return new AttributeModifier("class", true, createMainNavigationClassModifierModel(item));
    }

    private AbstractReadOnlyModel<String> createMainNavigationClassModifierModel(final ListItem<Class<? extends Page>> item) {
        return new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = -8849392135409571179L;

            @Override
            public String getObject() {
                if (item.getModelObject().equals(getPage().getClass())) {
                    return "topNavAct";
                } else if (isLastItem(item)) {
                    return "topNavLast";
                }
                return "";
            }
        };
    }

    private boolean isLastItem(ListItem<Class<? extends Page>> item) {
        return item.size() == (item.getIndex() - 1);
    }

    private Label createMenuLinkLabel(Class<? extends Page> pageClass) {
        ClassStringResourceLoader loader = new ClassStringResourceLoader(pageClass);
        String label = loader.loadStringResource(pageClass, CommonConstants.MAIN_NAVIGATION_LINK_LABEL, getLocale(), getStyle(), getVariation());
        if (StringUtils.isEmpty(label)) {
            label = loader.loadStringResource(pageClass, CommonConstants.CONTENT_TITLE_LABEL, getLocale(), getStyle(), getVariation());
        }
        return new Label("mainNavigationLinkLabel", label);
    }

    /**
     * Create the boxes on the right hand side
     *
     * @return view
     */
    private Component createRepeatingBoxes() {
        IModel<List<Box>> repeatingBoxesModel = createRepeatingBoxesModel();
        return new ListView<Box>("repeatingSideNav", repeatingBoxesModel) {
            private static final long serialVersionUID = -8656227160522461618L;

            @Override
            protected void populateItem(ListItem<Box> item) {
                Box box = item.getModelObject();
                String customStyle = box.getCustomStyle();
                Fragment outerBoxComponent = createOuterBoxFragment(customStyle);
                Component boxComponent = createInnerBox(item);
                setBoxTitleVisibility(item.getModelObject(), boxComponent);
                outerBoxComponent.add(boxComponent);
                item.setVisible(boxComponent.isVisible());
                item.add(outerBoxComponent);
            }
        };
    }

    private Fragment createOuterBoxFragment(String customStyle) {
        if (StringUtils.isNotBlank(customStyle) && existsCustomStyleFragment(customStyle)) {
            return new Fragment("outerBox", customStyle, this);
        } else {
            return new Fragment("outerBox", "defaultBoxTemplate", this);
        }
    }

    private boolean existsCustomStyleFragment(String fragmentId) {
        MarkupStream associatedMarkupStream = TemplatePage.this.getAssociatedMarkupStream(false);
        IMarkupFragment defaultBoxTemplateIndex = associatedMarkupStream.getMarkupFragment().find(fragmentId);
        return defaultBoxTemplateIndex != null;
    }

    private IModel<List<Box>> createRepeatingBoxesModel() {
        return new LoadableDetachableModel<List<Box>>() {
            private static final long serialVersionUID = -8338162419354614447L;

            @Override
            protected List<Box> load() {
                return boxService.findAllOrderedBySort();
            }
        };
    }

    private Component createInnerBox(ListItem<Box> item) {
        Box box = item.getModelObject();
        Class<? extends Component> boxClazz = boxRegistry.getClassBySimpleClassName(box.getBoxType());
        if (boxClazz == null) {
            return createBoxNotFoundPanel();
        } else if (boxClazz.isAssignableFrom(SearchBoxPanel.class)) {
            return newFilterBox(getBoxId());
        } else if (boxClazz.isAssignableFrom(OtherBoxPanel.class)) {
            return createOtherBox(box);
        } else if (boxClazz.isAssignableFrom(PageAdminBoxPanel.class)) {
            return createPageAdminBox();
        } else if (boxClazz.isAssignableFrom(LoginBoxPanel.class)) {
            return createLoginBox();
        } else if (boxClazz.isAssignableFrom(TagCloudBoxPanel.class)) {
            return newTagCloudBox(getBoxId());
        } else if (boxClazz.isAssignableFrom(GlobalAdminBoxPanel.class)) {
            return new GlobalAdminBoxPanel(getBoxId());
        } else if (boxClazz.isAssignableFrom(FeedBoxPanel.class)) {
            return new FeedBoxPanel(getBoxId(), getPageClass());
        } else {
            Component genericBoxInstance = createGenericBoxInstance(boxClazz);
            if (genericBoxInstance == null) {
                throw new IllegalArgumentException("The box class " + boxClazz + " does not have a default constructor with a String id parameter or String and PageParameters");
            }
            return genericBoxInstance;
        }
    }

    private Component createEmptyFilterBox() {
        return new WebMarkupContainer(getBoxId()).setVisible(false);
    }

    private Component createGenericBoxInstance(Class<? extends Component> boxClazz) {
        for (Constructor<?> constr : boxClazz.getConstructors()) {
            Class<?> param[] = constr.getParameterTypes();
            if (param.length == 2 && param[0] == String.class && param[1] == PageParameters.class) {
                try {
                    return (Component) constr.newInstance(getBoxId(), params);
                } catch (Exception e) {
                    throw new UnhandledException(e);
                }
            } else if (param.length == 1 && param[0] == String.class) {
                try {
                    return (Component) constr.newInstance(getBoxId());
                } catch (Exception e) {
                    throw new UnhandledException(e);
                }
            }
        }
        return null;
    }

    private void setBoxTitleVisibility(Box box, Component boxInstance) {
        if (boxInstance instanceof BoxTitleVisibility) {
            ((BoxTitleVisibility) boxInstance).setTitleVisible(!box.getHideTitle());
        }
    }

    private LoginBoxPanel createLoginBox() {
        return new LoginBoxPanel(getBoxId(), params);
    }

    private PageAdminBoxPanel createPageAdminBox() {
        PageAdminBoxPanel box = new PageAdminBoxPanel(getBoxId());
        // TODO bad interface!
        List<Component> components = newPageAdminBoxLinks("adminLink", "linkName");
        for (Component component : components) {
            box.addLink(component);
        }
        return box;
    }

    private OtherBoxPanel createOtherBox(Box box) {
        return new OtherBoxPanel(getBoxId(), Model.of(box));
    }

    private Component createBoxNotFoundPanel() {
        Component boxInstance;
        Box error = new Box();
        error.setTitle("!Error!");
        error.setContent("Box type is not available!");
        boxInstance = createOtherBox(error);
        return boxInstance;
    }

    /**
     * Create the Filter Box e.g. search or tags
     *
     * @param markupId markup id
     * @return filter component
     */
    protected Component newFilterBox(String markupId) {
        return createEmptyFilterBox();
    }

    /**
     * Create the TagCloud Box e.g. search or tags
     *
     * @param markupId markup id
     * @return tag cloud component
     */
    protected Component newTagCloudBox(String markupId) {
        return createEmptyFilterBox();
    }

    /**
     * Returns a custom list with page admin links
     *
     * @param linkMarkupId  link markup id
     * @param labelMarkupId link label markup id
     * @return list with links
     */
    protected List<Component> newPageAdminBoxLinks(String linkMarkupId, String labelMarkupId) {
        Component component = newPageAdminBoxLink(linkMarkupId, labelMarkupId);
        if (component != null) {
            return Arrays.asList(component);
        }
        return new ArrayList<Component>();
    }

    /**
     * Returns just one page admin link, convience method
     *
     * @param linkMarkupId  link markup id
     * @param labelMarkupId link label markup id
     * @return one link
     */
    protected Component newPageAdminBoxLink(String linkMarkupId, String labelMarkupId) {
        return null;
    }

    private String getBoxId() {
        return "box";
    }

    public String getPageTitle() {
        return "";
    }

    public FeedbackPanel getFeedback() {
        return feedback;
    }

    public String getRequestURL() {
        StringBuffer url = ((HttpServletRequest)RequestCycle.get().getRequest().getContainerRequest()).getRequestURL();
        return url.toString();
    }
}
