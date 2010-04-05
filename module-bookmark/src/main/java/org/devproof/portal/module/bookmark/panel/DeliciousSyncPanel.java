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
package org.devproof.portal.module.bookmark.panel;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.component.ProgressBar;
import org.devproof.portal.core.module.common.component.Progression;
import org.devproof.portal.core.module.common.component.ProgressionModel;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.right.panel.RightGridPanel;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.module.bookmark.BookmarkConstants;
import org.devproof.portal.module.bookmark.bean.DeliciousBean;
import org.devproof.portal.module.bookmark.bean.DeliciousFormBean;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity;
import org.devproof.portal.module.bookmark.entity.BookmarkTagEntity;
import org.devproof.portal.module.bookmark.service.BookmarkService;
import org.devproof.portal.module.bookmark.service.SynchronizeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Carsten Hufe
 */
public abstract class DeliciousSyncPanel extends Panel {

    private static final long serialVersionUID = 1L;

    @SpringBean(name = "synchronizeService")
    private SynchronizeService synchronizeService;
    @SpringBean(name = "bookmarkTagService")
    private TagService<BookmarkTagEntity> tagService;
    @SpringBean(name = "bookmarkService")
    private BookmarkService bookmarkService;

    private int actualItem = 0;
    private int maxItem = 0;
    private boolean fetching = false;
    private int newBookmarksCount = 0;
    private int modifiedBookmarksCount = 0;
    private int deletedBookmarksCount = 0;
    private DeliciousBean deliciousBean;
    private IModel<List<RightEntity>> allSelectedRightsModel;
    private ProgressBar progressBar;
    private DeliciousFormBean deliciousFormBean = new DeliciousFormBean();
    private FeedbackPanel feedbackPanel;
    private volatile boolean threadActive = false;

    public DeliciousSyncPanel(String id) {
        super(id);
        allSelectedRightsModel = createAllSelectedRightsModel();
        add(createCSSHeaderContributor());
        add(createFeedbackPanel());
        add(createDeliciousSyncForm());
    }

    private IModel<List<RightEntity>> createAllSelectedRightsModel() {
        return new LoadableDetachableModel<List<RightEntity>>() {
            private static final long serialVersionUID = -3952424378430843342L;

            @Override
            protected List<RightEntity> load() {
                return bookmarkService.findLastSelectedRights();
            }
        };
    }

    private HeaderContributor createCSSHeaderContributor() {
        return CSSPackageResource.getHeaderContribution(BookmarkConstants.REF_BOOKMARK_CSS);
    }

    private Form<DeliciousFormBean> createDeliciousSyncForm() {
        Form<DeliciousFormBean> form = new Form<DeliciousFormBean>("form", new CompoundPropertyModel<DeliciousFormBean>(deliciousFormBean));
        form.add(createUsernameField());
        form.add(createPasswordField());
        form.add(createTagField());
        form.add(createViewRightPanel());
        form.add(createVisitRightPanel());
        form.add(createVoteRightPanel());
        form.add(createProgressBar());
        form.add(createStartButton());
        form.add(createCancelButton());
        form.setOutputMarkupId(true);
        return form;
    }

    private RightGridPanel createVoteRightPanel() {
        return new RightGridPanel("voteRights", "bookmark.vote", allSelectedRightsModel);
    }

    private RightGridPanel createVisitRightPanel() {
        return new RightGridPanel("visitRights", "bookmark.visit", allSelectedRightsModel);
    }

    private RightGridPanel createViewRightPanel() {
        return new RightGridPanel("viewRights", "bookmark.view", allSelectedRightsModel);
    }

    private TextField<String> createTagField() {
        return new TextField<String>("tags");
    }

    private PasswordTextField createPasswordField() {
        return new PasswordTextField("password");
    }

    private RequiredTextField<String> createUsernameField() {
        return new RequiredTextField<String>("username");
    }

    private AjaxButton createStartButton() {
        return new AjaxButton("startButton") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                // Start the progress bar, will set visibility to true
                progressBar.start(target);
                final UserEntity user = ((PortalSession) Session.get()).getUser();
                new Thread() {
                    @Override
                    public void run() {
                        threadActive = true;
                        fetching = true;
                        DeliciousBean bean = synchronizeService.getDataFromDelicious(deliciousFormBean.username, deliciousFormBean.password, deliciousFormBean.tags);
                        deliciousBean = bean;
                        if (bean.hasError() && !threadActive) {
                            return;
                        }
                        Collection<BookmarkEntity> bookmarksToSave = retrieveBookmarks(bean);
                        saveBookmarks(user, bookmarksToSave);
                        deleteBookmarks(bean, bookmarksToSave);
                    }

                    private Collection<BookmarkEntity> retrieveBookmarks(DeliciousBean bean) {
                        List<BookmarkEntity> newBookmarks = synchronizeService.getNewDeliciousBookmarks(bean);
                        newBookmarksCount = newBookmarks.size();
                        List<BookmarkEntity> modifiedBookmarks = synchronizeService.getModifiedDeliciousBookmarks(bean);
                        modifiedBookmarksCount = modifiedBookmarks.size();
                        maxItem = bean.getPosts().size();
                        fetching = false;
                        Collection<BookmarkEntity> bookmarksToSave = new ArrayList<BookmarkEntity>(newBookmarksCount + modifiedBookmarksCount);
                        bookmarksToSave.addAll(newBookmarks);
                        bookmarksToSave.addAll(modifiedBookmarks);
                        return bookmarksToSave;
                    }

                    private void saveBookmarks(UserEntity user, Collection<BookmarkEntity> bookmarksToSave) {
                        for (BookmarkEntity bookmark : bookmarksToSave) {
                            actualItem++;
                            bookmark.setAllRights(allSelectedRightsModel.getObject());
                            if (bookmark.getCreatedAt() == null) {
                                bookmark.setCreatedAt(PortalUtil.now());
                            }
                            if (bookmark.getCreatedBy() == null) {
                                bookmark.setCreatedBy(user.getUsername());
                            }

                            bookmark.setModifiedAt(PortalUtil.now());
                            bookmark.setModifiedBy(user.getUsername());
                            bookmark.setUpdateModificationData(false);
                            List<BookmarkTagEntity> newTags = new ArrayList<BookmarkTagEntity>(bookmark.getTags().size());
                            for (BookmarkTagEntity tag : bookmark.getTags()) {
                                BookmarkTagEntity refreshedTag = tagService.findById(tag.getTagname());
                                if (refreshedTag == null) {
                                    newTags.add(tag);
                                    tagService.save(tag);
                                } else {
                                    newTags.add(refreshedTag);
                                }
                            }
                            bookmark.setTags(newTags);
                            bookmarkService.save(bookmark);
                        }
                    }

                    private void deleteBookmarks(DeliciousBean bean, Collection<BookmarkEntity> bookmarksToSave) {
                        List<BookmarkEntity> deletedBookmarks = synchronizeService.getRemovedDeliciousBookmarks(bean);
                        // set to 100% the counter does not work perfect, when a
                        // user has manual edited delious bookmarks
                        maxItem = deletedBookmarks.size() + bookmarksToSave.size();
                        deletedBookmarksCount = deletedBookmarks.size();
                        for (BookmarkEntity bookmark : deletedBookmarks) {
                            actualItem++;
                            bookmarkService.delete(bookmark);
                        }
                        tagService.deleteUnusedTags();
                    }
                }.start();

                // disable button
                setEnabled(false);
            }
        };
    }

    private AjaxLink<Void> createCancelButton() {
        return new AjaxLink<Void>("cancelButton") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                threadActive = false;
                onCancel(target);
            }
        };
    }

    private ProgressBar createProgressBar() {
        ProgressionModel progressionModel = createProgressionModel();
        progressBar = new ProgressBar("bar", progressionModel) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onFinished(AjaxRequestTarget target) {
                if (deliciousBean != null && deliciousBean.hasError()) {
                    if (deliciousBean.getHttpCode() == HttpStatus.SC_UNAUTHORIZED) {
                        error(getString("loginFailed"));
                    } else if (deliciousBean.getHttpCode() == HttpStatus.SC_SERVICE_UNAVAILABLE) {
                        error(getString("serviceNotAvailable"));
                    } else {
                        error(deliciousBean.getErrorMessage());
                    }
                } else {
                    info(new StringResourceModel("syncFinished", this, null, new Object[]{newBookmarksCount, modifiedBookmarksCount, deletedBookmarksCount}).getString());
                }
                target.addComponent(feedbackPanel);
            }
        };
        return progressBar;
    }

    private ProgressionModel createProgressionModel() {
        return new ProgressionModel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected Progression getProgression() {
                String descr = "";
                if (deliciousBean != null && deliciousBean.hasError()) {
                    descr = DeliciousSyncPanel.this.getString("processError");
                } else if (fetching) {
                    descr = new StringResourceModel("processFetching", DeliciousSyncPanel.this, null, new Object[]{actualItem, maxItem}).getString();
                } else if (actualItem > 0) {
                    descr = new StringResourceModel("processing", DeliciousSyncPanel.this, null, new Object[]{actualItem, maxItem}).getString();
                }
                int progressInPercent = actualItem == 0 && !fetching ? 0 : 20;
                if (actualItem > 0) {
                    progressInPercent += (int) (((double) actualItem / (double) maxItem) * 80d);
                } else if (deliciousBean != null && deliciousBean.hasError()) {
                    progressInPercent = 100;
                }
                return new Progression(progressInPercent, descr);
            }
        };
    }

    private FeedbackPanel createFeedbackPanel() {
        feedbackPanel = new FeedbackPanel("feedbackPanel");
        feedbackPanel.setOutputMarkupId(true);
        return feedbackPanel;
    }

    /**
     * called when the cancel button is clicked
     *
     * @param target
     */
    public abstract void onCancel(AjaxRequestTarget target);
}
