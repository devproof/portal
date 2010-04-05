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
package org.devproof.portal.module.uploadcenter.panel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;
import org.apache.wicket.validation.validator.PatternValidator;

import java.io.File;
import java.io.IOException;

/**
 * @author Carsten Hufe
 */
public abstract class CreateFolderPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private FeedbackPanel feedbackPanel;
    private IModel<File> actualFolderModel;
    private String foldername = "";

    public CreateFolderPanel(String id, IModel<File> actualFolderModel) {
        super(id);
        this.actualFolderModel = actualFolderModel;
        add(feedbackPanel = createFeedbackPanel());
        add(createCreateFolderForm());
    }

    private Form<ValueMap> createCreateFolderForm() {
        Form<ValueMap> form = new Form<ValueMap>("form");
        form.add(createFoldernameField());
        form.add(createCreateFolderButton());
        form.setOutputMarkupId(true);
        return form;
    }

    private AjaxButton createCreateFolderButton() {
        return new AjaxButton("createButton") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                File actualFolder = actualFolderModel.getObject();
                try {
                    FileUtils.forceMkdir(new File(actualFolder.getAbsolutePath() + File.separator + foldername));
                } catch (IOException e) {
                    throw new UnhandledException(e);
                }
                CreateFolderPanel.this.onCreate(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(feedbackPanel);
            }
        };
    }

    private FeedbackPanel createFeedbackPanel() {
        FeedbackPanel feedback = new FeedbackPanel("feedbackPanel");
        feedback.setOutputMarkupId(true);
        return feedback;
    }

    private RequiredTextField<String> createFoldernameField() {
        IModel<String> foldernameModel = new PropertyModel<String>(this, "foldername");
        RequiredTextField<String> foldername = new RequiredTextField<String>("foldername", foldernameModel);
        foldername.add(new PatternValidator("[A-Za-z0-9\\.]*"));
        return foldername;
    }

    public abstract void onCreate(AjaxRequestTarget target);
}
