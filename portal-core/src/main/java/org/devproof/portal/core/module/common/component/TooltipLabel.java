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
package org.devproof.portal.core.module.common.component;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * A label with tooltip functionality (on mouse over)
 * 
 * @author Carsten Hufe
 */
public class TooltipLabel extends Panel {
	private static final long serialVersionUID = 1L;

	public TooltipLabel(String id, Component label, Component tooltip) {
		super(id);
		String tooltipMarkupId = getTooltipMarkupId();
		modifyMarkupId(label, tooltip, tooltipMarkupId);
		add(createJavascriptHeaderContributor());
		add(createCSSHeaderContributor());
		add(createTooltipLink(label, tooltipMarkupId));
		add(tooltip);
	}

	private String getTooltipMarkupId() {
		double d = Math.random();
		String str = Double.toString(d).substring(2);
		String tooltipMarkupId = "MID" + str;
		return tooltipMarkupId;
	}

	private void modifyMarkupId(Component label, Component tooltip, String tooltipMarkupId) {
		tooltip.setMarkupId("tooltip");
		label.setMarkupId("label");
		tooltip.add(createIdAttributeModifier(tooltipMarkupId));
	}

	private WebMarkupContainer createTooltipLink(Component label, String tooltipMarkupId) {
		String labelMarkupId = getTooltipMarkupId();
		WebMarkupContainer link = new WebMarkupContainer("link");
		link.add(createOnMouseOverAttributeModifier(tooltipMarkupId, labelMarkupId));
		link.add(createOnMouseOutAttributeModifier(tooltipMarkupId));
		link.add(createIdAttributeModifier(labelMarkupId));
		link.add(label);
		return link;
	}

	private SimpleAttributeModifier createIdAttributeModifier(String tooltipMarkupId) {
		return new SimpleAttributeModifier("id", tooltipMarkupId);
	}

	private SimpleAttributeModifier createOnMouseOutAttributeModifier(String strTT) {
		return new SimpleAttributeModifier("onmouseout", "xstooltip_hide('" + strTT + "');");
	}

	private SimpleAttributeModifier createOnMouseOverAttributeModifier(String strTT, String tooltipMarkupId) {
		return new SimpleAttributeModifier("onmouseover", "xstooltip_show('" + strTT + "', '" + tooltipMarkupId
				+ "', 289, 49);");
	}

	private HeaderContributor createCSSHeaderContributor() {
		return CSSPackageResource.getHeaderContribution(TooltipLabel.class, "TooltipLabel.css");
	}

	private HeaderContributor createJavascriptHeaderContributor() {
		return JavascriptPackageResource.getHeaderContribution(TooltipLabel.class, "TooltipLabel.js");
	}
}
