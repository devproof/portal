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
package org.devproof.portal.core.module.common.panel;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.util.PortalUtil;

/**
 * 
 * @author Carsten Hufe
 */
public class BubblePanel extends Panel {
	private static final long serialVersionUID = 1L;

	public BubblePanel(String id) {
		super(id);
		PortalUtil.addJQuery(this);
		add(createCssHeaderContributor());
		add(createStyleAttributeModifier());
		add(createClassAttributeModifier());
		add(createContent());
		setOutputMarkupId(true);
	}

	private HeaderContributor createCssHeaderContributor() {
		return CSSPackageResource.getHeaderContribution(CommonConstants.class, "css/dialog.css");
	}

	private SimpleAttributeModifier createStyleAttributeModifier() {
		return new SimpleAttributeModifier("style", "display:none;");
	}

	private SimpleAttributeModifier createClassAttributeModifier() {
		return new SimpleAttributeModifier("class", "bubblePopup");
	}

	private Component createContent() {
		return createContent(getContentId());
	}

	public String getContentId() {
		return "content";
	}

	public void setContent(Component component) {
		replace(component);
	}

	private void setMessage(String message) {
		setContent(createMessageFragment(message));
	}

	private MessageFragment createMessageFragment(String message) {
		return new MessageFragment(getContentId(), message);
	}

	protected Component createContent(String id) {
		return new WebMarkupContainer(id);
	}

	private void show(String linkId, AjaxRequestTarget target) {
		target.addComponent(this);
		String js = "var id = '#" + getMarkupId() + "'; var p = $(\"#" + linkId + "\"); var pos = p.position();";
		js += "$(id).css( {'position': 'absolute', 'left': (pos.left - 45 + (p.width() / 2)) + 'px', 'top':(pos.top - $(id).height() - (p.height() / 2) + 50) + 'px' } );";
		js += "$('.bubblePopup').fadeOut('normal');";
		js += "$(id).fadeIn('normal');";
		target.appendJavascript(js);
	}

	public void showModal(AjaxRequestTarget target) {
		target.addComponent(this);
		String js = "";
		js = "var maskHeight = $(document).height(); var maskWidth = $(window).width();";
		js += "$('#modalMask').css({'width':maskWidth,'height':maskHeight});";
		js += "$('#modalMask').fadeTo('normal',0.3);";
		// js +=
		// "$('#modalMask').fadeIn('fast'); $('#modalMask').fadeTo('fast',0.3);";
		js += "var id = '#" + getMarkupId() + "';";
		js += "var winH = $(window).height();";
		js += "var winW = $(window).width();";
		js += "$(id).css({'top':  winH/2-$(id).height()/2, 'left': winW/2-$(id).width()/2}); ";
		js += "$(id).center(); $(id).fadeIn(1000); ";
		target.appendJavascript(js);
	}

	public void showMessage(String linkId, AjaxRequestTarget target, String message) {
		setMessage(message);
		show(linkId, target);
	}

	public void hide(AjaxRequestTarget target) {
		target.appendJavascript("$('#modalMask').fadeOut('fast'); $('.bubblePopup').fadeOut('slow'); ");

	}

	private class MessageFragment extends Fragment {
		private static final long serialVersionUID = 1L;

		private String message;

		public MessageFragment(String id, String message) {
			super(id, "messageFragment", BubblePanel.this);
			this.message = message;
			add(createInfoImage());
			add(createMessageLabel());
			add(createOkButton());
		}

		private Image createInfoImage() {
			return new Image("infoImage", CommonConstants.REF_INFORMATION_IMG);
		}

		private Label createMessageLabel() {
			return new Label("message", message);
		}

		private AjaxLink<Void> createOkButton() {
			return new AjaxLink<Void>("okButton") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					hide(target);
				}
			};
		}
	}
}
