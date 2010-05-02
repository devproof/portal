/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.user.panel;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.role.service.RoleService;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.service.UserService;

/**
 * @author Carsten Hufe
 */
public abstract class UserEditPanel extends Panel {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "roleService")
    private RoleService roleService;
    @SpringBean(name = "userService")
    private UserService userService;
    private IModel<UserEntity> userModel;
    private boolean creation;
    private FeedbackPanel feedback;
    private PasswordTextField password1;
    private PasswordTextField password2;

    public UserEditPanel(String id, IModel<UserEntity> userModel, boolean creation) {
        super(id, userModel);
        this.userModel = userModel;
        this.creation = creation;
        add(createFeedbackPanel());
        add(createUserEditForm());
    }

    private Form<UserEntity> createUserEditForm() {
        Form<UserEntity> form = new Form<UserEntity>("form", new CompoundPropertyModel<UserEntity>(userModel));
        form.add(createUsernameField());
        form.add(createFirstnameField());
        form.add(createLastnameField());
        form.add(createBirthdayField());
        form.add(createEmailField());
        form.add(createRoleDropDown());
        form.add(createPasswordField1());
        form.add(createPasswordField2());
        form.add(createActiveCheckBox());
        form.add(createConfirmedCheckBox());
        form.add(createEqualPasswordValidator());
        form.add(createSaveButton());
        form.add(createCancelButton());
        form.setOutputMarkupId(true);
        return form;
    }

    private CheckBox createConfirmedCheckBox() {
        return new CheckBox("confirmed");
    }

    private CheckBox createActiveCheckBox() {
        return new CheckBox("active");
    }

    private AjaxButton createSaveButton() {
        return new AjaxButton("saveButton") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                UserEntity user = (UserEntity) form.getModelObject();
                if (password1.getValue() != null && !"".equals(password1.getValue().trim())) {
                    user.setPlainPassword(password1.getValue());
                }
                userService.save(user);
                UserEditPanel.this.onSave(target);
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
                UserEditPanel.this.onCancel(target);
            }
        };
    }

    private EqualPasswordInputValidator createEqualPasswordValidator() {
        return new EqualPasswordInputValidator(password1, password2);
    }

    private PasswordTextField createPasswordField1() {
        password1 = new PasswordTextField("password1", new Model<String>());
        password1.setRequired(creation);
        return password1;
    }

    private PasswordTextField createPasswordField2() {
        password2 = new PasswordTextField("password2", new Model<String>());
        password2.setRequired(creation);
        return password2;
    }

    private DropDownChoice<?> createRoleDropDown() {
        IChoiceRenderer<RoleEntity> renderer = new ChoiceRenderer<RoleEntity>("description", "id");
        IModel<RoleEntity> roleModel = new PropertyModel<RoleEntity>(userModel, "role");
        IModel<List<RoleEntity>> availableRolesModel = createAvailableRolesModel();
        DropDownChoice<?> role = new DropDownChoice<RoleEntity>("role", roleModel, availableRolesModel, renderer);
        role.setRequired(true);
        return role;
    }

    private IModel<List<RoleEntity>> createAvailableRolesModel() {
        return new LoadableDetachableModel<List<RoleEntity>>() {
            private static final long serialVersionUID = 6780212125058885884L;

            @Override
            protected List<RoleEntity> load() {
                return roleService.findAll();
            }
        };
    }

    private FormComponent<String> createEmailField() {
        FormComponent<String> fc = new RequiredTextField<String>("email");
        fc.add(EmailAddressValidator.getInstance());
        fc.add(StringValidator.maximumLength(100));
        return fc;
    }

    private DateTextField createBirthdayField() {
        DateTextField dateTextField = new DateTextField("birthday");
        dateTextField.add(new DatePicker());
        return dateTextField;
    }

    private FormComponent<String> createLastnameField() {
        FormComponent<String> fc = new TextField<String>("lastname");
        fc.add(StringValidator.maximumLength(100));
        return fc;
    }

    private FormComponent<String> createFirstnameField() {
        FormComponent<String> fc = new TextField<String>("firstname");
        fc.add(StringValidator.maximumLength(100));
        return fc;
    }

    private FormComponent<String> createUsernameField() {
        FormComponent<String> fc = newUsernameField();
        fc.add(StringValidator.lengthBetween(3, 30));
        fc.add(createExistingUsernameValidator());
        fc.add(new PatternValidator("[A-Za-z0-9\\.]*"));
        return fc;
    }

	private FormComponent<String> newUsernameField() {
		return new RequiredTextField<String>("username") {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isEnabled() {
				return userModel.getObject().getId() == null;
			}
        	
        };
	}

    private AbstractValidator<String> createExistingUsernameValidator() {
        return new AbstractValidator<String>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onValidate(IValidatable<String> ivalidatable) {
                if (userService.existsUsername(ivalidatable.getValue()) && creation) {
                    error(ivalidatable);
                }
            }

            @Override
            protected String resourceKey() {
                return "existing.username";
            }
        };
    }

    private FeedbackPanel createFeedbackPanel() {
        feedback = new FeedbackPanel("feedbackPanel");
        feedback.setOutputMarkupId(true);
        return feedback;
    }

    public abstract void onSave(AjaxRequestTarget target);

    public abstract void onCancel(AjaxRequestTarget target);
}
