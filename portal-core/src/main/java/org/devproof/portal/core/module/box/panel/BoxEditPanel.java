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
package org.devproof.portal.core.module.box.panel;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.devproof.portal.core.config.BoxConfiguration;
import org.devproof.portal.core.module.box.entity.BoxEntity;
import org.devproof.portal.core.module.box.registry.BoxRegistry;
import org.devproof.portal.core.module.box.service.BoxService;

/**
 * @author Carsten Hufe
 */
public abstract class BoxEditPanel extends Panel {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "boxService")
	private BoxService boxService;
	@SpringBean(name = "boxRegistry")
	private BoxRegistry boxRegistry;

	public BoxEditPanel(final String id, final BoxEntity box) {
		super(id);
		FeedbackPanel feedbackPanel = createFeedbackPanel();
		IModel<BoxConfiguration> selectBoxModel = getBoxConfigurationModel(box);
		add(feedbackPanel);
		add(createPageForm(box, feedbackPanel, selectBoxModel));
	}

	private Form<BoxEntity> createPageForm(final BoxEntity box,
			FeedbackPanel feedbackPanel, IModel<BoxConfiguration> selectBoxModel) {
		Form<BoxEntity> form = createBoxEditForm(box);
		form.add(createContentField());
		form.add(createBoxTypeChoice(selectBoxModel));
		form.add(createTitleField());
		form.add(createAjaxButton(box, feedbackPanel, form, selectBoxModel));
		form.setOutputMarkupId(true);
		return form;
	}

	private IModel<BoxConfiguration> getBoxConfigurationModel(
			final BoxEntity box) {
		IModel<BoxConfiguration> selectBoxModel = Model.of(boxRegistry.getBoxConfigurationBySimpleClassName(box
				.getBoxType()));
		return selectBoxModel;
	}

	private DropDownChoice<BoxConfiguration> createBoxTypeChoice(
			final IModel<BoxConfiguration> selectBoxModel) {
		List<BoxConfiguration> confs = boxRegistry.getRegisteredBoxes();
		ChoiceRenderer<BoxConfiguration> choiceRenderer = new ChoiceRenderer<BoxConfiguration>("name", "boxClass");
		DropDownChoice<BoxConfiguration> boxType = new DropDownChoice<BoxConfiguration>("boxType", selectBoxModel,
				confs, choiceRenderer);
		boxType.setRequired(true);
		return boxType;
	}

	private TextField<String> createTitleField() {
		TextField<String> title = new TextField<String>("title");
		title.add(StringValidator.maximumLength(100));
		return title;
	}

	private AjaxButton createAjaxButton(final BoxEntity box,
			final FeedbackPanel feedbackPanel, Form<BoxEntity> form,
			final IModel<BoxConfiguration> selectBoxModel) {
		return new AjaxButton("saveButton", form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
				if (box.getSort() == null) {
					Integer sort = boxService.getMaxSortNum();
					box.setSort(sort);
				}
				box.setBoxType(selectBoxModel.getObject().getKey());
				boxService.save(box);
				BoxEditPanel.this.onSave(target);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, final Form<?> form) {
				target.addComponent(feedbackPanel);
			}
		};
	}

	private TextArea<String> createContentField() {
		TextArea<String> content = new TextArea<String>("content");
		return content;
	}


	private Form<BoxEntity> createBoxEditForm(BoxEntity box) {
		Form<BoxEntity> form = new Form<BoxEntity>("form", new CompoundPropertyModel<BoxEntity>(box));
		return form;
	}

	private FeedbackPanel createFeedbackPanel() {
		FeedbackPanel feedback = new FeedbackPanel("feedbackPanel");
		feedback.setOutputMarkupId(true);
		return feedback;
	}

	public abstract void onSave(AjaxRequestTarget target);
}
