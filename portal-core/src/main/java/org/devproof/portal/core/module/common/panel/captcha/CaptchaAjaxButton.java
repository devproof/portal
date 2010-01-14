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
package org.devproof.portal.core.module.common.panel.captcha;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.html.form.Form;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.core.module.common.util.PortalUtil;

/**
 * @author Carsten Hufe
 */
public abstract class CaptchaAjaxButton extends AjaxFallbackButton {
	private static final long serialVersionUID = 1L;
	private BubblePanel bubblePanel;

	public CaptchaAjaxButton(String id, BubblePanel bubblePanel, Form<?> form) {
		super(id, form);
		this.bubblePanel = bubblePanel;
		PortalUtil.addJQuery(this);
		setOutputMarkupId(true);
	}

	@Override
	final protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
		if (showCaptcha()) {
			CaptchaPanel captchaPanel = createCaptchaPanel();
			bubblePanel.setContent(captchaPanel);
			bubblePanel.show(getMarkupId(), target);
		} else {
			onClickAndCaptchaValidated(target);
		}
	}

	private CaptchaPanel createCaptchaPanel() {
		return new CaptchaPanel(bubblePanel.getContentId()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClickAndCaptchaValidated(AjaxRequestTarget target) {
				bubblePanel.hide(target);
				CaptchaAjaxButton.this.onClickAndCaptchaValidated(target);
			}

			@Override
			protected void onCancel(AjaxRequestTarget target) {
				bubblePanel.hide(target);
			}
		};
	}

	private boolean showCaptcha() {
		return !PortalSession.get().hasRight("captcha.disabled");
	}

	public abstract void onClickAndCaptchaValidated(AjaxRequestTarget target);
}
