/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.right.panel;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.PatternValidator;
import org.devproof.portal.core.module.common.component.PortalFeedbackPanel;
import org.devproof.portal.core.module.common.component.ValidationDisplayBehaviour;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.core.module.right.service.RightService;
import org.devproof.portal.core.module.role.entity.Role;
import org.devproof.portal.core.module.role.service.RoleService;

import java.util.Collection;
import java.util.List;

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
    private IModel<Right> rightModel;
    private boolean rightNameEditable;
    private Form<Right> rightForm;

    public RightEditPanel(String id, IModel<Right> rightModel, boolean rightNameEditable) {
        super(id, rightModel);
        this.rightModel = rightModel;
        this.rightNameEditable = rightNameEditable;
        add(createFeedbackPanel());
        add(createRightEditForm());
    }

    private Form<Right> createRightEditForm() {
        rightForm = new Form<Right>("form", new CompoundPropertyModel<Right>(rightModel));
        rightForm.add(createRightNameField());
        rightForm.add(createDescriptionField());
        rightForm.add(createRolesPalette());
        rightForm.add(createSaveButton());
        rightForm.add(createCancelButton());
        rightForm.setOutputMarkupId(true);
        return rightForm;
    }

    private AjaxSubmitLink createSaveButton() {
        return new AjaxSubmitLink("saveButton") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                rightService.save((Right) form.getModelObject());
                RightEditPanel.this.onSave(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedback);
                target.add(rightForm);
            }
        };
    }

    private AjaxLink<Void> createCancelButton() {
        return new AjaxLink<Void>("cancelButton") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                onCancel(target);
            }
        };
    }

    private Palette<Role> createRolesPalette() {
        IChoiceRenderer<Role> renderer = new ChoiceRenderer<Role>("description", "id");
        IModel<Collection<Role>> allRoles = createAllRolesModel();
        IModel<List<Role>> rightsRoles = new PropertyModel<List<Role>>(rightModel, "roles");
        return newRolesPalette(renderer, allRoles, rightsRoles);
    }

    private IModel<Collection<Role>> createAllRolesModel() {
        return new LoadableDetachableModel<Collection<Role>>() {
            private static final long serialVersionUID = 5727789303717749700L;

            @Override
            protected Collection<Role> load() {
                return roleService.findAllOrderByDescription();
            }
        };
    }

    private Palette<Role> newRolesPalette(IChoiceRenderer<Role> renderer, IModel<Collection<Role>> allRoles, IModel<List<Role>> rightsRoles) {
        return new Palette<Role>("roles", rightsRoles, allRoles, renderer, 10, false) {
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
    }

    private FormComponent<String> createDescriptionField() {
        TextField<String> fc = new RequiredTextField<String>("description");
        fc.add(new ValidationDisplayBehaviour());
        return fc;
    }

    private FormComponent<String> createRightNameField() {
        FormComponent<String> fc = new RequiredTextField<String>("right");
        fc.add(new PatternValidator("[A-Za-z0-9\\.]*"));
        fc.setEnabled(rightNameEditable);
        fc.add(new ValidationDisplayBehaviour());
        return fc;
    }

    private FeedbackPanel createFeedbackPanel() {
        feedback = new PortalFeedbackPanel("feedbackPanel");
        feedback.setOutputMarkupId(true);
        return feedback;
    }

    public abstract void onSave(AjaxRequestTarget target);

    public abstract void onCancel(AjaxRequestTarget target);
}
