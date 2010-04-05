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
package org.devproof.portal.module.article.panel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.module.article.ArticleConstants;
import org.devproof.portal.module.article.entity.ArticleEntity;

/**
 * @author Carsten Hufe
 */
public class ArticlePrintPanel extends Panel {

    private static final long serialVersionUID = 1L;
    private IModel<ArticleEntity> articleModel;

    public ArticlePrintPanel(String id, IModel<ArticleEntity> articleModel) {
        super(id, articleModel);
        this.articleModel = articleModel;
        add(createTitle());
        add(createMetaInfoPanel());
        add(createContentLabel());
    }

    private Label createTitle() {
        PropertyModel<String> titleModel = new PropertyModel<String>(articleModel, "title");
        return new Label("title", titleModel);
    }

    private MetaInfoPanel<ArticleEntity> createMetaInfoPanel() {
        return new MetaInfoPanel<ArticleEntity>("metaInfo", articleModel);
    }

    private ExtendedLabel createContentLabel() {
        IModel<String> contentModel = createContentModel();
        return new ExtendedLabel("content", contentModel);
    }

    private IModel<String> createContentModel() {
        return new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = -3133650134418166946L;
            @Override
            public String getObject() {
                ArticleEntity article = articleModel.getObject();
                String content = article.getFullArticle();
                content = content.replace(ArticleConstants.PAGEBREAK, "<br/><br/>");
                return content;
            }
        };
    }
}
