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
package org.devproof.portal.module.article.page;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.devproof.portal.core.module.common.component.richtext.BasicRichTextArea;
import org.devproof.portal.core.module.common.component.richtext.FullRichTextArea;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.right.panel.RightGridPanel;
import org.devproof.portal.core.module.tag.component.TagField;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.article.entity.ArticleEntity;
import org.devproof.portal.module.article.entity.ArticleTagEntity;
import org.devproof.portal.module.article.service.ArticleService;

/**
 * @author Carsten Hufe
 */
public class ArticleEditPage extends ArticleBasePage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "articleService")
	private ArticleService articleService;
	@SpringBean(name = "articleTagService")
	private TagService<ArticleTagEntity> articleTagService;

	private IModel<ArticleEntity> articleModel;
	private RequiredTextField<String> contentIdField;

	public ArticleEditPage(IModel<ArticleEntity> articleModel) {
		super(new PageParameters());
		this.articleModel = articleModel;
		add(createArticleEditForm());
	}

	private Form<ArticleEntity> createArticleEditForm() {
		Form<ArticleEntity> form = newArticleEditForm();
		form.add(createContentIdField());
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

	private RightGridPanel createReadRightPanel() {
		IModel<List<RightEntity>> selectedRights = new PropertyModel<List<RightEntity>>(articleModel, "allRights");
		return new RightGridPanel("readright", "article.read", selectedRights);
	}

	private RightGridPanel createViewRightPanel() {
		IModel<List<RightEntity>> selectedRights = new PropertyModel<List<RightEntity>>(articleModel, "allRights");
		return new RightGridPanel("viewright", "article.view", selectedRights);
	}

	private RightGridPanel createCommentRightPanel() {
		IModel<List<RightEntity>> selectedRights = new PropertyModel<List<RightEntity>>(articleModel, "allRights");
		return new RightGridPanel("commentright", "article.comment", selectedRights);
	}

	private TagField<ArticleTagEntity> createTagField() {
		IModel<List<ArticleTagEntity>> tagsModel = new PropertyModel<List<ArticleTagEntity>>(articleModel, "tags");
		return new TagField<ArticleTagEntity>("tags", tagsModel, articleTagService);
	}

	private RequiredTextField<String> createContentIdField() {
		contentIdField = newContentIdField();
		contentIdField.add(contentIdValidator());
		contentIdField.add(contentIdPatternValidator());
		contentIdField.setOutputMarkupId(true);
		return contentIdField;
	}

	private PatternValidator contentIdPatternValidator() {
		return new PatternValidator("[A-Za-z0-9\\_\\._\\-]*");
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
		RequiredTextField<String> title = new RequiredTextField<String>("title");
		title.add(createContentIdGeneratorBehavior(title));
		return title;
	}

	private FormComponent<String> createTeaserField() {
		return new BasicRichTextArea("teaser", false);
	}

	private FormComponent<String> createContentField() {
		return new FullRichTextArea("fullArticle");
	}

	private OnChangeAjaxBehavior createContentIdGeneratorBehavior(final RequiredTextField<String> title) {
		return new OnChangeAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (isNewArticle()) {
					String id = title.getModelObject();
					id = id.replace(' ', '_');
					id = id.replaceAll("[^A-Z^a-z^0-9^\\_^\\.^_\\-]*", "");
					contentIdField.setModelObject(id);
					target.addComponent(contentIdField);
				}
			}
		};
	}

	private AbstractValidator<String> contentIdValidator() {
		return new AbstractValidator<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onValidate(IValidatable<String> ivalidatable) {
				if (articleService.existsContentId(ivalidatable.getValue()) && isNewArticle()) {
					error(ivalidatable);
				}
			}

			@Override
			protected String resourceKey() {
				return "existing.contentId";
			}
		};
	}

	private Form<ArticleEntity> newArticleEditForm() {
		return new Form<ArticleEntity>("form", new CompoundPropertyModel<ArticleEntity>(articleModel)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				ArticleEntity article = articleModel.getObject();
				articleService.save(article);
				setRedirect(false);
				setResponsePage(ArticleReadPage.class, new PageParameters("0=" + article.getContentId()));
				info(getString("msg.saved"));
			}
		};
	}
}
