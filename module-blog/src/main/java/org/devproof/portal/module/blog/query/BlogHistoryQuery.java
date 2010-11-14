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
package org.devproof.portal.module.blog.query;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.annotation.BeanQuery;
import org.devproof.portal.core.module.common.query.SearchQuery;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.role.entity.Role;
import org.devproof.portal.core.module.tag.TagConstants;
import org.devproof.portal.module.blog.entity.Blog;

import java.io.Serializable;

/**
 * @author Carsten Hufe
 */
public class BlogHistoryQuery implements Serializable {
	private static final long serialVersionUID = 1L;
	private Blog blog;

    @BeanQuery("blog = ?")
    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }
}
