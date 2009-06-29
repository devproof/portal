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
package org.devproof.portal.module.blog.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.BookmarkablePagingPanel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.tag.panel.ContentTagPanel;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.blog.BlogConstants;
import org.devproof.portal.module.blog.entity.BlogEntity;
import org.devproof.portal.module.blog.entity.BlogTagEntity;
import org.devproof.portal.module.blog.panel.BlogSearchBoxPanel;
import org.devproof.portal.module.blog.query.BlogQuery;
import org.devproof.portal.module.blog.service.BlogService;

/**
 * @author Carsten Hufe
 */
public class BlogPage extends BlogBasePage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "blogService")
	private BlogService blogService;
	@SpringBean(name = "blogDataProvider")
	private QueryDataProvider<BlogEntity> blogDataProvider;
	@SpringBean(name = "blogTagService")
	private TagService<BlogTagEntity> blogTagService;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;
	private final BlogDataView dataView;

	public BlogPage(final PageParameters params) {
		super(params);
		final PortalSession session = (PortalSession) getSession();
		final BlogQuery query = new BlogQuery();
		if (!session.hasRight("blog.view")) {
			query.setRole(session.getRole());
		}
		this.blogDataProvider.setQueryObject(query);

		this.dataView = new BlogDataView("listBlog", this.blogDataProvider, params);
		addFilterBox(new BlogSearchBoxPanel("box", query, this.blogDataProvider, this, this.dataView, params));

		this.add(this.dataView);
		this.add(new BookmarkablePagingPanel("paging", this.dataView, BlogPage.class, params));
		this.addTagCloudBox(this.blogTagService, new PropertyModel<BlogTagEntity>(query, "tag"), BlogPage.class, params);
	}

	private class BlogDataView extends DataView<BlogEntity> {
		private static final long serialVersionUID = 1L;
		private final PageParameters params;
		private final boolean onlyOne;

		public BlogDataView(final String id, final IDataProvider<BlogEntity> dataProvider, final PageParameters params) {
			super(id, dataProvider);
			this.onlyOne = dataProvider.size() == 1;
			this.params = params;
			setItemsPerPage(BlogPage.this.configurationService.findAsInteger(BlogConstants.CONF_BLOG_ENTRIES_PER_PAGE));
		}

		@Override
		protected void populateItem(final Item<BlogEntity> item) {
			final BlogEntity blog = item.getModelObject();
			item.setOutputMarkupId(true);
			if (this.onlyOne) {
				setPageTitle(blog.getHeadline());
			}

			final BlogView blogView = new BlogView("blogView", blog, this.params);
			if (isAuthor()) {
				blogView.addOrReplace(new AuthorPanel<BlogEntity>("authorButtons", blog) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onDelete(final AjaxRequestTarget target) {
						BlogPage.this.blogService.delete(getEntity());
						item.setVisible(false);
						target.addComponent(item);
						target.addComponent(getFeedback());
						info(this.getString("msg.deleted"));
					}

					@Override
					public void onEdit(final AjaxRequestTarget target) {
						this.setResponsePage(new BlogEditPage(item.getModelObject()));
					}
				});
			}
			item.add(blogView);
		}
	}

	public class BlogView extends Fragment {

		private static final long serialVersionUID = 1L;

		public BlogView(final String id, final BlogEntity blogEntity, final PageParameters params) {
			super(id, "blogView", BlogPage.this);
			this.add(new WebMarkupContainer("authorButtons"));
			final BookmarkablePageLink<BlogPage> headlineLink = new BookmarkablePageLink<BlogPage>("headlineLink", BlogPage.class);
			if (params == null || !params.containsKey("id")) {
				headlineLink.setParameter("id", blogEntity.getId());
			}
			headlineLink.add(new Label("headlineLabel", blogEntity.getHeadline()));
			this.add(headlineLink);
			this.add(new MetaInfoPanel("metaInfo", blogEntity));
			this.add(new ExtendedLabel("content", blogEntity.getContent()));
			this.add(new ContentTagPanel<BlogTagEntity>("tags", new ListModel<BlogTagEntity>(blogEntity.getTags()), BlogPage.class, params));
		}
	}
}
