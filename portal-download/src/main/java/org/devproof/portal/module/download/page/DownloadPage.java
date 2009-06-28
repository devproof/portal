/*
 * Copyright 2009 Carsten Hufe devproof.org
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
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
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
import org.devproof.portal.core.module.common.component.ExternalImage;
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

	public DownloadPage(final PageParameters params) {
		super(params);
		final PortalSession session = (PortalSession) getSession();
		final DownloadQuery query = new DownloadQuery();
		if (!session.hasRight("download.view")) {
			query.setRole(session.getRole());
		}
		if (!isAuthor() && this.configurationService.findAsBoolean(DownloadConstants.CONF_DOWNLOAD_HIDE_BROKEN)) {
			query.setBroken(false);
		}

		this.downloadDataProvider.setQueryObject(query);
		final DownloadDataView dataView = new DownloadDataView("listDownload", params);
		this.add(dataView);
		addFilterBox(new DownloadSearchBoxPanel("box", query, this.downloadDataProvider, this, dataView, params));

		this.add(new BookmarkablePagingPanel("paging", dataView, DownloadPage.class, params));
		this.addTagCloudBox(this.downloadTagService, new PropertyModel<DownloadTagEntity>(query, "tag"), DownloadPage.class, params);
		// link from upload center
		if (params != null && params.containsKey("create")) {
			final DownloadEntity newDownload = this.downloadService.newDownloadEntity();
			newDownload.setUrl(params.getString("create"));
			this.setResponsePage(new DownloadEditPage(newDownload));
		}
	}

	private class DownloadDataView extends DataView<DownloadEntity> {
		private static final long serialVersionUID = 1L;
		private final PageParameters params;
		private final boolean onlyOne;

		public DownloadDataView(final String id, final PageParameters params) {
			super(id, DownloadPage.this.downloadDataProvider);
			this.params = params;
			this.onlyOne = DownloadPage.this.downloadDataProvider.size() == 1;
			setItemsPerPage(DownloadPage.this.configurationService.findAsInteger(DownloadConstants.CONF_DOWNLOADS_PER_PAGE));
		}

		@Override
		protected void populateItem(final Item<DownloadEntity> item) {
			final DownloadEntity download = item.getModelObject();
			item.setOutputMarkupId(true);
			if (this.onlyOne) {
				setPageTitle(download.getTitle());
			}

			final DownloadView downloadViewPanel = new DownloadView("downloadView", download, this.params);
			if (isAuthor()) {
				downloadViewPanel.addOrReplace(new AuthorPanel<DownloadEntity>("authorButtons", download) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onDelete(final AjaxRequestTarget target) {
						DownloadPage.this.downloadService.delete(getEntity());
						item.setVisible(false);
						target.addComponent(item);
						target.addComponent(getFeedback());
						info(this.getString("msg.deleted"));
					}

					@Override
					public void onEdit(final AjaxRequestTarget target) {
						this.setResponsePage(new DownloadEditPage(download));
					}
				});
			}
			item.add(downloadViewPanel);
		}
	};

	public class DownloadView extends Fragment {

		private static final long serialVersionUID = 1L;

		private final Model<Boolean> hasVoted = Model.of(Boolean.FALSE);

		public DownloadView(final String id, final DownloadEntity downloadEntity, final PageParameters params) {
			super(id, "downloadView", DownloadPage.this);
			final PortalSession session = (PortalSession) getSession();
			final boolean voteEnabled = DownloadPage.this.configurationService.findAsBoolean(DownloadConstants.CONF_DOWNLOAD_VOTE_ENABLED);
			final boolean allowedToDownload = session.hasRight("download.download", downloadEntity.getDownloadRights());
			final boolean allowedToVote = session.hasRight("download.vote", downloadEntity.getVoteRights());
			this.hasVoted.setObject(!allowedToVote);
			this.add(new WebMarkupContainer("authorButtons"));
			this.add(new Label("broken", DownloadPage.this.getString("broken")).setVisible(downloadEntity.getBroken() != null && downloadEntity.getBroken()));
			final BookmarkablePageLink<DownloadRedirectPage> titleLink = new BookmarkablePageLink<DownloadRedirectPage>("titleLink", DownloadRedirectPage.class);
			titleLink.setParameter("0", downloadEntity.getId());
			titleLink.setEnabled(allowedToDownload);
			titleLink.setEnabled(allowedToDownload);
			titleLink.add(new Label("titleLabel", downloadEntity.getTitle()));
			this.add(titleLink);
			this.add(new MetaInfoPanel("metaInfo", downloadEntity));
			this.add(new ExtendedLabel("description", downloadEntity.getDescription()));
			this.add(new Label("hits", String.valueOf(downloadEntity.getHits())));
			this.add(new ContentTagPanel<DownloadTagEntity>("tags", new ListModel<DownloadTagEntity>(downloadEntity.getTags()), DownloadPage.class, params));
			this.add(new StatelessRatingPanel("vote", new PropertyModel<Integer>(downloadEntity, "calculatedRating"), Model.of(5), new PropertyModel<Integer>(downloadEntity, "numberOfVotes"),
					this.hasVoted, true, params, downloadEntity.getId()) {
				private static final long serialVersionUID = 1L;

				@Override
				protected boolean onIsStarActive(final int star) {
					return star < ((int) (downloadEntity.getCalculatedRating() + 0.5));
				}

				@Override
				protected void onRated(final int rating) {
					if (allowedToVote) {
						DownloadView.this.hasVoted.setObject(Boolean.TRUE);
						DownloadPage.this.downloadService.rateDownload(rating, downloadEntity);
						info(DownloadPage.this.getString("voteCounted"));
					}
				}
			}.setVisible(voteEnabled));

			final BookmarkablePageLink<?> downloadLink = new BookmarkablePageLink<Void>("downloadLink", DownloadRedirectPage.class);
			downloadLink.add(new ExternalImage("downloadImage", DeadLinkCheckConstants.REF_DOWNLOAD_IMG));
			downloadLink.setParameter("0", downloadEntity.getId());

			final String labelKey = allowedToDownload ? "downloadNow" : "loginToDownload";

			final Label downloadLinkLabel = new Label("downloadLinkLabel", DownloadPage.this.getString(labelKey));
			this.add(downloadLink.add(downloadLinkLabel).setEnabled(allowedToDownload));

			final String[] infoFields = new String[] { "softwareVersion", "downloadSize", "manufacturer", "licence", "price" };

			final RepeatingView repeating = new RepeatingView("infoFieldsRepeating");

			for (final String fieldName : infoFields) {
				try {
					final String getter = PortalUtil.addGet(fieldName);
					final Method method = ReflectionUtils.findMethod(DownloadEntity.class, getter);
					final String value = (String) method.invoke(downloadEntity);
					if (StringUtils.isNotEmpty(value)) {
						final WebMarkupContainer item = new WebMarkupContainer(repeating.newChildId());
						repeating.add(item);
						item.add(new Label("label", DownloadPage.this.getString(fieldName)));
						if ("manufacturer".equals(fieldName) && StringUtils.isNotEmpty(downloadEntity.getManufacturerHomepage())) {
							final ExternalLink link = new ExternalLink("link", downloadEntity.getManufacturerHomepage(), value);
							item.add(new Fragment("info", "linkFragment", DownloadPage.this).add(link));
						} else {
							item.add(new Fragment("info", "labelFragment", DownloadPage.this).add(new Label("label", value)));
						}
					}
				} catch (final IllegalArgumentException e) {
					throw new UnhandledException(e);
				} catch (final IllegalAccessException e) {
					throw new UnhandledException(e);
				} catch (final InvocationTargetException e) {
					throw new UnhandledException(e);
				}
			}
			this.add(repeating);
		}
	}
}
