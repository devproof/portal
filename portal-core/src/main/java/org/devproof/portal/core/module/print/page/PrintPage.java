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
package org.devproof.portal.core.module.print.page;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.print.PrintConstants;

/**
 * @author Carsten Hufe
 */
public abstract class PrintPage extends WebPage {

	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;
	private PageParameters params;

	public PrintPage(PageParameters params) {
		super(params);
		this.params = params;
		addSyntaxHighlighter();
		add(createDefaultCSSHeaderContributor());
		add(createPrinterCSSHeaderContributor());
		add(createPrintableComponent());
		add(createPageTitle());
		add(createCopyrightContainer());
		add(createFooterLabel());
	}

	private Component createPrintableComponent() {
		return createPrintableComponent("content", params);
	}

	private void addSyntaxHighlighter() {
		String theme = configurationService.findAsString(CommonConstants.CONF_SYNTAXHL_THEME);
		PortalUtil.addSyntaxHightlighter(this, theme);
	}

	private HeaderContributor createPrinterCSSHeaderContributor() {
		return CSSPackageResource.getHeaderContribution(PrintConstants.class, "css/print.css");
	}

	private HeaderContributor createDefaultCSSHeaderContributor() {
		return CSSPackageResource.getHeaderContribution(CommonConstants.class, "css/default.css");
	}

	private Component createFooterLabel() {
		String footerContent = configurationService.findAsString("footer_content");
		return new Label("footerLabel", footerContent).setEscapeModelStrings(false);
	}

	private WebMarkupContainer createCopyrightContainer() {
		WebMarkupContainer copyright = new WebMarkupContainer("copyright");
		copyright.add(new SimpleAttributeModifier("content", configurationService
				.findAsString(CommonConstants.CONF_COPYRIGHT_OWNER)));
		return copyright;
	}

	private Label createPageTitle() {
		return new Label("pageTitle", configurationService.findAsString(CommonConstants.CONF_PAGE_TITLE));
	}

	/**
	 * Returns the printable component
	 * 
	 * @param id
	 *            wicket id
	 * @param params
	 *            Page Parameter
	 * @return Instanciated component
	 */
	protected abstract Component createPrintableComponent(String id, PageParameters params);
}
