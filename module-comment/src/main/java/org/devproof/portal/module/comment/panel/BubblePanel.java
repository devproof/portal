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
package org.devproof.portal.module.comment.panel;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * the part in blogs downloads, etc "created by [name] at [date]
 * 
 * @author Carsten Hufe
 */
public class BubblePanel extends Panel {
	private static final long serialVersionUID = 1L;

	public BubblePanel(String id) {
		super(id);
		add(new SimpleAttributeModifier("style", "display:none; width: 450px;"));
		add(new SimpleAttributeModifier("class", "bubblePopup"));
		add(createContent(getContentId()));
		setOutputMarkupId(true);
	}

	public String getContentId() {
		return "content";
	}

	public Component createContent(String id) {
		return new WebMarkupContainer(id);
	}

	public void show(String linkId, AjaxRequestTarget target) {
		String js = "var p = $(\"#" + linkId + "\"); var pos = p.position();";
		js += "$(\"#" + getMarkupId()
				+ "\").css( {\"position\": \"absolute\", \"left\": (pos.left) + \"px\", \"top\":(pos.top - $(\"#"
				+ getMarkupId() + "\").height() - 3) + \"px\" } );";

		js += "$(\".bubblePopup\").fadeOut(\"fast\");";
		js += "$(\"#" + getMarkupId() + "\").fadeIn(\"slow\");";
		target.appendJavascript(js);
	}

	public void hide(AjaxRequestTarget target) {
		target.appendJavascript("$(\".bubblePopup\").fadeOut(\"slow\");");
	}
}
