package org.devproof.portal.module.comment.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;

public abstract class CaptchaAjaxLink extends AjaxFallbackLink<Void> {
	private static final long serialVersionUID = 1L;
	private BubblePanel bubblePanel;

	public CaptchaAjaxLink(String id, BubblePanel bubblePanel) {
		super(id);
		this.bubblePanel = bubblePanel;
		setOutputMarkupId(true);
	}

	@Override
	final public void onClick(AjaxRequestTarget target) {
		if (bubblePanel.isRenderAllowed()) {
			bubblePanel.replace(new CaptchaPanel(bubblePanel.getContentId()) {
				private static final long serialVersionUID = 1L;
				@Override
				protected void onClickAndCaptchaValidated(AjaxRequestTarget target) {
					bubblePanel.hide(target);
					CaptchaAjaxLink.this.onClickAndCaptchaValidated(target);
				}
				@Override
				protected void onCancel(AjaxRequestTarget target) {
					bubblePanel.hide(target);
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
			bubblePanel.show(getMarkupId(), target);
		} else {
			onClickAndCaptchaValidated(target);
		}
	}

	public abstract void onClickAndCaptchaValidated(AjaxRequestTarget target);
}
