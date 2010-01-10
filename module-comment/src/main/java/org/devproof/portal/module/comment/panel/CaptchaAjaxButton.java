package org.devproof.portal.module.comment.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.markup.html.form.Form;
import org.devproof.portal.module.comment.panel.CaptchaPanel.OnClickCallback;

public abstract class CaptchaAjaxButton extends AjaxFallbackButton {
	private static final long serialVersionUID = 1L;
	private CaptchaPanel captchaPanel;

	public CaptchaAjaxButton(String id, CaptchaPanel captchaPanel, Form<?> form) {
		super(id, form);
		this.captchaPanel = captchaPanel;
		captchaPanel.setOnClickCallback(new OnClickCallback() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClickAndCaptchaValidated(AjaxRequestTarget target) {
				CaptchaAjaxButton.this.onClickAndCaptchaValidated(target);
			}
		});
	}

	@Override
	final protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
		if (captchaPanel.isRenderAllowed()) {
			captchaPanel.refreshCaptcha();
			target.addComponent(captchaPanel);
			String js = "var p = $(\"#" + getMarkupId() + "\");\n var pos = p.position();";
			js += "$(\"#" + captchaPanel.getMarkupId()
					+ "\").css( {\"position\": \"absolute\", \"left\": (pos.left) + \"px\", \"top\":(pos.top - $(\"#"
					+ captchaPanel.getMarkupId() + "\").height() - 3) + \"px\" } );";

			js += "$(\".captchaPopup\").fadeOut(\"fast\");";
			js += "$(\"#" + captchaPanel.getMarkupId() + "\").fadeIn(\"slow\");";
			target.appendJavascript(js);
		} else {
			onClickAndCaptchaValidated(target);
		}
	}

	public abstract void onClickAndCaptchaValidated(AjaxRequestTarget target);
}
