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
		final RightGridPanel viewRight = new RightGridPanel("viewright", "article.view", article.getAllRights());
		final RightGridPanel readRight = new RightGridPanel("readright", "article.read", article.getAllRights());
		final TagField<ArticleTagEntity> tagField = new TagField<ArticleTagEntity>("tags", article.getTags(), this.articleTagService);
		final IModel<String> content = new Model<String>(getStringFromArticlePages(article.getArticlePages()));

		final Form<ArticleEntity> form = new Form<ArticleEntity>("form", new CompoundPropertyModel<ArticleEntity>(article)) {
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
				article.setArticlePages(ArticleEditPage.this.getArticlePagesFromString(content.getObject(), article));
				ArticleEditPage.this.articleService.save(article);
				setRedirect(false);
				this.setResponsePage(ArticleViewPage.class, new PageParameters("0=" + article.getContentId()));
				info(this.getString("msg.saved"));
			}
		};
		form.setOutputMarkupId(true);
		form.add(tagField);
		form.add(viewRight);
		form.add(readRight);
		this.add(form);

		// Other form fields
		FormComponent<String> fc;

		final RequiredTextField<String> contentId = new RequiredTextField<String>("contentId");
		contentId.setOutputMarkupId(true);
		contentId.setEnabled(article.getId() == null);
		contentId.add(new AbstractValidator<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onValidate(final IValidatable<String> ivalidatable) {
				if (ArticleEditPage.this.articleService.existsContentId(ivalidatable.getValue()) && article.getId() == null) {
					this.error(ivalidatable);
				}
			}

			@Override
			protected String resourceKey() {
				return "existing.contentId";
			}
		});
		contentId.add(new PatternValidator("[A-Za-z0-9\\_\\._\\-]*"));
		form.add(contentId);

		// title
		final RequiredTextField<String> title = new RequiredTextField<String>("title");
		title.add(StringValidator.minimumLength(3));
		if (article.getId() == null) {
			title.add(new OnChangeAjaxBehavior() {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onUpdate(final AjaxRequestTarget target) {
					String id = title.getModelObject();
					id = id.replace(' ', '_');
					id = id.replaceAll("[^A-Z^a-z^0-9^\\_^\\.^_\\-]*", "");
					contentId.setModelObject(id);
					target.addComponent(contentId);
				}
			});
		}
		form.add(title);

		// teaser
		fc = new RichTextArea("teaser");
		fc.add(StringValidator.minimumLength(3));
		fc.setRequired(true);
		form.add(fc);

		// teaser
		fc = new RichTextArea("content", content);
		fc.add(StringValidator.minimumLength(3));
		fc.setRequired(true);
		form.add(fc);
	}

	private String getStringFromArticlePages(final List<ArticlePageEntity> pages) {
		String back = "";
		if (pages != null) {
			final StringBuilder buf = new StringBuilder();
			boolean first = true;
			for (final ArticlePageEntity page : pages) {
				if (first) {
					first = false;
				} else {
					buf.append(ArticleConstants.PAGEBREAK);
				}
				buf.append(page.getContent());
			}
			back = buf.toString();
		}
		return back;
	}

	private List<ArticlePageEntity> getArticlePagesFromString(final String pages, final ArticleEntity parent) {
		final List<ArticlePageEntity> back = new ArrayList<ArticlePageEntity>();
		String strs[] = null;
		if (pages != null) {
			strs = StringUtils.splitByWholeSeparator(pages, ArticleConstants.PAGEBREAK);
		} else {
			strs = new String[1];
			strs[0] = "";
		}
		for (int i = 0; i < strs.length; i++) {
			ArticlePageEntity page = null;
			if (parent.getArticlePages() != null && parent.getArticlePages().size() > i) {
				page = parent.getArticlePages().get(i);
			} else {
				page = this.articleService.newArticlePageEntity(parent, i + 1);
				page.setArticle(parent);
			}
			page.setContent(strs[i]);
			back.add(page);
		}

		return back;
	}
}
