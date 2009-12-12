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
package org.devproof.portal.module.download.page;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.component.StatelessRatingPanel;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.BookmarkablePagingPanel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.tag.panel.ContentTagPanel;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.deadlinkcheck.DeadLinkCheckConstants;
import org.devproof.portal.module.download.DownloadConstants;
import org.devproof.portal.module.download.entity.DownloadEntity;
import org.devproof.portal.module.download.entity.DownloadTagEntity;
import org.devproof.portal.module.download.panel.DownloadSearchBoxPanel;
import org.devproof.portal.module.download.query.DownloadQuery;
import org.devproof.portal.module.download.service.DownloadService;
import org.springframework.util.ReflectionUtils;

/**
 * @author Carsten Hufe
 */
public class DownloadPage extends DownloadBasePage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "downloadService")
	private DownloadService downloadService;
	@SpringBean(name = "downloadDataProvider")
	private QueryDataProvider<DownloadEntity> downloadDataProvider;
	@SpringBean(name = "downloadTagService")
	private TagService<DownloadTagEntity> downloadTagService;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;

	private final DownloadDataView dataView;
	private final DownloadQuery query;
	private final PageParameters params;

	public DownloadPage(PageParameters params) {
		super(params);
		this.params = params;
		query = createDownloadQuery();
		add(dataView = createDownloadDataView());
		addFilterBox(createDownloadSearchBoxPanel());

		add(createPagingPanel());
		addTagCloudBox();
		redirectToCreateDownloadPage();
	}

	private void redirectToCreateDownloadPage() {
		// link from upload center
		if (isCreateLinkFromUploadCenter() && isAuthor()) {
			DownloadEntity newDownload = downloadService.newDownloadEntity();
			newDownload.setUrl(params.getString("create"));
			setResponsePage(new DownloadEditPage(newDownload));
		}
	}

	private boolean isCreateLinkFromUploadCenter() {
		return params.containsKey("create");
	}

	private void addTagCloudBox() {
		addTagCloudBox(downloadTagService, new PropertyModel<DownloadTagEntity>(query, "tag"), DownloadPage.class,
				params);
	}

	private BookmarkablePagingPanel createPagingPanel() {
		return new BookmarkablePagingPanel("paging", dataView, DownloadPage.class, params);
	}

	private DownloadSearchBoxPanel createDownloadSearchBoxPanel() {
		return new DownloadSearchBoxPanel("box", query, downloadDataProvider, this, dataView, params);
	}

	private DownloadDataView createDownloadDataView() {
		DownloadDataView dataView = new DownloadDataView("listDownload");
		return dataView;
	}

	private DownloadQuery createDownloadQuery() {
		final PortalSession session = (PortalSession) getSession();
		final DownloadQuery query = new DownloadQuery();

		if (!session.hasRight("download.view")) {
			query.setRole(session.getRole());
		}
		if (!isAuthor() && configurationService.findAsBoolean(DownloadConstants.CONF_DOWNLOAD_HIDE_BROKEN)) {
			query.setBroken(false);
		}
		downloadDataProvider.setQueryObject(query);
		return query;
	}

	private class DownloadDataView extends DataView<DownloadEntity> {
		private static final long serialVersionUID = 1L;
		private final boolean onlyOneDownloadInResult;

		public DownloadDataView(final String id) {
			super(id, downloadDataProvider);
			onlyOneDownloadInResult = downloadDataProvider.size() == 1;
			setItemsPerPage(configurationService.findAsInteger(DownloadConstants.CONF_DOWNLOADS_PER_PAGE));
		}

		@Override
		protected void populateItem(final Item<DownloadEntity> item) {
			setDownloadNameAsPageTitle(item);
			item.setOutputMarkupId(true);
			item.add(createDownloadViewPanel(item));
		}

		private DownloadView createDownloadViewPanel(final Item<DownloadEntity> item) {
			return new DownloadView("downloadView", item);
		}

		private void setDownloadNameAsPageTitle(final Item<DownloadEntity> item) {
			if (onlyOneDownloadInResult) {
				DownloadEntity download = item.getModelObject();
				setPageTitle(download.getTitle());
			}
		}
	};

	public class DownloadView extends Fragment {

		private static final long serialVersionUID = 1L;

		private final Model<Boolean> hasVoted;
		private DownloadEntity download;

		public DownloadView(String id, Item<DownloadEntity> item) {
			super(id, "downloadView", DownloadPage.this);
			download = item.getModelObject();
			hasVoted = Model.of(!isAllowedToVote());

			add(createBrokenLabel());
			add(createTitleLink());
			add(createAppropriateAuthorPanel(item));
			add(createMetaInfoPanel());
			add(createDescriptionLabel());
			add(createHitsLabel());
			add(createTagPanel());
			add(createRatingPanel());
			add(createOptionalInfoLabels());
			add(createDownloadLink());
		}

		private Component createAppropriateAuthorPanel(final Item<DownloadEntity> item) {
			if (isAuthor()) {
				return createAuthorPanel(item);
			} else {
				return createEmptyAuthorPanel();
			}
		}

		private AuthorPanel<DownloadEntity> createAuthorPanel(final Item<DownloadEntity> item) {
			return new AuthorPanel<DownloadEntity>("authorButtons", download) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onDelete(final AjaxRequestTarget target) {
					downloadService.delete(getEntity());
					item.setVisible(false);
					target.addComponent(item);
					target.addComponent(getFeedback());
					info(getString("msg.deleted"));
				}

				@Override
				public void onEdit(final AjaxRequestTarget target) {
					DownloadEntity refreshedDownload = downloadService.findById(download.getId());
					setResponsePage(new DownloadEditPage(refreshedDownload));
				}
			};
		}

		private WebMarkupContainer createEmptyAuthorPanel() {
			return new WebMarkupContainer("authorButtons");
		}

		private RepeatingView createOptionalInfoLabels() {
			String[] infoFields = new String[] { "softwareVersion", "downloadSize", "manufacturer", "licence", "price" };
			RepeatingView repeating = new RepeatingView("infoFieldsRepeating");
			for (String fieldName : infoFields) {
				try {
					String getter = PortalUtil.addGet(fieldName);
					Method method = ReflectionUtils.findMethod(DownloadEntity.class, getter);
					String value = (String) method.invoke(download);
					if (StringUtils.isNotEmpty(value)) {
						repeating.add(createInfoLine(repeating.newChildId(), fieldName, value));
					}
				} catch (final IllegalArgumentException e) {
					throw new UnhandledException(e);
				} catch (final IllegalAccessException e) {
					throw new UnhandledException(e);
				} catch (final InvocationTargetException e) {
					throw new UnhandledException(e);
				}
			}
			return repeating;
		}

		private WebMarkupContainer createInfoLine(String id, String fieldName, String value) {
			WebMarkupContainer infoLine = new WebMarkupContainer(id);
			infoLine.add(new Label("label", DownloadPage.this.getString(fieldName)));
			if (isManufacturerHomepage(fieldName)) {
				infoLine.add(createLinkFragment(value));
			} else {
				infoLine.add(createLabelFragment(value));
			}
			return infoLine;
		}

		private Fragment createLabelFragment(String value) {
			Fragment fragment = new Fragment("info", "labelFragment", DownloadPage.this);
			fragment.add(new Label("label", value));
			return fragment;
		}

		private Fragment createLinkFragment(String value) {
			Fragment fragment = new Fragment("info", "linkFragment", DownloadPage.this);
			fragment.add(createManufacturerLink(value));
			return fragment;
		}

		private ExternalLink createManufacturerLink(String value) {
			ExternalLink link = new ExternalLink("link", download.getManufacturerHomepage(), value);
			return link;
		}

		private boolean isManufacturerHomepage(String fieldName) {
			return "manufacturer".equals(fieldName) && StringUtils.isNotEmpty(download.getManufacturerHomepage());
		}

		private BookmarkablePageLink<?> createDownloadLink() {
			BookmarkablePageLink<?> downloadLink = new BookmarkablePageLink<Void>("downloadLink",
					DownloadRedirectPage.class);
			downloadLink.add(createDownloadLinkImage());
			downloadLink.add(createDownloadLinkLabel());
			downloadLink.setParameter("0", download.getId());
			downloadLink.setEnabled(isAllowedToDownload());
			return downloadLink;
		}

		private Label createDownloadLinkLabel() {
			String labelKey = isAllowedToDownload() ? "downloadNow" : "loginToDownload";
			Label downloadLinkLabel = new Label("downloadLinkLabel", DownloadPage.this.getString(labelKey));
			return downloadLinkLabel;
		}

		private Image createDownloadLinkImage() {
			return new Image("downloadImage", DeadLinkCheckConstants.REF_DOWNLOAD_IMG);
		}

		private Component createRatingPanel() {
			StatelessRatingPanel ratingPanel = newRatingPanel();
			ratingPanel.setVisible(isVoteEnabled());
			return ratingPanel;
		}

		private StatelessRatingPanel newRatingPanel() {
			StatelessRatingPanel ratingPanel = new StatelessRatingPanel("vote", new PropertyModel<Integer>(download,
					"calculatedRating"), Model.of(5), new PropertyModel<Integer>(download, "numberOfVotes"), hasVoted,
					true, params, download.getId()) {
				private static final long serialVersionUID = 1L;

				@Override
				protected boolean onIsStarActive(final int star) {
					return star < ((int) (download.getCalculatedRating() + 0.5));
				}

				@Override
				protected void onRated(final int rating) {
					if (isAllowedToDownload()) {
						hasVoted.setObject(Boolean.TRUE);
						downloadService.rateDownload(rating, download);
						info(DownloadPage.this.getString("voteCounted"));
					}
				}
			};
			return ratingPanel;
		}

		private ContentTagPanel<DownloadTagEntity> createTagPanel() {
			return new ContentTagPanel<DownloadTagEntity>("tags", new ListModel<DownloadTagEntity>(download.getTags()),
					DownloadPage.class, params);
		}

		private Label createHitsLabel() {
			return new Label("hits", String.valueOf(download.getHits()));
		}

		private ExtendedLabel createDescriptionLabel() {
			return new ExtendedLabel("description", download.getDescription());
		}

		private MetaInfoPanel createMetaInfoPanel() {
			return new MetaInfoPanel("metaInfo", download);
		}

		private boolean isVoteEnabled() {
			return configurationService.findAsBoolean(DownloadConstants.CONF_DOWNLOAD_VOTE_ENABLED);
		}

		private BookmarkablePageLink<DownloadRedirectPage> createTitleLink() {
			BookmarkablePageLink<DownloadRedirectPage> titleLink = new BookmarkablePageLink<DownloadRedirectPage>(
					"titleLink", DownloadRedirectPage.class);
			titleLink.setParameter("0", download.getId());
			titleLink.setEnabled(isAllowedToDownload());
			titleLink.add(createTitleLabel());
			return titleLink;
		}

		private Label createTitleLabel() {
			return new Label("titleLabel", download.getTitle());
		}

		private Component createBrokenLabel() {
			return new Label("broken", DownloadPage.this.getString("broken")).setVisible(download.getBroken() != null
					&& download.getBroken());
		}

		private boolean isAllowedToDownload() {
			PortalSession session = (PortalSession) getSession();
			return session.hasRight("download.download", download.getDownloadRights());
		}

		private boolean isAllowedToVote() {
			PortalSession session = (PortalSession) getSession();
			return session.hasRight("download.vote", download.getVoteRights());
		}
	}
}
