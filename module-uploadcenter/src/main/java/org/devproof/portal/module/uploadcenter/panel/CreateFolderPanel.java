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
package org.devproof.portal.module.uploadcenter.panel;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.value.ValueMap;
import org.apache.wicket.validation.validator.PatternValidator;

/**
 * @author Carsten Hufe
 */
abstract public class CreateFolderPanel extends Panel {
	private static final long serialVersionUID = 1L;

	public CreateFolderPanel(final String id, final File actFolder) {
		super(id);
		final FeedbackPanel feedback = new FeedbackPanel("feedbackPanel");
		feedback.setOutputMarkupId(true);
		add(feedback);

		Form<ValueMap> form = new Form<ValueMap>("form", new CompoundPropertyModel<ValueMap>(new ValueMap()));
		form.setOutputMarkupId(true);
		add(form);

		final RequiredTextField<String> foldername = new RequiredTextField<String>("foldername");
		foldername.add(new PatternValidator("[A-Za-z0-9\\.]*"));
		form.add(foldername);

		form.add(new AjaxButton("createButton", form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
				try {
					FileUtils
							.forceMkdir(new File(actFolder.getAbsolutePath() + File.separator + foldername.getValue()));
				} catch (IOException e) {
					throw new UnhandledException(e);
				}
				CreateFolderPanel.this.onCreate(target);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, final Form<?> form) {
				target.addComponent(feedback);
			}
		});
	}

	public abstract void onCreate(AjaxRequestTarget target);
}
