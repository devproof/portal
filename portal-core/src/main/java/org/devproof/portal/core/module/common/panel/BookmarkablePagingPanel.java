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
package org.devproof.portal.core.module.common.panel;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.devproof.portal.core.module.common.query.SearchParameterResolver;

/**
 * Should be refactored ... very ugly. Search engines must be able to index nice
 * urls!
 * 
 * @author Carsten Hufe
 */
public class BookmarkablePagingPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private static final String PAGE_PARAM = "page";

	private IPageable pageable;
	private IModel<? extends SearchParameterResolver> pagingParameterResolverModel;
	private Class<? extends Page> parentClazz;

	public BookmarkablePagingPanel(String id, IPageable pageable,
			IModel<? extends SearchParameterResolver> pagingParameterResolverModel, Class<? extends Page> parentClazz) {
		super(id);
		this.pageable = pageable;
		this.pagingParameterResolverModel = pagingParameterResolverModel;
		this.parentClazz = parentClazz;
		// handleCurrentPageParameter();
		add(createBackLink());
		add(createForwardLink());
	}

	//
	// private void handleCurrentPageParameter() {
	// // TODO move to Dataview???
	// PageParameters params = RequestCycle.get().getPageParameters();
	// if (params != null && params.containsKey(PAGE_PARAM)) {
	// int page = params.getAsInteger(PAGE_PARAM, 1);
	// if (page > 0 && page <= pageable.getPageCount()) {
	// pageable.setCurrentPage(page - 1);
	// }
	// }
	// }

	@Override
	protected void onBeforeRender() {
		// if params is null, its a post search request ... so reset the current
		// page
		PageParameters params = RequestCycle.get().getPageParameters();
		if (params != null && params.containsKey(PAGE_PARAM)) {
			int page = params.getAsInteger(PAGE_PARAM, 1);
			if (page > 0 && page <= pageable.getPageCount()) {
				pageable.setCurrentPage(page - 1);
			}
		} else {
			pageable.setCurrentPage(0);
		}
		super.onBeforeRender();
	}

	private BookmarkablePageLink<String> createForwardLink() {
		return new BookmarkablePageLink<String>("forwardLink", parentClazz) {
			private static final long serialVersionUID = 1L;

			@Override
			public PageParameters getPageParameters() {
				SearchParameterResolver resolver = pagingParameterResolverModel.getObject();
				PageParameters pageParameters = resolver.getPageParameters();
				pageParameters.put(PAGE_PARAM, pageable.getCurrentPage() + 2);
				return pageParameters;
			}

			@Override
			public boolean isVisible() {
				return (pageable.getPageCount() - 1) > pageable.getCurrentPage();
			}
		};
	}

	private BookmarkablePageLink<String> createBackLink() {
		return new BookmarkablePageLink<String>("backLink", parentClazz) {
			private static final long serialVersionUID = 1L;

			@Override
			public PageParameters getPageParameters() {
				SearchParameterResolver resolver = pagingParameterResolverModel.getObject();
				PageParameters pageParameters = resolver.getPageParameters();
				pageParameters.put(PAGE_PARAM, pageable.getCurrentPage());
				return pageParameters;
			}

			@Override
			public boolean isVisible() {
				return pageable.getCurrentPage() != 0;
			}
		};
	}
}
