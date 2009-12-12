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
package org.devproof.portal.module.article.panel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.module.article.ArticleConstants;
import org.devproof.portal.module.article.entity.ArticleEntity;

/**
 * @author Carsten Hufe
 */
public class ArticlePrintPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private ArticleEntity article;

	public ArticlePrintPanel(String id, ArticleEntity article) {
		super(id);
		this.article = article;
		add(createTitle());
		add(createMetaInfoPanel());
		add(createContentLabel());
	}

	private Label createTitle() {
		return new Label("title", article.getTitle());
	}

	private MetaInfoPanel createMetaInfoPanel() {
		return new MetaInfoPanel("metaInfo", article);
	}

	private ExtendedLabel createContentLabel() {
		String content = article.getFullArticle();
		content = content.replace(ArticleConstants.PAGEBREAK, "<br/><br/>");
		return new ExtendedLabel("content", content);
	}
}
