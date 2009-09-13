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
package org.devproof.portal.core.module.common.page;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.MapModel;
import org.apache.wicket.resource.loader.ClassStringResourceLoader;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.collections.MiniMap;
import org.apache.wicket.util.string.UrlUtils;
import org.apache.wicket.util.template.TextTemplateHeaderContributor;
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
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.feed.component.Atom1Link;
import org.devproof.portal.core.module.feed.component.Rss2Link;
import org.devproof.portal.core.module.feed.panel.FeedBoxPanel;
import org.devproof.portal.core.module.tag.entity.BaseTagEntity;
import org.devproof.portal.core.module.tag.panel.TagCloudBoxPanel;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.core.module.user.page.LoginPage;
import org.devproof.portal.core.module.user.panel.LoginBoxPanel;

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
	private final IModel<String> pageTitle;

	
	public TemplatePage(final PageParameters params) {
		add(CSSPackageResource.getHeaderContribution(CommonConstants.class, "css/default.css"));
		add(CSSPackageResource.getHeaderContribution(CommonConstants.class, "css/body.css"));
		if (params.containsKey("infoMsg")) {
			info(params.getString("infoMsg"));
		}
		if (params.containsKey("errorMsg")) {
			info(params.getString("errorMsg"));
		}

		setOutputMarkupId(true);
		addMainNavigation();

		add(new Rss2Link("rss2reference", getClass()));
		add(new Atom1Link("atom1reference", getClass()));

		String footerContent = configurationService.findAsString("footer_content");
		CommonMarkupContainerFactory factory = sharedRegistry.getResource("footerLink");
		MarkupContainer footerLink;
		if (factory != null) {
			footerLink = factory.newInstance("footerLink");
		} else {
			footerLink = new WebMarkupContainer("footerLink");
		}
		footerLink.add(new Label("footerLabel", footerContent).setEscapeModelStrings(false));
		add(footerLink);
		add(feedback = new FeedbackPanel("feedbackPanel"));

		feedback.setOutputMarkupId(true);
		addBoxes(params);

		final boolean googleEnabled = configurationService.findAsBoolean(CommonConstants.CONF_GOOGLE_ANALYTICS_ENABLED);
		final WebMarkupContainer googleAnalytics1 = new WebMarkupContainer("googleAnalytics1");
		googleAnalytics1.setVisible(googleEnabled);
		add(googleAnalytics1);
		final WebComponent googleAnalytics2 = new WebComponent("googleAnalytics2") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
				final StringBuilder buf = new StringBuilder();
				buf.append("try {\n");
				buf.append("var pageTracker = _gat._getTracker(\"");
				buf.append(configurationService.findAsString(CommonConstants.CONF_GOOGLE_WEBPROPERTY_ID));
				buf.append("\");\n");
				buf.append("pageTracker._trackPageview();");
				buf.append("} catch(err) {}");
				replaceComponentTagBody(markupStream, openTag, buf.toString());
			}
		};
		googleAnalytics2.setVisible(googleEnabled);
		add(googleAnalytics2);
		String localPageTitle = this.getString("contentTitle", null, "");
		if (StringUtils.isNotEmpty(localPageTitle)) {
			localPageTitle += " - ";
		}
		pageTitle = Model.of(localPageTitle + configurationService.findAsString(CommonConstants.CONF_PAGE_TITLE));
		add(new Label("pageTitle", pageTitle));
		final WebMarkupContainer copyright = new WebMarkupContainer("copyright");
		copyright.add(new SimpleAttributeModifier("content", configurationService
				.findAsString(CommonConstants.CONF_COPYRIGHT_OWNER)));
		add(copyright);
	}

	/**
	 * Adds the Css and JavaScript of the SyntaxHighligher to the page
	 */
	protected void addSyntaxHighlighter() {
		add(CSSPackageResource.getHeaderContribution(CommonConstants.class,
				"js/SyntaxHighlighter/SyntaxHighlighter.css"));
		add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shCore.js"));
		add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class,
				"js/SyntaxHighlighter/shBrushJava.js"));
		add(JavascriptPackageResource
				.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushSql.js"));
		add(JavascriptPackageResource
				.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushXml.js"));
		add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class,
				"js/SyntaxHighlighter/shBrushJScript.js"));
		add(JavascriptPackageResource
				.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushCpp.js"));
		add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class,
				"js/SyntaxHighlighter/shBrushCSharp.js"));
		add(JavascriptPackageResource
				.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushCss.js"));
		add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class,
				"js/SyntaxHighlighter/shBrushDelphi.js"));
		add(JavascriptPackageResource
				.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushPhp.js"));
		add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class,
				"js/SyntaxHighlighter/shBrushPython.js"));
		add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class,
				"js/SyntaxHighlighter/shBrushRuby.js"));
		add(JavascriptPackageResource.getHeaderContribution(CommonConstants.class, "js/SyntaxHighlighter/shBrushVb.js"));
		final Map<String, Object> values = new MiniMap<String, Object>(1);
		values.put("swfPath", UrlUtils.rewriteToContextRelative("resources/"
				+ CommonConstants.REF_SYNTAXHIGHLIGHTER_SWF, getRequest()));
		add(TextTemplateHeaderContributor.forJavaScript(CommonConstants.class,
				"js/SyntaxHighlighter/SyntaxHighlighterCopy.js", new MapModel<String, Object>(values)));
	}

	/**
	 * Adds the Main Navigation on the top
	 */
	private void addMainNavigation() {
		final RepeatingView repeating = new RepeatingView("repeatingMainNav");
		add(repeating);
		final List<Class<? extends Page>> registeredPages = mainNavigationRegistry.getRegisteredPages();
		for (final Class<? extends Page> pageClass : registeredPages) {
			final WebMarkupContainer item = new WebMarkupContainer(repeating.newChildId());
			repeating.add(item);
			final BookmarkablePageLink<Void> link = new BookmarkablePageLink<Void>("mainNavigationLink", pageClass);
			String label = new ClassStringResourceLoader(pageClass).loadStringResource(null,
					CommonConstants.MAIN_NAVIGATION_LINK_LABEL);
			if (StringUtils.isEmpty(label)) {
				label = new ClassStringResourceLoader(pageClass).loadStringResource(null,
						CommonConstants.CONTENT_TITLE_LABEL);
			}
			link.add(new Label("mainNavigationLinkLabel", label));
			item.add(link);
		}
	}

	/**
	 * Add the boxes on the right hand side
	 */
	private void addBoxes(final PageParameters params) {
		final List<BoxEntity> boxes = boxService.findAllOrderedBySort();
		final RepeatingView repeating = new RepeatingView("repeatingSideNav");
		add(repeating);
		for (final BoxEntity box : boxes) {
			final WebMarkupContainer item = new WebMarkupContainer(repeating.newChildId());
			repeating.add(item);
			Class<? extends Component> boxClazz = boxRegistry.getClassBySimpleClassName(box.getBoxType());
			Component boxInstance = null;
			if (boxClazz == null) {
				final BoxEntity error = new BoxEntity();
				error.setTitle("!Error!");
				error.setContent("Box type is not available!");
				item.add(boxInstance = new OtherBoxPanel("box", Model.of(error)));
			} else if (boxClazz.isAssignableFrom(SearchBoxPanel.class)) {
				if (filterBox == null) {
					item.add(filterBox = new WebMarkupContainer("box").setVisible(false));
					filterBoxHideTitle = box.getHideTitle();
				}
			} else if (boxClazz.isAssignableFrom(OtherBoxPanel.class)) {
				item.add(boxInstance = new OtherBoxPanel("box", Model.of(box)));
			} else if (boxClazz.isAssignableFrom(PageAdminBoxPanel.class)) {
				item.add(pageAdminBox = new PageAdminBoxPanel("box"));
				boxInstance = pageAdminBox;
			} else if (boxClazz.isAssignableFrom(LoginBoxPanel.class)) {
				if (!(this instanceof LoginPage)) {
					item.add(boxInstance = new LoginBoxPanel("box", params));
				} else {
					item.remove();
				}
			} else if (boxClazz.isAssignableFrom(TagCloudBoxPanel.class)) {
				if (tagCloudBox == null) {
					item.add(tagCloudBox = new WebMarkupContainer("box").setVisible(false));
					tagCloudBoxHideTitle = box.getHideTitle();
					boxInstance = tagCloudBox;
				}
			} else if (boxClazz.isAssignableFrom(GlobalAdminBoxPanel.class)) {
				item.add(boxInstance = new GlobalAdminBoxPanel("box"));
			} else if (boxClazz.isAssignableFrom(FeedBoxPanel.class)) {
				item.add(boxInstance = new FeedBoxPanel("box", getPageClass()));
			} else {
				boolean found = false;
				for (final Constructor<?> constr : boxClazz.getConstructors()) {
					final Class<?> param[] = constr.getParameterTypes();
					if (param.length == 2 && param[0] == String.class && param[1] == PageParameters.class) {
						try {
							boxInstance = (Component) constr.newInstance("box", params);
							found = true;
							break;
						} catch (final Exception e) {
							throw new UnhandledException(e);
						}
					} else if (param.length == 1 && param[0] == String.class) {
						try {
							boxInstance = (Component) constr.newInstance("box");
							found = true;
							break;
						} catch (final Exception e) {
							throw new UnhandledException(e);
						}
					}
				}
				if (!found) {
					throw new IllegalArgumentException(
							"The box class "
									+ boxClazz
									+ " does not have a default constructor with a String id parameter or String and PageParameters");
				} else {
					item.add(boxInstance);
				}
			}

			if (boxInstance instanceof BoxTitleVisibility) {
				((BoxTitleVisibility) boxInstance).setTitleVisible(!box.getHideTitle());
			}
		}
	}

	/**
	 * Set the Filter Box e.g. search or tags
	 */
	public void addFilterBox(final Panel filterBox) {
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
	public <T extends BaseTagEntity<?>> void addTagCloudBox(final TagService<T> tagService, final IModel<T> model,
			final Class<? extends Page> page, final PageParameters params) {
		TagCloudBoxPanel<?> newTagCloudBox = new TagCloudBoxPanel<T>("box", tagService, model, page, params);
		newTagCloudBox.setTitleVisible(!tagCloudBoxHideTitle);
		tagCloudBox.replaceWith(newTagCloudBox);
		tagCloudBox = newTagCloudBox;
	}

	/**
	 * Clean the tag selection
	 */
	public void cleanTagSelection() {
		if (tagCloudBox instanceof TagCloudBoxPanel<?>) {
			final TagCloudBoxPanel<?> box = (TagCloudBoxPanel<?>) tagCloudBox;
			box.cleanSelection();
		}
	}

	/**
	 * Add a link to the page admin panel
	 */
	public void addPageAdminBoxLink(final Component link) {
		if (pageAdminBox != null) {
			pageAdminBox.addLink(link);
		}
	}

	/**
	 * Change the page title
	 */
	public void setPageTitle(final String title) {
		pageTitle.setObject(title + " - " + configurationService.findAsString(CommonConstants.CONF_PAGE_TITLE));
	}

	public FeedbackPanel getFeedback() {
		return feedback;
	}

	public String getRequestURL() {
		StringBuffer url = getWebRequestCycle().getWebRequest().getHttpServletRequest().getRequestURL();
		return url.toString();
	}
}
