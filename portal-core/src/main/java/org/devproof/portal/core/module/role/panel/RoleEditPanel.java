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
package org.devproof.portal.core.module.role.panel;

import java.util.Collection;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.right.service.RightService;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.role.service.RoleService;

/**
 * @author Carsten Hufe
 */
public abstract class RoleEditPanel extends Panel {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "roleService")
	private RoleService roleService;
	@SpringBean(name = "rightService")
	private RightService rightService;

	public RoleEditPanel(final String id, final RoleEntity role, final boolean isRightEditable) {
		super(id, Model.of(role));
		final FeedbackPanel feedback = new FeedbackPanel("feedbackPanel");
		feedback.setOutputMarkupId(true);
		add(feedback);

		Form<RoleEntity> form = new Form<RoleEntity>("form", new CompoundPropertyModel<RoleEntity>(role));
		form.setOutputMarkupId(true);
		add(form);

		FormComponent<String> fc;

		fc = new RequiredTextField<String>("description");
		fc.add(StringValidator.minimumLength(5));
		fc.setEnabled(isRightEditable);
		form.add(fc);

		form.add(new CheckBox("active"));

		IChoiceRenderer<RightEntity> renderer = new ChoiceRenderer<RightEntity>("description", "right");
		IModel<Collection<RightEntity>> allRights = new CollectionModel<RightEntity>(rightService
				.findAllOrderByDescription());
		IModel<List<RightEntity>> roleRights = new ListModel<RightEntity>(role.getRights());

		final Palette<RightEntity> palette = new Palette<RightEntity>("rights", roleRights, allRights, renderer, 10,
				false) {
			// final Palette palette = new Palette("rights", new
			// Model<Serializable>((Serializable)role.getRights()), new
			// Model<Serializable>((Serializable)rights), renderer, 10, false) {
			private static final long serialVersionUID = 1L;

			@Override
			protected ResourceReference getCSS() {
				return null;
			}

			@Override
			protected Component newAvailableHeader(final String componentId) {
				return new Label(componentId, this.getString("palette.available"));
			}

			@Override
			protected Component newSelectedHeader(final String componentId) {
				return new Label(componentId, this.getString("palette.selected"));
			}
		};

		form.add(palette);

		form.add(new AjaxButton("saveButton", form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
				roleService.save((RoleEntity) form.getModelObject());
				RoleEditPanel.this.onSave(target);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, final Form<?> form) {
				// repaint the feedback panel so errors are shown
				target.addComponent(feedback);
			}
		});
	}

	public abstract void onSave(AjaxRequestTarget target);
}
