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
package org.devproof.portal.core.module.right.panel;

import java.util.Collection;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.right.service.RightService;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.role.service.RoleService;

/**
 * @author Carsten Hufe
 */
public abstract class RightEditPanel extends Panel {
	private static final long serialVersionUID = 1L;
	@SpringBean(name = "roleService")
	private RoleService roleService;
	@SpringBean(name = "rightService")
	private RightService rightService;
	private FeedbackPanel feedback;
	private RightEntity right;
	private boolean isRightEditable;

	public RightEditPanel(String id, RightEntity right, boolean isRightEditable) {
		super(id, Model.of(right));
		this.right = right;
		this.isRightEditable = isRightEditable;
		add(createFeedbackPanel());
		add(createRightEditForm());
	}

	private Form<RightEntity> createRightEditForm() {
		Form<RightEntity> form = new Form<RightEntity>("form", new CompoundPropertyModel<RightEntity>(right));
		form.add(createRightNameField());
		form.add(createDescriptionField());
		form.add(createRolesPalette());
		form.add(createSaveButton());
		form.setOutputMarkupId(true);
		return form;
	}

	private AjaxButton createSaveButton() {
		return new AjaxButton("saveButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				rightService.save((RightEntity) form.getModelObject());
				RightEditPanel.this.onSave(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(feedback);
			}
		};
	}

	private Palette<RoleEntity> createRolesPalette() {
		IChoiceRenderer<RoleEntity> renderer = new ChoiceRenderer<RoleEntity>("description", "id");
		IModel<Collection<RoleEntity>> allRoles = new CollectionModel<RoleEntity>(roleService
				.findAllOrderByDescription());
		IModel<List<RoleEntity>> rightsRoles = new ListModel<RoleEntity>(right.getRoles());
		return newRolesPalette(renderer, allRoles, rightsRoles);
	}

	private Palette<RoleEntity> newRolesPalette(IChoiceRenderer<RoleEntity> renderer,
			IModel<Collection<RoleEntity>> allRoles, IModel<List<RoleEntity>> rightsRoles) {
		Palette<RoleEntity> palette = new Palette<RoleEntity>("roles", rightsRoles, allRoles, renderer, 10, false) {
			private static final long serialVersionUID = 1L;

			@Override
			protected ResourceReference getCSS() {
				return null;
			}

			@Override
			protected Component newAvailableHeader(String componentId) {
				return new Label(componentId, getString("palette.available"));
			}

			@Override
			protected Component newSelectedHeader(String componentId) {
				return new Label(componentId, getString("palette.selected"));
			}
		};
		return palette;
	}

	private FormComponent<String> createDescriptionField() {
		FormComponent<String> fc;
		fc = new TextArea<String>("description");
		fc.setRequired(true);
		fc.add(StringValidator.minimumLength(10));
		return fc;
	}

	private FormComponent<String> createRightNameField() {
		FormComponent<String> fc;
		fc = new RequiredTextField<String>("right");
		fc.add(StringValidator.minimumLength(5));
		fc.add(new PatternValidator("[A-Za-z0-9\\.]*"));
		fc.setEnabled(isRightEditable);
		return fc;
	}

	private FeedbackPanel createFeedbackPanel() {
		feedback = new FeedbackPanel("feedbackPanel");
		feedback.setOutputMarkupId(true);
		return feedback;
	}

	public abstract void onSave(AjaxRequestTarget target);
}
