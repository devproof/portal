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
package org.devproof.portal.core.module.common.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Dynamic info message in a popup
 * 
 * @author Carsten Hufe
 * 
 */
public class InfoMessagePanel extends Panel {

	private static final long serialVersionUID = 1L;

	private String message;
	private ModalWindow modalWindow;

	public InfoMessagePanel(String id, String message, ModalWindow modalWindow) {
		super(id);
		this.message = message;
		this.modalWindow = modalWindow;
		setModalWindowSize();
		add(createOkButtonForm());
	}

	private Form<?> createOkButtonForm() {
		Form<?> form = new Form<Object>("form");
		form.add(createInfoMessageLabel());
		form.add(createOkAjaxButton());
		form.setOutputMarkupId(true);
		return form;
	}

	private Label createInfoMessageLabel() {
		return new Label("infoMessage", message);
	}

	private AjaxButton createOkAjaxButton() {
		return new AjaxButton("okButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				modalWindow.close(target);
			}
		};
	}

	private void setModalWindowSize() {
		modalWindow.setInitialHeight(140);
		modalWindow.setInitialWidth(300);
	}
}
