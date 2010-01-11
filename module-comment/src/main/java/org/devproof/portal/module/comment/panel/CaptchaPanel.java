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

import java.awt.Dimension;
import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * the part in blogs downloads, etc "created by [name] at [date]
 * 
 * @author Carsten Hufe
 */
public class CaptchaPanel extends Panel {
	private static final long serialVersionUID = 1L;

	private WebMarkupContainer captcha;
	private KittenCaptchaPanel kittenCaptchaImagePanel;
	private OnClickCallback onClickCallback;
	private AjaxLink<Void> confirmButton;

	public CaptchaPanel(String id) {
		super(id);
		add(new SimpleAttributeModifier("style", "display:none; width: 450px; "));
		add(new SimpleAttributeModifier("class", "captchaPopup"));
		captcha = new WebMarkupContainer("captcha");
		captcha.add(new WebMarkupContainer("kittenCaptchaImage"));
		confirmButton = new AjaxLink<Void>("confirm") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				if (kittenCaptchaImagePanel.allKittensSelected()) {
					String js = "$(\".captchaPopup\").fadeOut(\"slow\");";
					target.appendJavascript(js);
					onClickCallback.onClickAndCaptchaValidated(target);
				}
			}

			@Override
			public boolean isEnabled() {
				return kittenCaptchaImagePanel != null && kittenCaptchaImagePanel.allKittensSelected();
			}
		};
		captcha.add(confirmButton);
		AjaxLink<Void> abortButton = new AjaxLink<Void>("cancel") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				String js = "$(\".captchaPopup\").fadeOut(\"slow\");";
				target.appendJavascript(js);
			}

		};
		captcha.add(abortButton);
		add(captcha);
		setOutputMarkupId(true);
	}

	private Component createKittenCaptchaImagePanel() {
		kittenCaptchaImagePanel = new KittenCaptchaPanel("kittenCaptchaImage", new Dimension(400, 200)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClick(AjaxRequestTarget target) {
				target.addComponent(confirmButton);
			}
		};
		return kittenCaptchaImagePanel;
	}

	public void refreshCaptcha() {
		captcha.replace(createKittenCaptchaImagePanel());
	}

	protected void setOnClickCallback(OnClickCallback onClickCallback) {
		this.onClickCallback = onClickCallback;
	}

	protected static interface OnClickCallback extends Serializable {
		public void onClickAndCaptchaValidated(AjaxRequestTarget target);
	}

}
