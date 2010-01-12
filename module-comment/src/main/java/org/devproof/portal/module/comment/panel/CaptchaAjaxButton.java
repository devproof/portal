package org.devproof.portal.module.comment.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.html.form.Form;
import org.devproof.portal.core.app.PortalSession;

public abstract class CaptchaAjaxButton extends AjaxFallbackButton {
	private static final long serialVersionUID = 1L;
	private BubblePanel bubblePanel;

	public CaptchaAjaxButton(String id, BubblePanel bubblePanel, Form<?> form) {
		super(id, form);
		this.bubblePanel = bubblePanel;
	}

	@Override
	final protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
		if (!PortalSession.get().hasRight("captcha.disabled")) {
			CaptchaPanel captchaPanel = new CaptchaPanel(bubblePanel.getContentId()) {
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
			bubblePanel.setContent(captchaPanel);
			bubblePanel.show(getMarkupId(), target);
		} else {
			onClickAndCaptchaValidated(target);
		}
	}

	public abstract void onClickAndCaptchaValidated(AjaxRequestTarget target);
}
