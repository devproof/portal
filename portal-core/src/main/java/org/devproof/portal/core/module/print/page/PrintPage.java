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
package org.devproof.portal.core.module.print.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.print.PrintConstants;

/**
 * @author Carsten Hufe
 */
public class PrintPage extends WebPage {

	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;

	public PrintPage(final PageParameters params) {
		add(CSSPackageResource.getHeaderContribution(CommonConstants.class, "css/default.css"));
		add(CSSPackageResource.getHeaderContribution(PrintConstants.class, "css/print.css"));
		add(new Label("content", "Halo"));
		add(new Label("pageTitle", configurationService.findAsString(CommonConstants.CONF_PAGE_TITLE)));
		WebMarkupContainer copyright = new WebMarkupContainer("copyright");
		copyright.add(new SimpleAttributeModifier("content", configurationService
				.findAsString(CommonConstants.CONF_COPYRIGHT_OWNER)));
		add(copyright);
		String footerContent = configurationService.findAsString("footer_content");
		add(new Label("footerLabel", footerContent).setEscapeModelStrings(false));
	}
}
