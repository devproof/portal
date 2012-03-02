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
package org.devproof.portal.core.module.box.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;
import org.devproof.portal.core.config.BoxConfiguration;
import org.devproof.portal.core.config.Secured;
import org.devproof.portal.core.module.box.BoxConstants;
import org.devproof.portal.core.module.box.entity.Box;
import org.devproof.portal.core.module.box.registry.BoxRegistry;
import org.devproof.portal.core.module.box.service.BoxService;
import org.devproof.portal.core.module.common.component.PortalFeedbackPanel;
import org.devproof.portal.core.module.common.component.ValidationDisplayBehaviour;
import org.devproof.portal.core.module.common.panel.OtherBoxPanel;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@Secured(BoxConstants.ADMIN_RIGHT)
public abstract class BoxEditPanel extends Panel {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "boxService")
    private BoxService boxService;
    @SpringBean(name = "boxRegistry")
    private BoxRegistry boxRegistry;
    private FeedbackPanel feedback;
    private IModel<BoxConfiguration> boxSelectionModel;
    private IModel<Box> boxModel;
    private WebMarkupContainer otherPageConfigurationContainer;
    private Form<Box> boxForm;

    public BoxEditPanel(String id, IModel<Box> boxModel) {
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

    private Form<Box> createBoxEditForm() {
        CompoundPropertyModel<Box> formModel = new CompoundPropertyModel<Box>(boxModel);
        boxForm = new Form<Box>("form", formModel);
        boxForm.add(createBoxTypeChoice());
        boxForm.add(createTitleField());
        boxForm.add(createHideTitleCheckBox());
        boxForm.add(createCustomStyleField());
        boxForm.add(createOtherBoxConfigurationContainer());
        boxForm.add(createAjaxButton());
        boxForm.add(createCancelButton());
        boxForm.setOutputMarkupId(true);
        return boxForm;
    }

    private WebMarkupContainer createOtherBoxConfigurationContainer() {
        otherPageConfigurationContainer = newOtherPageConfigurationContainer();
        otherPageConfigurationContainer.add(createContentField());
        otherPageConfigurationContainer.setOutputMarkupId(true);
        otherPageConfigurationContainer.setOutputMarkupPlaceholderTag(true);
        return otherPageConfigurationContainer;
    }

    private WebMarkupContainer newOtherPageConfigurationContainer() {
        return new WebMarkupContainer("otherPageConfiguration") {
            private static final long serialVersionUID = -2477387792864679307L;

            @Override
            public boolean isVisible() {
                BoxConfiguration object = boxSelectionModel.getObject();
                return object != null && OtherBoxPanel.class.equals(object.getBoxClass());
            }
        };
    }

    private TextField<String> createCustomStyleField() {
        return new TextField<String>("customStyle");
    }

    private CheckBox createHideTitleCheckBox() {
        return new CheckBox("hideTitle");
    }

    private DropDownChoice<BoxConfiguration> createBoxTypeChoice() {
        List<BoxConfiguration> confs = boxRegistry.getRegisteredBoxes();
        ChoiceRenderer<BoxConfiguration> choiceRenderer = new ChoiceRenderer<BoxConfiguration>("name", "boxClass");
        DropDownChoice<BoxConfiguration> boxTypeChoice = new DropDownChoice<BoxConfiguration>("boxType", boxSelectionModel, confs, choiceRenderer);
        boxTypeChoice.add(createOnSelectUpdateBEaviour());
        boxTypeChoice.setRequired(true);
        boxTypeChoice.add(new ValidationDisplayBehaviour());
        return boxTypeChoice;
    }

    private AjaxFormComponentUpdatingBehavior createOnSelectUpdateBEaviour() {
        return new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 5607813611383990978L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(otherPageConfigurationContainer);
            }
        };
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
                Box box = boxModel.getObject();
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
                target.add(feedback);
                target.add(form);
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
        feedback = new PortalFeedbackPanel("feedbackPanel");
        feedback.setOutputMarkupId(true);
        return feedback;
    }

    protected abstract void onSave(AjaxRequestTarget target);

    protected abstract void onCancel(AjaxRequestTarget target);
}
