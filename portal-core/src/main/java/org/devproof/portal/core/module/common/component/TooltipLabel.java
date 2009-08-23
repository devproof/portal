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
package org.devproof.portal.core.module.common.component;

import org.apache.wicket.Component;
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

	public TooltipLabel(final String id, final Component label, final Component tooltip) {
		super(id);
		add(JavascriptPackageResource.getHeaderContribution(TooltipLabel.class, "TooltipLabel.js"));
		add(CSSPackageResource.getHeaderContribution(TooltipLabel.class, "TooltipLabel.css"));
		tooltip.setMarkupId("tooltip");
		label.setMarkupId("label");
		WebMarkupContainer link = new WebMarkupContainer("link");
		add(link);
		link.add(label);
		add(tooltip);

		// modifying tags
		double d = Math.random();
		String str = Double.toString(d).substring(2);
		String strTT = "TT" + str;
		String strL = "L" + str;

		link
				.add(new SimpleAttributeModifier("onmouseover", "xstooltip_show('" + strTT + "', '" + strL
						+ "', 289, 49);"));
		link.add(new SimpleAttributeModifier("onmouseout", "xstooltip_hide('" + strTT + "');"));
		tooltip.add(new SimpleAttributeModifier("id", strTT));
		link.add(new SimpleAttributeModifier("id", strL));
	}
}
