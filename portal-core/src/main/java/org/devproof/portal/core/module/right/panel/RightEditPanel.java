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

	public RightEditPanel(final String id, final RightEntity right, final boolean isRightEditable) {
		super(id, Model.of(right));
		final FeedbackPanel feedback = new FeedbackPanel("feedbackPanel");
		feedback.setOutputMarkupId(true);
		add(feedback);

		Form<RightEntity> form = new Form<RightEntity>("form", new CompoundPropertyModel<RightEntity>(right));
		form.setOutputMarkupId(true);
		add(form);

		FormComponent<String> fc;

		fc = new RequiredTextField<String>("right");
		fc.add(StringValidator.minimumLength(5));
		fc.add(new PatternValidator("[A-Za-z0-9\\.]*"));
		fc.setEnabled(isRightEditable);
		form.add(fc);

		fc = new TextArea<String>("description");
		fc.setRequired(true);
		fc.add(StringValidator.minimumLength(10));
		form.add(fc);

		// roles
		IChoiceRenderer<RoleEntity> renderer = new ChoiceRenderer<RoleEntity>("description", "id");
		IModel<Collection<RoleEntity>> allRoles = new CollectionModel<RoleEntity>(roleService
				.findAllOrderByDescription());
		IModel<List<RoleEntity>> rightsRoles = new ListModel<RoleEntity>(right.getRoles());

		final Palette<RoleEntity> palette = new Palette<RoleEntity>("roles", rightsRoles, allRoles, renderer, 10, false) {
			private static final long serialVersionUID = 1L;

			@Override
			protected ResourceReference getCSS() {
				return null;
			}

			@Override
			protected Component newAvailableHeader(final String componentId) {
				return new Label(componentId, getString("palette.available"));
			}

			@Override
			protected Component newSelectedHeader(final String componentId) {
				return new Label(componentId, getString("palette.selected"));
			}
		};

		form.add(palette);

		form.add(new AjaxButton("saveButton", form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
				rightService.save((RightEntity) form.getModelObject());
				RightEditPanel.this.onSave(target);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, final Form<?> form) {
				target.addComponent(feedback);
			}
		});
	}

	public abstract void onSave(AjaxRequestTarget target);
}
