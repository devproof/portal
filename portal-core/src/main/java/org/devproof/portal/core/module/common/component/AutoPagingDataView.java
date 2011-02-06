/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.common.component;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.devproof.portal.core.module.common.panel.BookmarkablePagingPanel;

/**
 * Sets the current page with the PageParameter "page"
 *
 * @author Carsten Hufe
 */
public abstract class AutoPagingDataView<T> extends DataView<T> {

    protected AutoPagingDataView(String id, IDataProvider<T> dataProvider) {
        super(id, dataProvider);
    }

    private static final long serialVersionUID = 1L;

    @Override
    protected void onBeforeRender() {
        // if params is null, its a post search request ... so reset the
        // current page
        PageParameters params = RequestCycle.get().getPageParameters();
        if (params != null && params.containsKey(BookmarkablePagingPanel.PAGE_PARAM)) {
            int page = params.getAsInteger(BookmarkablePagingPanel.PAGE_PARAM, 1);
            if (page > 0 && page <= getPageCount()) {
                setCurrentPage(page - 1);
            }
        } else {
            setCurrentPage(0);
        }
        super.onBeforeRender();
    }

}
