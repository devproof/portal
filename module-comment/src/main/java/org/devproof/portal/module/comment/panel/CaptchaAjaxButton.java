package org.devproof.portal.module.comment.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.html.form.Form;
import org.devproof.portal.module.comment.panel.CaptchaPanel.OnClickCallback;

public abstract class CaptchaAjaxButton extends AjaxFallbackButton {
	private static final long serialVersionUID = 1L;
	private CaptchaBubblePanel captchaBubblePanel;

	public CaptchaAjaxButton(String id, CaptchaBubblePanel captchaBubblePanel, Form<?> form) {
		super(id, form);
		this.captchaBubblePanel = captchaBubblePanel;
	}

	@Override
	final protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
		if (captchaBubblePanel.isRenderAllowed()) {
			captchaBubblePanel.setOnClickCallback(new OnClickCallback() {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClickAndCaptchaValidated(AjaxRequestTarget target) {
					CaptchaAjaxButton.this.onClickAndCaptchaValidated(target);
				}
			});
			captchaBubblePanel.refreshCaptcha();
			target.addComponent(captchaBubblePanel);
			captchaBubblePanel.show(getMarkupId(), target);
			// String js = "var p = $(\"#" + getMarkupId() +
			// "\");\n var pos = p.position();";
			// js += "$(\"#" + captchaBubblePanel.getMarkupId()
			// +
			// "\").css( {\"position\": \"absolute\", \"left\": (pos.left) + \"px\", \"top\":(pos.top - $(\"#"
			// + captchaBubblePanel.getMarkupId() +
			// "\").height() - 3) + \"px\" } );";
			//
			// js += "$(\".captchaPopup\").fadeOut(\"fast\");";
			// js += "$(\"#" + captchaBubblePanel.getMarkupId() +
			// "\").fadeIn(\"slow\");";
			// js += "hideLoadingIndicator();";
			// target.appendJavascript(js);
		} else {
			onClickAndCaptchaValidated(target);
		}
	}

	public abstract void onClickAndCaptchaValidated(AjaxRequestTarget target);
}
