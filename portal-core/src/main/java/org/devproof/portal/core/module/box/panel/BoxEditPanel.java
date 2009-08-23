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
abstract public class BoxEditPanel extends Panel {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "boxService")
	private BoxService boxService;
	@SpringBean(name = "boxRegistry")
	private BoxRegistry boxRegistry;

	public BoxEditPanel(final String id, final BoxEntity box) {
		super(id);
		final FeedbackPanel feedback = new FeedbackPanel("feedbackPanel");
		feedback.setOutputMarkupId(true);
		add(feedback);

		final Form<BoxEntity> form = new Form<BoxEntity>("form");
		form.setOutputMarkupId(true);
		add(form);
		form.setModel(new CompoundPropertyModel<BoxEntity>(box));

		final TextArea<String> content = new TextArea<String>("content");
		form.add(content);

		List<BoxConfiguration> confs = boxRegistry.getRegisteredBoxes();
		ChoiceRenderer<BoxConfiguration> choiceRenderer = new ChoiceRenderer<BoxConfiguration>("name", "boxClass");
		final IModel<BoxConfiguration> selectBoxModel = Model.of(boxRegistry.getBoxConfigurationBySimpleClassName(box
				.getBoxType()));
		DropDownChoice<BoxConfiguration> boxType = new DropDownChoice<BoxConfiguration>("boxType", selectBoxModel,
				confs, choiceRenderer);
		boxType.setRequired(true);
		form.add(boxType);

		TextField<String> title = new TextField<String>("title");
		title.add(StringValidator.maximumLength(100));
		form.add(title);

		form.add(new AjaxButton("saveButton", form) {
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
				target.addComponent(feedback);
			}
		});
	}

	public abstract void onSave(AjaxRequestTarget target);
}
