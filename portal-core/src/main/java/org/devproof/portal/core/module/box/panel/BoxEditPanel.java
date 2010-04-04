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
package org.devproof.portal.core.module.box.panel;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.CheckBox;
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
	private FeedbackPanel feedback;
	private IModel<BoxConfiguration> boxSelectionModel;
	private IModel<BoxEntity> boxModel;

	public BoxEditPanel(String id, IModel<BoxEntity> boxModel) {
		super(id);
		this.boxModel = boxModel;
        this.boxSelectionModel = createBoxSelectionModel();
		add(createFeedbackPanel());
		add(createBoxEditForm());
	}

    private Model<BoxConfiguration> createBoxSelectionModel() {
        String boxType = boxModel.getObject().getBoxType();
        return Model.of(boxRegistry.getBoxConfigurationBySimpleClassName(boxType));
    }

    private Form<BoxEntity> createBoxEditForm() {
        CompoundPropertyModel<BoxEntity> formModel = new CompoundPropertyModel<BoxEntity>(boxModel);
        Form<BoxEntity> form = new Form<BoxEntity>("form", formModel);
		form.add(createContentField());
		form.add(createBoxTypeChoice());
		form.add(createTitleField());
		form.add(createHideTitleCheckBox());
		form.add(createAjaxButton());
		form.add(createCancelButton());
		form.setOutputMarkupId(true);
		return form;
	}

	private CheckBox createHideTitleCheckBox() {
		return new CheckBox("hideTitle");
	}

	private DropDownChoice<BoxConfiguration> createBoxTypeChoice() {
		List<BoxConfiguration> confs = boxRegistry.getRegisteredBoxes();
		ChoiceRenderer<BoxConfiguration> choiceRenderer = new ChoiceRenderer<BoxConfiguration>("name", "boxClass");
		DropDownChoice<BoxConfiguration> boxType = new DropDownChoice<BoxConfiguration>("boxType", boxSelectionModel,
				confs, choiceRenderer);
		boxType.setRequired(true);
		return boxType;
	}

	private TextField<String> createTitleField() {
		TextField<String> title = new TextField<String>("title");
		title.add(StringValidator.maximumLength(100));
		return title;
	}

	private AjaxButton createAjaxButton() {
		return new AjaxButton("saveButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                BoxEntity box = boxModel.getObject();
				if (box.getSort() == null) {
					Integer sort = boxService.getMaxSortNum();
					box.setSort(sort);
				}
				box.setBoxType(boxSelectionModel.getObject().getKey());
				boxService.save(box);
				BoxEditPanel.this.onSave(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedback);
			}
		};
	}

	private AjaxLink<Void> createCancelButton() {
		return new AjaxLink<Void>("cancelButton") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				BoxEditPanel.this.onCancel(target);
			}
		};
	}

	private TextArea<String> createContentField() {
		return new TextArea<String>("content");
	}

	private FeedbackPanel createFeedbackPanel() {
		feedback = new FeedbackPanel("feedbackPanel");
		feedback.setOutputMarkupId(true);
		return feedback;
	}

	protected abstract void onSave(AjaxRequestTarget target);

	protected abstract void onCancel(AjaxRequestTarget target);
}
