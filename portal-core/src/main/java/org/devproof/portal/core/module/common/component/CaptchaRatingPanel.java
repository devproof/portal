/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
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
package org.devproof.portal.core.module.common.component;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.rating.RatingPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.model.IModel;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.core.module.common.panel.captcha.CaptchaPanel;
import org.devproof.portal.core.module.common.util.PortalUtil;

/**
 * Is an extension of the rating panel Without ajax and with bookmarkable links
 * 
 * @author Carsten Hufe
 */
public abstract class CaptchaRatingPanel extends RatingPanel {
	private static final long serialVersionUID = 1L;
	private BubblePanel bubblePanel;
	private IModel<Boolean> hasVoted;

	public CaptchaRatingPanel(String id, IModel<Integer> rating, IModel<Integer> nrOfStars, IModel<Integer> nrOfVotes,
			IModel<Boolean> hasVoted, boolean addDefaultCssStyle, BubblePanel bubblePanel) {
		super(id, rating, nrOfStars, nrOfVotes, hasVoted, addDefaultCssStyle);
		this.bubblePanel = bubblePanel;
		this.hasVoted = hasVoted;
		PortalUtil.addJQuery(this);
	}

	@Override
	final protected void onRated(int rating, AjaxRequestTarget target) {
	}

	protected void onRated(int rating, AjaxRequestTarget target, String outputMarkupId) {
		if (!hasVoted.getObject()) {
			if (showCaptcha()) {
				CaptchaPanel captchaPanel = createCaptchaPanel(rating, outputMarkupId);
				bubblePanel.setContent(captchaPanel);
				bubblePanel.showModal(target);
			} else {
				onRatedAndCaptchaValidated(rating, target);
				bubblePanel.showMessage(outputMarkupId, target, getString("voteCounted"));
				// target.addComponent(CaptchaRatingPanel.this.get("rater"));
			}
		} else {
			bubblePanel.showMessage(outputMarkupId, target, getString("alreadyVoted"));
		}
	}

	protected abstract void onRatedAndCaptchaValidated(int rating, AjaxRequestTarget target);

	private CaptchaPanel createCaptchaPanel(final int rating, final String outputMarkupId) {
		return new CaptchaPanel(bubblePanel.getContentId()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClickAndCaptchaValidated(AjaxRequestTarget target) {
				bubblePanel.hide(target);
				CaptchaRatingPanel.this.onRatedAndCaptchaValidated(rating, target);
				bubblePanel.showMessage(outputMarkupId, target, CaptchaRatingPanel.this.getString("voteCounted"));
				// target.addComponent(CaptchaRatingPanel.this.get("rater"));
			}

			@Override
			protected void onCancel(AjaxRequestTarget target) {
				bubblePanel.hide(target);
			}
		};
	}

	@Override
	protected Component newRatingStarBar(String id, IModel<Integer> nrOfStars) {
		return new RatingStarBar(id, nrOfStars);
	}

	private boolean showCaptcha() {
		return !PortalSession.get().hasRight("captcha.disabled");
	}

	/**
	 * Renders the stars and the links necessary for rating.
	 */
	private final class RatingStarBar extends Loop {
		/** For serialization. */
		private static final long serialVersionUID = 1L;

		private RatingStarBar(String id, IModel<Integer> model) {
			super(id, model);
		}

		@Override
		protected void populateItem(LoopItem item) {
			// Use an AjaxFallbackLink for rating to make voting work even
			// without Ajax.
			AjaxFallbackLink<Void> link = new AjaxFallbackLink<Void>("link") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					LoopItem item = (LoopItem) getParent();

					// adjust the rating, and provide the target to the subclass
					// of our rating component, so other components can also get
					// updated in case of an AJAX event.

					onRated(item.getIteration() + 1, target, getMarkupId());
				}

				@Override
				public boolean isEnabled() {
					return !hasVoted.getObject() && CaptchaRatingPanel.this.isEnabled();
				}
			};

			int iteration = item.getIteration();

			// add the star image, which is either active (highlighted) or
			// inactive (no star)
			link.add(new WebMarkupContainer("star").add(new SimpleAttributeModifier("src",
					(onIsStarActive(iteration) ? getActiveStarUrl(iteration) : getInactiveStarUrl(iteration)))));
			link.setOutputMarkupId(true);
			item.add(link);
		}
	}
}
