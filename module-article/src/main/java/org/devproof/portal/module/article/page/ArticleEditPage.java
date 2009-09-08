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
package org.devproof.portal.module.article.page;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
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
import org.devproof.portal.module.article.ArticleConstants;
import org.devproof.portal.module.article.entity.ArticleEntity;
import org.devproof.portal.module.article.entity.ArticlePageEntity;
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

	public ArticleEditPage(final ArticleEntity article) {
		super(new PageParameters());
		add(createArticleEditForm(article));
	}

	private Form<ArticleEntity> createArticleEditForm(final ArticleEntity article) {
		RightGridPanel viewRightPanel = createViewRightPanel(article);
		RightGridPanel readRightPanel = createReadRightPanel(article);
		TagField<ArticleTagEntity> tagField = createTagField(article);
		RequiredTextField<String> contentIdField = createContentIdField(article);
		
		IModel<String> contentToEdit = getFullArticleHtmlFromArticlePages(article.getArticlePages());
		
		Form<ArticleEntity> form = newArticleEditForm(article, viewRightPanel, readRightPanel, tagField, contentToEdit);
		form.setOutputMarkupId(true);
		form.add(tagField);
		form.add(viewRightPanel);
		form.add(readRightPanel);
		form.add(contentIdField);
		form.add(createTitleField(article, contentIdField));
		form.add(createTeaserField());
		form.add(createContentField(contentToEdit));
		return form;
	}

	private RightGridPanel createReadRightPanel(final ArticleEntity article) {
		return new RightGridPanel("readright", "article.read", article.getAllRights());
	}

	private RightGridPanel createViewRightPanel(final ArticleEntity article) {
		return new RightGridPanel("viewright", "article.view", article.getAllRights());
	}

	private TagField<ArticleTagEntity> createTagField(
			final ArticleEntity article) {
		return new TagField<ArticleTagEntity>("tags", article.getTags(), articleTagService);
	}

	private RequiredTextField<String> createContentIdField(
			final ArticleEntity article) {
		final RequiredTextField<String> contentId = new RequiredTextField<String>("contentId");
		contentId.setEnabled(isNewArticle(article));
		contentId.add(createContentIdValidator(article));
		contentId.add(new PatternValidator("[A-Za-z0-9\\_\\._\\-]*"));
		return contentId;
	}

	private RequiredTextField<String> createTitleField(
			final ArticleEntity article,
			final RequiredTextField<String> contentId) {
		RequiredTextField<String> title = new RequiredTextField<String>("title");
		title.add(StringValidator.minimumLength(3));
		if (isNewArticle(article)) {
			title.add(createContentIdGeneratorBehavior(contentId, title));
		}
		return title;
	}

	private FormComponent<String> createTeaserField() {
		FormComponent<String> fc = new RichTextArea("teaser");
		fc.add(StringValidator.minimumLength(3));
		fc.setRequired(true);
		return fc;
	}

	private FormComponent<String> createContentField(
			IModel<String> contentToEdit) {
		FormComponent<String> fc;
		fc = new RichTextArea("content", contentToEdit);
		fc.add(StringValidator.minimumLength(3));
		fc.setRequired(true);
		return fc;
	}

	private boolean isNewArticle(final ArticleEntity article) {
		return article.getId() == null;
	}

	private OnChangeAjaxBehavior createContentIdGeneratorBehavior(
			final RequiredTextField<String> contentId,
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

	private AbstractValidator<String> createContentIdValidator(
			final ArticleEntity article) {
		return new AbstractValidator<String>() {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onValidate(final IValidatable<String> ivalidatable) {
				if (articleService.existsContentId(ivalidatable.getValue()) && isNewArticle(article)) {
					error(ivalidatable);
				}
			}

			@Override
			protected String resourceKey() {
				return "existing.contentId";
			}
		};
	}

	private Form<ArticleEntity> newArticleEditForm(
			final ArticleEntity article, final RightGridPanel viewRight,
			final RightGridPanel readRight,
			final TagField<ArticleTagEntity> tagField,
			final IModel<String> content) {
		return new Form<ArticleEntity>("form", new CompoundPropertyModel<ArticleEntity>(article)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				ArticleEditPage.this.setVisible(false);
				// ArticleEntity article = this.getModelObject();
				final List<RightEntity> allRights = new ArrayList<RightEntity>();
				allRights.addAll(viewRight.getSelectedRights());
				allRights.addAll(readRight.getSelectedRights());
				article.setAllRights(allRights);
				article.setTags(tagField.getTagsAndStore());
				article.setArticlePages(ArticleEditPage.this.getArticlePagesFromFullArticleHtml(content.getObject(), article));
				articleService.save(article);
				setRedirect(false);
				setResponsePage(ArticleReadPage.class, new PageParameters("0=" + article.getContentId()));
				info(getString("msg.saved"));
			}
		};
	}

	private IModel<String> getFullArticleHtmlFromArticlePages(final List<ArticlePageEntity> pages) {
		String back = "";
		if (pages != null) {
			final StringBuilder buf = new StringBuilder();
			boolean firstArticlePage = true;
			for (final ArticlePageEntity page : pages) {
				if (firstArticlePage) {
					firstArticlePage = false;
				} else {
					buf.append(ArticleConstants.PAGEBREAK);
				}
				buf.append(page.getContent());
			}
			back = buf.toString();
		}
		return new Model<String>(back);
	}

	private List<ArticlePageEntity> getArticlePagesFromFullArticleHtml(final String pages, final ArticleEntity parent) {
		List<ArticlePageEntity> back = new ArrayList<ArticlePageEntity>();
		String[] splittedPages = getSplittedPages(pages);
		for (int i = 0; i < splittedPages.length; i++) {
			ArticlePageEntity page = null;
			boolean isUpdatablePageAvailable = parent.getArticlePages() != null 
												&& parent.getArticlePages().size() > i;
			if (isUpdatablePageAvailable) {
				page = parent.getArticlePages().get(i);
			} else {
				page = articleService.newArticlePageEntity(parent, i + 1);
				page.setArticle(parent);
			}
			page.setContent(splittedPages[i]);
			back.add(page);
		}
		return back;
	}

	private String[] getSplittedPages(final String pages) {
		String splittedPages[] = null;
		if (pages != null) {
			splittedPages = StringUtils.splitByWholeSeparator(pages, ArticleConstants.PAGEBREAK);
		} else {
			splittedPages = new String[1];
			splittedPages[0] = "";
		}
		return splittedPages;
	}
}
