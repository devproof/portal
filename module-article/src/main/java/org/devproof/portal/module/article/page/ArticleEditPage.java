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
package org.devproof.portal.module.article.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.PatternValidator;
import org.devproof.portal.core.config.Secured;
import org.devproof.portal.core.module.common.component.richtext.BasicRichTextArea;
import org.devproof.portal.core.module.common.component.richtext.FullRichTextArea;
import org.devproof.portal.core.module.mount.entity.MountPoint;
import org.devproof.portal.core.module.mount.panel.MountInputPanel;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.core.module.right.panel.RightGridPanel;
import org.devproof.portal.core.module.tag.component.TagField;
import org.devproof.portal.module.article.ArticleConstants;
import org.devproof.portal.module.article.entity.Article;
import org.devproof.portal.module.article.entity.ArticleTag;
import org.devproof.portal.module.article.service.ArticleService;
import org.devproof.portal.module.article.service.ArticleTagService;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@Secured(ArticleConstants.AUTHOR_RIGHT)
public class ArticleEditPage extends ArticleBasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "articleService")
    private ArticleService articleService;
    @SpringBean(name = "articleTagService")
    private ArticleTagService articleTagService;

    private IModel<Article> articleModel;
    private MountInputPanel mountInputPanel;

    public ArticleEditPage(IModel<Article> articleModel) {
        super(new PageParameters());
        this.articleModel = articleModel;
        add(createArticleEditForm());
    }

    private Form<Article> createArticleEditForm() {
        Form<Article> form = newArticleEditForm();
        form.add(mountInputPanel());
        form.add(createTitleField());
        form.add(createTeaserField());
        form.add(createContentField());
        form.add(createTagField());
        form.add(createViewRightPanel());
        form.add(createReadRightPanel());
        form.add(createCommentRightPanel());
        form.setOutputMarkupId(true);
        return form;
    }

    private MountInputPanel mountInputPanel() {
        mountInputPanel = new MountInputPanel("mountUrls", "article", createArticleIdModel());
        return mountInputPanel;
    }

    private IModel<String> createArticleIdModel() {
        return new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = 1340993990243817302L;

            @Override
            public String getObject() {
                Integer id = articleModel.getObject().getId();
                if(id != null) {
                    return id.toString();
                }
                return null;
            }
        };
    }

    private RightGridPanel createReadRightPanel() {
        IModel<List<Right>> selectedRights = new PropertyModel<List<Right>>(articleModel, "allRights");
        return new RightGridPanel("readright", "article.read", selectedRights);
    }

    private RightGridPanel createViewRightPanel() {
        IModel<List<Right>> selectedRights = new PropertyModel<List<Right>>(articleModel, "allRights");
        return new RightGridPanel("viewright", "article.view", selectedRights);
    }

    private RightGridPanel createCommentRightPanel() {
        IModel<List<Right>> selectedRights = new PropertyModel<List<Right>>(articleModel, "allRights");
        return new RightGridPanel("commentright", "article.comment", selectedRights);
    }

    private TagField<ArticleTag> createTagField() {
        IModel<List<ArticleTag>> tagsModel = new PropertyModel<List<ArticleTag>>(articleModel, "tags");
        return new TagField<ArticleTag>("tags", tagsModel, articleTagService);
    }

    private RequiredTextField<String> newContentIdField() {
        return new RequiredTextField<String>("contentId") {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isEnabled() {
                return isNewArticle();
            }
        };
    }

    private boolean isNewArticle() {
        return articleModel.getObject().getId() == null;
    }

    private RequiredTextField<String> createTitleField() {
        return new RequiredTextField<String>("title");
    }

    private FormComponent<String> createTeaserField() {
        return new BasicRichTextArea("teaser", false);
    }

    private FormComponent<String> createContentField() {
        return new FullRichTextArea("fullArticle");
    }

    private Form<Article> newArticleEditForm() {
        return new Form<Article>("form", new CompoundPropertyModel<Article>(articleModel)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                Article article = articleModel.getObject();
                articleService.save(article);
                mountInputPanel.storeMountPoints();
                setRedirect(false);
                setResponsePage(ArticleReadPage.class, new PageParameters("0=" + article.getId()));
                info(getString("msg.saved"));
            }
        };
    }
}
