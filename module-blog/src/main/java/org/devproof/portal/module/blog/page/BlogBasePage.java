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
package org.devproof.portal.module.blog.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.module.blog.BlogConstants;
import org.devproof.portal.module.blog.entity.BlogEntity;
import org.devproof.portal.module.blog.service.BlogService;

/**
 * @author Carsten Hufe
 */
public abstract class BlogBasePage extends TemplatePage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "blogService")
	private BlogService blogService;
	private boolean isAuthor = false;

	public BlogBasePage(PageParameters params) {
		super(params);
		setAuthorRight();
		add(createCSSHeaderContributor());
		addSyntaxHighlighter();
		addBlogAddLink();
	}

	private HeaderContributor createCSSHeaderContributor() {
		return CSSPackageResource.getHeaderContribution(BlogConstants.REF_BLOG_CSS);
	}

	private void addBlogAddLink() {
		// New Blog Link
		if (isAuthor) {
			Link<?> addLink = createBlogAddLink();
			addLink.add(new Label("linkName", getString("createLink")));
			addPageAdminBoxLink(addLink);
		}
	}

	private Link<?> createBlogAddLink() {
		return new Link<Object>("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				BlogEntity newEntry = blogService.newBlogEntity();
				setResponsePage(new BlogEditPage(newEntry));
			}
		};
	}

	private void setAuthorRight() {
		PortalSession session = (PortalSession) getSession();
		isAuthor = session.hasRight("page.BlogEditPage");
	}

	public boolean isAuthor() {
		return isAuthor;
	}
}
