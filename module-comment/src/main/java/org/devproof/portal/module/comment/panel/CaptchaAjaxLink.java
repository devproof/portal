package org.devproof.portal.module.comment.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.devproof.portal.module.comment.panel.CaptchaPanel.OnClickCallback;

public abstract class CaptchaAjaxLink extends AjaxFallbackLink<Void> {
	private static final long serialVersionUID = 1L;
	private CaptchaPanel captchaPanel;

	public CaptchaAjaxLink(String id, CaptchaPanel captchaPanel) {
		super(id);
		this.captchaPanel = captchaPanel;
		setOutputMarkupId(true);
	}

	@Override
	final public void onClick(AjaxRequestTarget target) {
		if (captchaPanel.isRenderAllowed()) {
			captchaPanel.setOnClickCallback(new OnClickCallback() {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClickAndCaptchaValidated(AjaxRequestTarget target) {
					CaptchaAjaxLink.this.onClickAndCaptchaValidated(target);
				}
			});

			String js = "var p = $(\"#" + getMarkupId() + "\");\n var pos = p.position();";
			js += "$(\"#" + captchaPanel.getMarkupId()
					+ "\").css( {\"position\": \"absolute\", \"left\": (pos.left) + \"px\", \"top\":(pos.top - $(\"#"
					+ captchaPanel.getMarkupId() + "\").height() - 3) + \"px\" } );";

			js += "$(\".captchaPopup\").fadeOut(\"fast\");";
			js += "$(\"#" + captchaPanel.getMarkupId() + "\").fadeIn(\"slow\");";
			target.appendJavascript(js);
			captchaPanel.refreshCaptcha();
			target.addComponent(captchaPanel);
		} else {
			onClickAndCaptchaValidated(target);
		}
	}

	public abstract void onClickAndCaptchaValidated(AjaxRequestTarget target);
}
