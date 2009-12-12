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
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.devproof.portal.core.module.common.component.richtext.RichTextArea;
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

	private ArticleEntity article;
	private RequiredTextField<String> contentIdField;

	public ArticleEditPage(ArticleEntity article) {
		super(new PageParameters());
		this.article = article;
		add(createArticleEditForm());
	}

	private Form<ArticleEntity> createArticleEditForm() {
		Form<ArticleEntity> form = newArticleEditForm();
		form.add(contentIdField = createContentIdField());
		form.add(createTitleField());
		form.add(createTeaserField());
		form.add(createContentField());
		form.add(createTagField());
		form.add(createViewRightPanel());
		form.add(createReadRightPanel());
		form.setOutputMarkupId(true);
		return form;
	}

	private RightGridPanel createReadRightPanel() {
		return new RightGridPanel("readright", "article.read", new ListModel<RightEntity>(article.getAllRights()));
	}

	private RightGridPanel createViewRightPanel() {
		return new RightGridPanel("viewright", "article.view", new ListModel<RightEntity>(article.getAllRights()));
	}

	private TagField<ArticleTagEntity> createTagField() {
		IModel<List<ArticleTagEntity>> listModel = new PropertyModel<List<ArticleTagEntity>>(article, "tags");
		return new TagField<ArticleTagEntity>("tags", listModel, articleTagService);
	}

	private RequiredTextField<String> createContentIdField() {
		RequiredTextField<String> contentId = new RequiredTextField<String>("contentId");
		contentId.setEnabled(isNewArticle());
		contentId.add(newContentIdValidator());
		contentId.add(new PatternValidator("[A-Za-z0-9\\_\\._\\-]*"));
		return contentId;
	}

	private RequiredTextField<String> createTitleField() {
		RequiredTextField<String> title = new RequiredTextField<String>("title");
		title.add(StringValidator.minimumLength(3));
		if (isNewArticle()) {
			title.add(createContentIdGeneratorBehavior(contentIdField, title));
		}
		return title;
	}

	private FormComponent<String> createTeaserField() {
		FormComponent<String> fc = new RichTextArea("teaser");
		fc.add(StringValidator.minimumLength(3));
		fc.setRequired(true);
		return fc;
	}

	private FormComponent<String> createContentField() {
		FormComponent<String> fc;
		fc = new RichTextArea("fullArticle");
		fc.add(StringValidator.minimumLength(3));
		fc.setRequired(true);
		return fc;
	}

	private boolean isNewArticle() {
		return article.getId() == null;
	}

	private OnChangeAjaxBehavior createContentIdGeneratorBehavior(final RequiredTextField<String> contentId,
			final RequiredTextField<String> title) {
		return new OnChangeAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				String id = title.getModelObject();
				id = id.replace(' ', '_');
				id = id.replaceAll("[^A-Z^a-z^0-9^\\_^\\.^_\\-]*", "");
				contentId.setModelObject(id);
				target.addComponent(contentId);
			}
		};
	}

	private AbstractValidator<String> newContentIdValidator() {
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
		return new Form<ArticleEntity>("form", new CompoundPropertyModel<ArticleEntity>(article)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				articleService.save(article);
				setRedirect(false);
				setResponsePage(ArticleReadPage.class, new PageParameters("0=" + article.getContentId()));
				info(getString("msg.saved"));
			}
		};
	}
}
