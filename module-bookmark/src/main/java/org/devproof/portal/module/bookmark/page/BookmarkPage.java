/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.module.bookmark.page;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.*;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.module.common.component.AutoPagingDataView;
import org.devproof.portal.core.module.common.component.CaptchaRatingPanel;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.BookmarkablePagingPanel;
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.tag.panel.TagCloudBoxPanel;
import org.devproof.portal.core.module.tag.panel.TagContentPanel;
import org.devproof.portal.module.bookmark.BookmarkConstants;
import org.devproof.portal.module.bookmark.entity.Bookmark;
import org.devproof.portal.module.bookmark.entity.Bookmark.Source;
import org.devproof.portal.module.bookmark.entity.BookmarkTag;
import org.devproof.portal.module.bookmark.panel.BookmarkSearchBoxPanel;
import org.devproof.portal.module.bookmark.query.BookmarkQuery;
import org.devproof.portal.module.bookmark.service.BookmarkService;
import org.devproof.portal.module.bookmark.service.BookmarkTagService;

import java.util.Iterator;
import java.util.List;

/**
 * @author Carsten Hufe
 */
@ModulePage(mountPath = "/bookmarks", registerMainNavigationLink = true)
public class BookmarkPage extends BookmarkBasePage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "bookmarkService")
	private BookmarkService bookmarkService;
	@SpringBean(name = "bookmarkDataProvider")
	private QueryDataProvider<Bookmark, BookmarkQuery> bookmarkDataProvider;
	@SpringBean(name = "bookmarkTagService")
	private BookmarkTagService bookmarkTagService;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;

	private BookmarkDataView dataView;
	private IModel<BookmarkQuery> searchQueryModel;
	private BubblePanel bubblePanel;

	public BookmarkPage(PageParameters params) {
		super(params);
		searchQueryModel = bookmarkDataProvider.getSearchQueryModel();
		add(createBubblePanel());
		add(createRepeatingBookmarks());
		add(createPagingPanel());
	}

    @Override
    protected Component newTagCloudBox(String markupId) {
        return createTagCloudBox(markupId);
    }

    @Override
    protected Component newFilterBox(String markupId) {
        return createBookmarkSearchBoxPanel(markupId);
    }

    private BubblePanel createBubblePanel() {
		bubblePanel = new BubblePanel("bubble");
		return bubblePanel;
	}

    private Component createTagCloudBox(String markupId) {
        return new TagCloudBoxPanel<BookmarkTag>(markupId, bookmarkTagService, getClass());
    }

	private BookmarkablePagingPanel createPagingPanel() {
		return new BookmarkablePagingPanel("paging", dataView, searchQueryModel, BookmarkPage.class);
	}

	private BookmarkSearchBoxPanel createBookmarkSearchBoxPanel(String markupId) {
		return new BookmarkSearchBoxPanel("box", searchQueryModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean isAuthor() {
				return BookmarkPage.this.isAuthor();
			}
		};
	}

	@Override
	public String getPageTitle() {
		if (bookmarkDataProvider.size() == 1) {
			Iterator<? extends Bookmark> it = bookmarkDataProvider.iterator(0, 1);
			Bookmark bookmark = it.next();
			return bookmark.getTitle();
		}
		return "";
	}

	private BookmarkDataView createRepeatingBookmarks() {
		dataView = new BookmarkDataView("repeatingBookmarks");
		return dataView;
	}

	private class BookmarkDataView extends AutoPagingDataView<Bookmark> {
		private static final long serialVersionUID = 1L;

		public BookmarkDataView(String id) {
			super(id, bookmarkDataProvider);
			setItemsPerPage(configurationService.findAsInteger(BookmarkConstants.CONF_BOOKMARKS_PER_PAGE));
			setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
		}

		@Override
		protected void populateItem(Item<Bookmark> item) {
			item.add(createBookmarkView(item));
			item.setOutputMarkupId(true);
		}

		private BookmarkView createBookmarkView(Item<Bookmark> item) {
			return new BookmarkView("bookmarkView", item);
		}
	}

	private class BookmarkView extends Fragment {
		private static final long serialVersionUID = 1L;
		private IModel<Boolean> hasVoted;
		private IModel<Bookmark> bookmarkModel;

		public BookmarkView(String id, Item<Bookmark> item) {
			super(id, "bookmarkView", BookmarkPage.this);
			bookmarkModel = item.getModel();
			hasVoted = Model.of(!isAllowedToVote());
			add(createBrokenLabel());
			add(createTitleLink());
			add(createDeliciousSourceImage());
			add(createAppropriateAuthorPanel(item));
			add(createMetaInfoPanel());
			add(createDescriptionLabel());
			add(createHitsLabel());
			add(createTagPanel());
			add(createRatingPanel());
			add(createVisitLink());
		}

		private BookmarkablePageLink<?> createVisitLink() {
			Bookmark bookmark = bookmarkModel.getObject();
			BookmarkablePageLink<?> visitLink = newVisitLink();
			visitLink.add(createVisitLinkImage());
			visitLink.add(createVisitLinkLabel());
			visitLink.setParameter("0", bookmark.getId());
			return visitLink;
		}

		private BookmarkablePageLink<?> newVisitLink() {
			return new BookmarkablePageLink<Void>("bookmarkLink", BookmarkRedirectPage.class) {
				private static final long serialVersionUID = -5119163785575238506L;

				@Override
				public boolean isEnabled() {
					return isAllowedToVisit();
				}
			};
		}

		private Image createVisitLinkImage() {
			return new Image("bookmarkImage", BookmarkConstants.REF_LINK_IMG);
		}

		private Label createVisitLinkLabel() {
			IModel<String> linkModel = createVisitLinkModel();
			return new Label("bookmarkLinkLabel", linkModel);
		}

		private IModel<String> createVisitLinkModel() {
			return new AbstractReadOnlyModel<String>() {
				private static final long serialVersionUID = -1310603186873782577L;

				@Override
				public String getObject() {
					String labelKey = isAllowedToVisit() ? "visitNow" : "loginToVisit";
					return BookmarkPage.this.getString(labelKey);
				}
			};
		}

		private Component createRatingPanel() {
			IModel<Integer> calculatedRatingModel = new PropertyModel<Integer>(bookmarkModel, "calculatedRating");
			IModel<Integer> numberOfVotesModel = new PropertyModel<Integer>(bookmarkModel, "numberOfVotes");
			return new CaptchaRatingPanel("vote", calculatedRatingModel, Model.of(5), numberOfVotesModel, hasVoted,
					true, bubblePanel) {
				private static final long serialVersionUID = 2020860765811355013L;

				@Override
				protected boolean onIsStarActive(int star) {
					Bookmark bookmark = bookmarkModel.getObject();
					return star < ((int) (bookmark.getCalculatedRating() + 0.5));
				}

				@Override
				protected void onRatedAndCaptchaValidated(int rating, AjaxRequestTarget target) {
					Bookmark bookmark = bookmarkModel.getObject();
					hasVoted.setObject(Boolean.TRUE);
					bookmarkService.rateBookmark(rating, bookmark);
				}

				@Override
				public boolean isEnabled() {
					return isAllowedToVote();
				}

				@Override
				public boolean isVisible() {
					return isVoteEnabled();
				}
			};
		}

		private boolean isAllowedToVote() {
			Bookmark bookmark = bookmarkModel.getObject();
			PortalSession session = (PortalSession) getSession();
			return session.hasRight("bookmark.vote", bookmark.getVoteRights());
		}

		private boolean isVoteEnabled() {
			return configurationService.findAsBoolean(BookmarkConstants.CONF_BOOKMARK_VOTE_ENABLED);
		}

		private TagContentPanel<BookmarkTag> createTagPanel() {
			IModel<List<BookmarkTag>> tagsModel = new PropertyModel<List<BookmarkTag>>(bookmarkModel,
					"tags");
			return new TagContentPanel<BookmarkTag>("tags", tagsModel, BookmarkPage.class);
		}

		private Label createHitsLabel() {
			IModel<Integer> hitsModel = new PropertyModel<Integer>(bookmarkModel, "hits");
			return new Label("hits", hitsModel);
		}

		private ExtendedLabel createDescriptionLabel() {
			IModel<String> descriptionModel = new PropertyModel<String>(bookmarkModel, "description");
			return new ExtendedLabel("description", descriptionModel);
		}

		private Component createDeliciousSourceImage() {
			Bookmark bookmark = bookmarkModel.getObject();
			if (isAuthor() && bookmark.getSource() == Source.DELICIOUS) {
				// be aware... images are stateful
				return new Image("delicious", BookmarkConstants.REF_DELICIOUS_IMG);
			} else {
				Component hiddenImage = new WebMarkupContainer("delicious");
				hiddenImage.setVisible(false);
				return hiddenImage;
			}
		}

		private MetaInfoPanel<Bookmark> createMetaInfoPanel() {
			return new MetaInfoPanel<Bookmark>("metaInfo", bookmarkModel);
		}

		private BookmarkablePageLink<BookmarkRedirectPage> createTitleLink() {
			Bookmark bookmark = bookmarkModel.getObject();
			BookmarkablePageLink<BookmarkRedirectPage> titleLink = newTitleLink();
			titleLink.setParameter("0", bookmark.getId());
			titleLink.add(createTitleLabel());
			return titleLink;
		}

		private BookmarkablePageLink<BookmarkRedirectPage> newTitleLink() {
			return new BookmarkablePageLink<BookmarkRedirectPage>("titleLink", BookmarkRedirectPage.class) {
				private static final long serialVersionUID = 8468466974865735414L;

				@Override
				public boolean isEnabled() {
					return isAllowedToVisit();
				}
			};
		}

		private Label createTitleLabel() {
			return new Label("titleLabel", new PropertyModel<String>(bookmarkModel, "title"));
		}

		private boolean isAllowedToVisit() {
			Bookmark bookmark = bookmarkModel.getObject();
			PortalSession session = (PortalSession) getSession();
			return session.hasRight("bookmark.visit", bookmark.getVisitRights());
		}

		private Component createBrokenLabel() {
			return new Label("broken", BookmarkPage.this.getString("broken")) {
				private static final long serialVersionUID = 6476871098870011172L;

				@Override
				public boolean isVisible() {
					Bookmark bookmark = bookmarkModel.getObject();
					return bookmark.getBroken() != null && bookmark.getBroken();
				}
			};
		}

		private Component createAppropriateAuthorPanel(Item<Bookmark> item) {
			if (isAuthor()) {
				return createAuthorPanel(item);
			} else {
				return createEmptyAuthorPanel();
			}
		}

		private AuthorPanel<Bookmark> createAuthorPanel(final Item<Bookmark> item) {
			return new AuthorPanel<Bookmark>("authorButtons", bookmarkModel) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onDelete(AjaxRequestTarget target) {
					bookmarkService.delete(bookmarkModel.getObject());
					item.setVisible(false);
					target.addComponent(item);
					target.addComponent(getFeedback());
					info(getString("msg.deleted"));
				}

				@Override
				public void onEdit(AjaxRequestTarget target) {
					IModel<Bookmark> bookmarkModel = createBookmarkModel();
					setResponsePage(new BookmarkEditPage(bookmarkModel));
				}
			};
		}

		private IModel<Bookmark> createBookmarkModel() {
			return new LoadableDetachableModel<Bookmark>() {
				private static final long serialVersionUID = 8475595194139413544L;

				@Override
				protected Bookmark load() {
					Bookmark bookmark = bookmarkModel.getObject();
					return bookmarkService.findById(bookmark.getId());
				}
			};
		}

		private WebMarkupContainer createEmptyAuthorPanel() {
			return new WebMarkupContainer("authorButtons");
		}
	}
}
