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
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.devproof.portal.core.module.common.CommonConstants;

/**
 * the part in blogs downloads, etc "created by [name] at [date]
 * 
 * @author Carsten Hufe
 */
public class BubblePanel extends Panel {
	private static final long serialVersionUID = 1L;

	public BubblePanel(String id) {
		super(id);
		add(new SimpleAttributeModifier("style", "display:none;"));
		add(new SimpleAttributeModifier("class", "bubblePopup"));
		add(createContent(getContentId()));
		setOutputMarkupId(true);
	}

	public String getContentId() {
		return "content";
	}

	public void setContent(Component component) {
		replace(component);
	}

	public void setMessage(String message) {
		setContent(new MessageFragment(getContentId(), message));
	}

	protected Component createContent(String id) {
		return new WebMarkupContainer(id);
	}

	public void show(String linkId, AjaxRequestTarget target) {
		target.addComponent(this);
		String js = "var p = $(\"#" + linkId + "\"); var pos = p.position();";
		js += "$(\"#"
				+ getMarkupId()
				+ "\").css( {\"position\": \"absolute\", \"left\": (pos.left - 45 + (p.width() / 2)) + \"px\", \"top\":(pos.top - $(\"#"
				+ getMarkupId() + "\").height() - (p.height() / 2)) + \"px\" } );";

		js += "$(\".bubblePopup\").fadeOut(\"normal\");";
		js += "$(\"#" + getMarkupId() + "\").fadeIn(\"normal\");";
		target.appendJavascript(js);
	}

	public void hide(AjaxRequestTarget target) {
		target.appendJavascript("$(\".bubblePopup\").fadeOut(\"slow\");");
	}

	private class MessageFragment extends Fragment {
		private static final long serialVersionUID = 1L;

		public MessageFragment(String id, String message) {
			super(id, "messageFragment", BubblePanel.this);
			add(new Image("infoImage", CommonConstants.REF_INFORMATION_IMG));
			add(new Label("message", message));
			add(new AjaxLink<Void>("okButton") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					hide(target);
				}
			});
		}
	}
}
