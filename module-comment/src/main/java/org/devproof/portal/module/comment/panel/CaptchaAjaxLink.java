package org.devproof.portal.module.comment.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.devproof.portal.module.comment.panel.CaptchaPanel.OnClickCallback;

public abstract class CaptchaAjaxLink extends AjaxFallbackLink<Void> {
	private static final long serialVersionUID = 1L;
	private CaptchaBubblePanel captchaBubblePanel;

	public CaptchaAjaxLink(String id, CaptchaBubblePanel captchaBubblePanel) {
		super(id);
		this.captchaBubblePanel = captchaBubblePanel;
		setOutputMarkupId(true);
	}

	@Override
	final public void onClick(AjaxRequestTarget target) {
		if (captchaBubblePanel.isRenderAllowed()) {
			captchaBubblePanel.setOnClickCallback(new OnClickCallback() {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClickAndCaptchaValidated(AjaxRequestTarget target) {
					CaptchaAjaxLink.this.onClickAndCaptchaValidated(target);
				}
			});

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
			// target.appendJavascript(js);
			// captchaBubblePanel.refreshCaptcha();
			target.addComponent(captchaBubblePanel);
			captchaBubblePanel.show(getMarkupId(), target);
		} else {
			onClickAndCaptchaValidated(target);
		}
	}

	public abstract void onClickAndCaptchaValidated(AjaxRequestTarget target);
}
