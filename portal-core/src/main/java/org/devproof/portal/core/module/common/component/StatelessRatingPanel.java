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
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.rating.RatingPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.model.IModel;

/**
 * Is an extension of the rating panel Without ajax and with bookmarkable links
 * 
 * @author Carsten Hufe
 */
public abstract class StatelessRatingPanel extends RatingPanel {
	private static final long serialVersionUID = 1L;

	private IModel<Boolean> hasVoted;
	private PageParameters params;
	private Integer contentId;

	public StatelessRatingPanel(String id, IModel<Integer> rating, IModel<Integer> nrOfStars,
			IModel<Integer> nrOfVotes, IModel<Boolean> hasVoted, boolean addDefaultCssStyle, PageParameters params,
			Integer contentId) {
		super(id, rating, nrOfStars, nrOfVotes, hasVoted, addDefaultCssStyle);
		this.hasVoted = hasVoted;
		this.params = params;
		this.contentId = contentId;
		executeStatelessVoting(nrOfStars, hasVoted, params, contentId);
	}

	private void executeStatelessVoting(IModel<Integer> nrOfStars, IModel<Boolean> hasVoted, PageParameters params,
			Integer contentId) {
		if (params.containsKey("rateid") && params.containsKey("vote")) {
			Integer rateId = params.getAsInteger("rateid");
			Integer vote = params.getAsInteger("vote");
			if (vote > nrOfStars.getObject()) {
				vote = nrOfStars.getObject();
			}
			if (contentId.equals(rateId)) {
				hasVoted.setObject(Boolean.TRUE);
				onRated(vote + 1);
			}
		}
	}

	@Override
	protected Component newRatingStarBar(String id, IModel<Integer> nrOfStars) {
		return new StatelessRatingStarBar(id, nrOfStars);
	}

	/**
	 * Renders the stars and the links necessary for rating.
	 */
	private final class StatelessRatingStarBar extends Loop {
		/** For serialization. */
		private static final long serialVersionUID = 1L;

		private StatelessRatingStarBar(String id, IModel<Integer> model) {
			super(id, model);
		}

		@Override
		protected void populateItem(LoopItem item) {
			item.add(creatingStarBookmarkableLink(item));
		}

		private BookmarkablePageLink<Void> creatingStarBookmarkableLink(LoopItem item) {
			BookmarkablePageLink<Void> link = new BookmarkablePageLink<Void>("link", getPage().getClass());
			link.setEnabled(!hasVoted.getObject());
			link.setParameter("rateid", contentId);
			link.setParameter("vote", item.getIteration());
			copyParameterToLink(link);
			int iteration = item.getIteration();
			// add the star image, which is either active (highlighted) or
			// inactive (no star)
			link.add(new WebMarkupContainer("star").add(new SimpleAttributeModifier("src",
					(onIsStarActive(iteration) ? getActiveStarUrl(iteration) : getInactiveStarUrl(iteration)))));
			return link;
		}

		private void copyParameterToLink(BookmarkablePageLink<Void> link) {
			for (String key : params.keySet()) {
				link.setParameter(key, params.getString(key));
			}
		}
	}

	@Override
	protected void onRated(int rating, AjaxRequestTarget target) {
		this.onRated(rating);
	}

	protected abstract void onRated(int rating);
}
