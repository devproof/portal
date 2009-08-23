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
package org.devproof.portal.module.bookmark.panel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
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

/**
 * @author Carsten Hufe
 */
public class DeliciousSyncPanel extends Panel {

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

	public DeliciousSyncPanel(final String id) {
		super(id);
		this.add(CSSPackageResource.getHeaderContribution(BookmarkConstants.REF_BOOKMARK_CSS));
		final List<RightEntity> rights = new ArrayList<RightEntity>();
		final RightGridPanel viewRights = new RightGridPanel("viewRights", "bookmark.view", rights);
		final RightGridPanel visitRights = new RightGridPanel("visitRights", "bookmark.visit", rights);
		final RightGridPanel voteRights = new RightGridPanel("voteRights", "bookmark.vote", rights);

		final FeedbackPanel feedback = new FeedbackPanel("feedbackPanel");
		feedback.setOutputMarkupId(true);
		add(feedback);
		final DeliciousFormBean formBean = new DeliciousFormBean();
		final Form<DeliciousFormBean> form = new Form<DeliciousFormBean>("form",
				new CompoundPropertyModel<DeliciousFormBean>(formBean));
		form.setOutputMarkupId(true);
		add(form);

		form.add(new RequiredTextField<String>("username"));
		form.add(new PasswordTextField("password"));
		form.add(new TextField<String>("tags"));
		form.add(viewRights);
		form.add(visitRights);
		form.add(voteRights);

		final ProgressionModel model = new ProgressionModel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected Progression getProgression() {
				String descr = "";
				if (deliciousBean != null && deliciousBean.hasError()) {
					descr = DeliciousSyncPanel.this.getString("processError");
				} else if (fetching) {
					descr = new StringResourceModel("processFetching", DeliciousSyncPanel.this, null, new Object[] {
							actualItem, maxItem }).getString();
				} else if (actualItem > 0) {
					descr = new StringResourceModel("processing", DeliciousSyncPanel.this, null, new Object[] {
							actualItem, maxItem }).getString();
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
		final ProgressBar bar = new ProgressBar("bar", model) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onFinished(final AjaxRequestTarget target) {
				if (deliciousBean != null && deliciousBean.hasError()) {
					if (deliciousBean.getHttpCode() == HttpStatus.SC_UNAUTHORIZED) {
						error(this.getString("loginFailed"));
					} else if (deliciousBean.getHttpCode() == HttpStatus.SC_SERVICE_UNAVAILABLE) {
						error(this.getString("serviceNotAvailable"));
					} else {
						error(deliciousBean.getErrorMessage());
					}
				} else {
					info(new StringResourceModel("syncFinished", this, null, new Object[] { newBookmarksCount,
							modifiedBookmarksCount, deletedBookmarksCount }).getString());
				}
				target.addComponent(feedback);
			}
		};
		form.add(bar);

		form.add(new IndicatingAjaxButton("startButton", form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
				// Start the progress bar, will set visibility to true
				bar.start(target);
				final List<RightEntity> allRights = new ArrayList<RightEntity>();
				final UserEntity user = ((PortalSession) Session.get()).getUser();
				allRights.addAll(viewRights.getSelectedRights());
				allRights.addAll(visitRights.getSelectedRights());
				allRights.addAll(voteRights.getSelectedRights());
				new Thread() {
					@Override
					public void run() {
						fetching = true;
						final DeliciousBean bean = synchronizeService.getDataFromDelicious(formBean.username,
								formBean.password, formBean.tags);
						deliciousBean = bean;
						if (bean.hasError()) {
							return;
						}
						final List<BookmarkEntity> newBookmarks = synchronizeService.getNewDeliciousBookmarks(bean);
						newBookmarksCount = newBookmarks.size();
						final List<BookmarkEntity> modifiedBookmarks = synchronizeService
								.getModifiedDeliciousBookmarks(bean);
						modifiedBookmarksCount = modifiedBookmarks.size();
						maxItem = bean.getPosts().size();
						fetching = false;
						final Collection<BookmarkEntity> bookmarksToSave = new ArrayList<BookmarkEntity>(
								newBookmarksCount + modifiedBookmarksCount);
						bookmarksToSave.addAll(newBookmarks);
						bookmarksToSave.addAll(modifiedBookmarks);

						for (final BookmarkEntity bookmark : bookmarksToSave) {
							actualItem++;
							bookmark.setAllRights(allRights);
							if (bookmark.getCreatedAt() == null) {
								bookmark.setCreatedAt(PortalUtil.now());
							}
							if (bookmark.getCreatedBy() == null) {
								bookmark.setCreatedBy(user.getUsername());
							}

							bookmark.setModifiedAt(PortalUtil.now());
							bookmark.setModifiedBy(user.getUsername());
							final List<BookmarkTagEntity> newTags = new ArrayList<BookmarkTagEntity>(bookmark.getTags()
									.size());
							for (final BookmarkTagEntity tag : bookmark.getTags()) {
								final BookmarkTagEntity refreshedTag = tagService.findById(tag.getTagname());
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
						final List<BookmarkEntity> deletedBookmarks = synchronizeService
								.getRemovedDeliciousBookmarks(bean);
						// set to 100% the counter does not work perfect, when a
						// user has manual edited delious bookmarks
						maxItem = deletedBookmarks.size() + bookmarksToSave.size();
						deletedBookmarksCount = deletedBookmarks.size();
						for (final BookmarkEntity bookmark : deletedBookmarks) {
							actualItem++;
							bookmarkService.delete(bookmark);
						}
						tagService.deleteUnusedTags();
					}
				}.start();

				// disable button
				setVisible(false);
			}
		});
	}
}
