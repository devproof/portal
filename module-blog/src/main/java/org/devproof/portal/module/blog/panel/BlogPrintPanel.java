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
package org.devproof.portal.module.blog.panel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.module.blog.entity.BlogEntity;

/**
 * @author Carsten Hufe
 */
public class BlogPrintPanel extends Panel {

	private static final long serialVersionUID = 1L;
    private IModel<BlogEntity> blogModel;


    public BlogPrintPanel(String id, IModel<BlogEntity> blogModel) {
		super(id);
        this.blogModel = blogModel;
        add(createHeadline());
		add(createMetaInfoPanel());
		add(createContentLabel());
	}

	private Label createHeadline() {
        IModel<String> headlineModel = new PropertyModel<String>(blogModel, "headline");
        return new Label("headline", headlineModel);
	}

	private MetaInfoPanel createMetaInfoPanel() {
		return new MetaInfoPanel("metaInfo", blogModel);
	}

	private ExtendedLabel createContentLabel() {
        IModel<String> contentModel = new PropertyModel<String>(blogModel, "content");
        return new ExtendedLabel("content", contentModel);
	}
}
