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

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
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
	private final BlogQuery query;
	private final PageParameters params;
	
	public BlogPage(final PageParameters params) {
		super(params);
		this.params = params;
		query = createBlogQuery();
		add(dataView = createBlogDataView());
		addFilterBox(createBlogSearchBoxPanel());
		add(createPagingPanel());
		addTagCloudBox();
	}

	private BlogSearchBoxPanel createBlogSearchBoxPanel() {
		return new BlogSearchBoxPanel("box", query, blogDataProvider, this, dataView, params);
	}

	private BookmarkablePagingPanel createPagingPanel() {
		return new BookmarkablePagingPanel("paging", dataView, BlogPage.class, params);
	}

	private void addTagCloudBox() {
		addTagCloudBox(blogTagService, new PropertyModel<BlogTagEntity>(query, "tag"), BlogPage.class, params);
	}

	private BlogDataView createBlogDataView() {
		return new BlogDataView("listBlog");
	}

	private BlogQuery createBlogQuery() {
		PortalSession session = (PortalSession) getSession();
		BlogQuery query = new BlogQuery();
		if (!session.hasRight("blog.view")) {
			query.setRole(session.getRole());
		}
		blogDataProvider.setQueryObject(query);
		return query;
	}

	private class BlogDataView extends DataView<BlogEntity> {
		private static final long serialVersionUID = 1L;
		private final boolean onlyOneBlogEntryInResult;

		public BlogDataView(final String id) {
			super(id, blogDataProvider);
			onlyOneBlogEntryInResult = blogDataProvider.size() == 1;
			setItemsPerPage(configurationService.findAsInteger(BlogConstants.CONF_BLOG_ENTRIES_PER_PAGE));
		}

		@Override
		protected void populateItem(final Item<BlogEntity> item) {
			setBlogTitleAsPageTitle(item);
			item.setOutputMarkupId(true);
			item.add(createBlogView(item));
		}

		private void setBlogTitleAsPageTitle(final Item<BlogEntity> item) {
			if (onlyOneBlogEntryInResult) {
				BlogEntity blog = item.getModelObject();
				setPageTitle(blog.getHeadline());
			}
		}

		private BlogView createBlogView(final Item<BlogEntity> item) {
			return new BlogView("blogView", item);
		}
	}

	private class BlogView extends Fragment {

		private static final long serialVersionUID = 1L;

		private BlogEntity blog;
		
		public BlogView(final String id, final Item<BlogEntity> item) {
			super(id, "blogView", BlogPage.this);
			this.blog = item.getModelObject();
			add(createAppropriateAuthorPanel(item));
			add(createHeadline());
			add(createTagPanel());
			add(createMetaInfoPanel());
			add(createContentLabel());
		}
		
		private Component createAppropriateAuthorPanel(final Item<BlogEntity> item) {
			if (isAuthor()) {
				return createAuthorPanel(item);
			} else {
				return createEmptyAuthorPanel();
			}
		}
		
		private AuthorPanel<BlogEntity> createAuthorPanel(final Item<BlogEntity> item) {
			return new AuthorPanel<BlogEntity>("authorButtons", blog) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onDelete(final AjaxRequestTarget target) {
					blogService.delete(getEntity());
					item.setVisible(false);
					target.addComponent(item);
					target.addComponent(getFeedback());
					info(getString("msg.deleted"));
				}

				@Override
				public void onEdit(final AjaxRequestTarget target) {
					setResponsePage(new BlogEditPage(item.getModelObject()));
				}
			};
		}

		private WebMarkupContainer createEmptyAuthorPanel() {
			return new WebMarkupContainer("authorButtons");
		}
		
		private BookmarkablePageLink<BlogPage> createHeadline() {
			final BookmarkablePageLink<BlogPage> headlineLink = new BookmarkablePageLink<BlogPage>("headlineLink",
					BlogPage.class);
			if (params == null || !params.containsKey("id")) {
				headlineLink.setParameter("id", blog.getId());
			}
			headlineLink.add(new Label("headlineLabel", blog.getHeadline()));
			return headlineLink;
		}

		private MetaInfoPanel createMetaInfoPanel() {
			return new MetaInfoPanel("metaInfo", blog);
		}

		private ExtendedLabel createContentLabel() {
			return new ExtendedLabel("content", blog.getContent());
		}

		private ContentTagPanel<BlogTagEntity> createTagPanel() {
			return new ContentTagPanel<BlogTagEntity>("tags", new ListModel<BlogTagEntity>(blog.getTags()),
					BlogPage.class, params);
		}
	}
}
