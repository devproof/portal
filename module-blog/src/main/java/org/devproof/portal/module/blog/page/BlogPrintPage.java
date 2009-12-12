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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.print.page.PrintPage;
import org.devproof.portal.module.blog.entity.BlogEntity;
import org.devproof.portal.module.blog.panel.BlogPrintPanel;
import org.devproof.portal.module.blog.service.BlogService;

/**
 * @author Carsten Hufe
 */
public class BlogPrintPage extends PrintPage {
	@SpringBean(name = "blogService")
	private BlogService blogService;

	public BlogPrintPage(PageParameters params) {
		super(params);
	}

	@Override
	protected Component createPrintableComponent(String id, PageParameters params) {
		Integer blogId = params.getAsInteger("0");
		if (blogId == null) {
			return getMissingParameterLabel(id);
		}
		BlogEntity blog = blogService.findById(blogId);
		if (blog == null) {
			// dont tell the user that it does not exist
			return getMissingRightsLabel(id);
		}
		if (isAllowedToRead(blog)) {
			return new BlogPrintPanel(id, blog);
		} else {
			return getMissingParameterLabel(id);
		}
	}

	private boolean isAllowedToRead(BlogEntity blog) {
		PortalSession session = (PortalSession) getSession();
		return session.hasRight(blog.getViewRights()) || session.hasRight("page.BlogEditPage");
	}
}
