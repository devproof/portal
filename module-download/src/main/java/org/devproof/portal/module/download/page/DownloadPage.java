/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.module.download.page;

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
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.*;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.component.AutoPagingDataView;
import org.devproof.portal.core.module.common.component.CaptchaRatingPanel;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.BookmarkablePagingPanel;
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.tag.panel.TagContentPanel;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.download.DownloadConstants;
import org.devproof.portal.module.download.entity.DownloadEntity;
import org.devproof.portal.module.download.entity.DownloadTagEntity;
import org.devproof.portal.module.download.panel.DownloadSearchBoxPanel;
import org.devproof.portal.module.download.query.DownloadQuery;
import org.devproof.portal.module.download.service.DownloadService;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * @author Carsten Hufe
 */
public class DownloadPage extends DownloadBasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "downloadService")
    private DownloadService downloadService;
    @SpringBean(name = "downloadDataProvider")
    private QueryDataProvider<DownloadEntity, DownloadQuery> downloadDataProvider;
    @SpringBean(name = "downloadTagService")
    private TagService<DownloadTagEntity> downloadTagService;
    @SpringBean(name = "configurationService")
    private ConfigurationService configurationService;

    private IModel<DownloadQuery> queryModel;
    private DownloadDataView dataView;
    private BubblePanel bubblePanel;
    private PageParameters params;

    public DownloadPage(PageParameters params) {
        super(params);
        this.params = params;
        this.queryModel = downloadDataProvider.getSearchQueryModel();
        add(createBubblePanel());
        add(createRepeatingDownloads());
        add(createPagingPanel());
        addFilterBox(createDownloadSearchBoxPanel());
        addTagCloudBox();
    }

    @Override
    protected void onBeforeRender() {
        redirectToCreateDownloadPage();
        super.onBeforeRender();
    }

    private BubblePanel createBubblePanel() {
        bubblePanel = new BubblePanel("bubble");
        return bubblePanel;
    }

    private void redirectToCreateDownloadPage() {
        // link from upload center
        if (isCreateLinkFromUploadCenter() && isAuthor()) {
            DownloadEntity newDownload = downloadService.newDownloadEntity();
            newDownload.setUrl(params.getString("create"));
            IModel<DownloadEntity> downloadModel = Model.of(newDownload);
            setResponsePage(new DownloadEditPage(downloadModel));
        }
    }

    private boolean isCreateLinkFromUploadCenter() {
        return params.containsKey("create");
    }

    private void addTagCloudBox() {
        addTagCloudBox(downloadTagService, DownloadPage.class);
    }

    private BookmarkablePagingPanel createPagingPanel() {
        return new BookmarkablePagingPanel("paging", dataView, queryModel, DownloadPage.class);
    }

    private DownloadSearchBoxPanel createDownloadSearchBoxPanel() {
        return new DownloadSearchBoxPanel("box", queryModel) {
            private static final long serialVersionUID = -4167284441561354178L;

            @Override
            protected boolean isAuthor() {
                return DownloadPage.this.isAuthor();
            }
        };
    }

    @Override
    public String getPageTitle() {
        if (downloadDataProvider.size() == 1) {
            Iterator<? extends DownloadEntity> it = downloadDataProvider.iterator(0, 1);
            DownloadEntity download = it.next();
            return download.getTitle();
        }
        return "";
    }

    private DownloadDataView createRepeatingDownloads() {
        dataView = new DownloadDataView("repeatingDownloads");
        return dataView;
    }

    private class DownloadDataView extends AutoPagingDataView<DownloadEntity> {
        private static final long serialVersionUID = 1L;

        public DownloadDataView(String id) {
            super(id, downloadDataProvider);
            setItemsPerPage(configurationService.findAsInteger(DownloadConstants.CONF_DOWNLOADS_PER_PAGE));
            setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
        }

        @Override
        protected void populateItem(Item<DownloadEntity> item) {
            item.add(createDownloadViewPanel(item));
            item.setOutputMarkupId(true);
        }

        private DownloadView createDownloadViewPanel(Item<DownloadEntity> item) {
            return new DownloadView("downloadView", item);
        }
    }

    public class DownloadView extends Fragment {
        private static final long serialVersionUID = 1L;
        private IModel<Boolean> hasVoted;
        private IModel<DownloadEntity> downloadModel;

        public DownloadView(String id, Item<DownloadEntity> item) {
            super(id, "downloadView", DownloadPage.this);
            downloadModel = item.getModel();
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

        private Component createAppropriateAuthorPanel(Item<DownloadEntity> item) {
            if (isAuthor()) {
                return createAuthorPanel(item);
            } else {
                return createEmptyAuthorPanel();
            }
        }

        private AuthorPanel<DownloadEntity> createAuthorPanel(final Item<DownloadEntity> item) {
            return new AuthorPanel<DownloadEntity>("authorButtons", downloadModel) {
                private static final long serialVersionUID = 1L;

                @Override
                public void onDelete(AjaxRequestTarget target) {
                    downloadService.delete(downloadModel.getObject());
                    item.setVisible(false);
                    target.addComponent(item);
                    target.addComponent(getFeedback());
                    info(getString("msg.deleted"));
                }

                @Override
                public void onEdit(AjaxRequestTarget target) {
                    IModel<DownloadEntity> reloadedDownloadModel = createDownloadModel();
                    setResponsePage(new DownloadEditPage(reloadedDownloadModel));
                }
            };
        }

        private IModel<DownloadEntity> createDownloadModel() {
            return new LoadableDetachableModel<DownloadEntity>() {
                private static final long serialVersionUID = -2683624402683569635L;
                @Override
                protected DownloadEntity load() {
                    DownloadEntity download = downloadModel.getObject();
                    return downloadService.findById(download.getId());
                }
            };
        }

        private WebMarkupContainer createEmptyAuthorPanel() {
            return new WebMarkupContainer("authorButtons");
        }

        private RepeatingView createOptionalInfoLabels() {
            String[] infoFields = new String[]{"softwareVersion", "downloadSize", "manufacturer", "licence", "price"};
            RepeatingView repeating = new RepeatingView("repeatingInfoFields");
            DownloadEntity download = downloadModel.getObject();
            for (String fieldName : infoFields) {
                try {
                    String getter = PortalUtil.addGet(fieldName);
                    Method method = ReflectionUtils.findMethod(DownloadEntity.class, getter);
                    String value = (String) method.invoke(download);
                    if (StringUtils.isNotEmpty(value)) {
                        repeating.add(createInfoLine(repeating.newChildId(), fieldName, value));
                    }
                } catch (IllegalArgumentException e) {
                    throw new UnhandledException(e);
                } catch (IllegalAccessException e) {
                    throw new UnhandledException(e);
                } catch (InvocationTargetException e) {
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
            DownloadEntity download = downloadModel.getObject();
            return new ExternalLink("link", download.getManufacturerHomepage(), value);
        }

        private boolean isManufacturerHomepage(String fieldName) {
            DownloadEntity download = downloadModel.getObject();
            return "manufacturer".equals(fieldName) && StringUtils.isNotEmpty(download.getManufacturerHomepage());
        }

        private BookmarkablePageLink<?> createDownloadLink() {
            DownloadEntity download = downloadModel.getObject();
            BookmarkablePageLink<?> downloadLink = newDownloadLink();
            downloadLink.add(createDownloadLinkImage());
            downloadLink.add(createDownloadLinkLabel());
            downloadLink.setParameter("0", download.getId());
            return downloadLink;
        }

        private BookmarkablePageLink<?> newDownloadLink() {
            return new BookmarkablePageLink<Void>("downloadLink", DownloadRedirectPage.class) {
                private static final long serialVersionUID = -773349636185304837L;

                @Override
                public boolean isEnabled() {
                    return isAllowedToDownload();
                }
            };
        }

        private Label createDownloadLinkLabel() {
            IModel<String> downloadLinkLabelModel = createDownloadLinkLabelModel();
            return new Label("downloadLinkLabel", downloadLinkLabelModel);
        }

        private IModel<String> createDownloadLinkLabelModel() {
            return new AbstractReadOnlyModel<String>() {
                private static final long serialVersionUID = 8934555375794694831L;
                @Override
                public String getObject() {
                    String labelKey = isAllowedToDownload() ? "downloadNow" : "loginToDownload";
                    return DownloadPage.this.getString(labelKey);
                }
            };
        }

        private Image createDownloadLinkImage() {
            return new Image("downloadImage", DownloadConstants.REF_DOWNLOAD_IMG);
        }

        private Component createRatingPanel() {
            CaptchaRatingPanel ratingPanel = newRatingPanel();
            ratingPanel.setVisible(isVoteEnabled());
            return ratingPanel;
        }

        private CaptchaRatingPanel newRatingPanel() {
            IModel<Integer> calculatedRatingModel = new PropertyModel<Integer>(downloadModel, "calculatedRating");
            IModel<Integer> numberOfVotesModel = new PropertyModel<Integer>(downloadModel, "numberOfVotes");
            return new CaptchaRatingPanel("vote", calculatedRatingModel, Model.of(5), numberOfVotesModel, hasVoted,
                    true, bubblePanel) {
                private static final long serialVersionUID = 1L;

                @Override
                protected boolean onIsStarActive(int star) {
                    DownloadEntity download = downloadModel.getObject();
                    return star < ((int) (download.getCalculatedRating() + 0.5));
                }

                @Override
                protected void onRatedAndCaptchaValidated(int rating, AjaxRequestTarget target) {
                    DownloadEntity download = downloadModel.getObject();
                    hasVoted.setObject(Boolean.TRUE);
                    downloadService.rateDownload(rating, download);
                }

                @Override
                public boolean isEnabled() {
                    return isAllowedToVote();
                }
            };
        }

        private TagContentPanel<DownloadTagEntity> createTagPanel() {
            IModel<List<DownloadTagEntity>> tagsModel = new PropertyModel<List<DownloadTagEntity>>(downloadModel, "tags");
            return new TagContentPanel<DownloadTagEntity>("tags", tagsModel, DownloadPage.class);
        }

        private Label createHitsLabel() {
            IModel<String> hitsModel = new PropertyModel<String>(downloadModel, "hits");
            return new Label("hits", hitsModel);
        }

        private ExtendedLabel createDescriptionLabel() {
            IModel<String> descriptionModel = new PropertyModel<String>(downloadModel, "description");
            return new ExtendedLabel("description", descriptionModel);
        }

        private MetaInfoPanel createMetaInfoPanel() {
            return new MetaInfoPanel<DownloadEntity>("metaInfo", downloadModel);
        }

        private boolean isVoteEnabled() {
            return configurationService.findAsBoolean(DownloadConstants.CONF_DOWNLOAD_VOTE_ENABLED);
        }

        private BookmarkablePageLink<DownloadRedirectPage> createTitleLink() {
            DownloadEntity download = downloadModel.getObject();
            BookmarkablePageLink<DownloadRedirectPage> titleLink = newTitleLink();
            titleLink.setParameter("0", download.getId());
            titleLink.add(createTitleLabel());
            return titleLink;
        }

        private BookmarkablePageLink<DownloadRedirectPage> newTitleLink() {
            return new BookmarkablePageLink<DownloadRedirectPage>("titleLink", DownloadRedirectPage.class) {
                private static final long serialVersionUID = -8960982980920074494L;

                @Override
                public boolean isEnabled() {
                    return isAllowedToDownload();
                }
            };
        }

        private Label createTitleLabel() {
            IModel<String> titleModel = new PropertyModel<String>(downloadModel, "title");
            return new Label("titleLabel", titleModel);
        }

        private Component createBrokenLabel() {
            return new Label("broken", DownloadPage.this.getString("broken")){
                private static final long serialVersionUID = -807674850041935129L;

                @Override
                public boolean isVisible() {
                    DownloadEntity download = downloadModel.getObject();
                    return download.getBroken() != null && download.getBroken();
                }
            };
        }

        private boolean isAllowedToDownload() {
            DownloadEntity download = downloadModel.getObject();
            PortalSession session = (PortalSession) getSession();
            return session.hasRight("download.download", download.getDownloadRights());
        }

        private boolean isAllowedToVote() {
            DownloadEntity download = downloadModel.getObject();
            PortalSession session = (PortalSession) getSession();
            return session.hasRight("download.vote", download.getVoteRights());
        }
    }
}
