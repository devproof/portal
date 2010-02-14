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
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

/**
 * Modal window with confirmation for deleting something
 * 
 * @author Carsten Hufe
 * 
 */
public abstract class ConfirmDeletePanel<T> extends Panel {

	private static final long serialVersionUID = 1L;

	private T entity;
	private BubblePanel bubblePanel;

	public ConfirmDeletePanel(String id, T entity, BubblePanel bubblePanel) {
		super(id);
		this.entity = entity;
		this.bubblePanel = bubblePanel;
		add(createConfirmDeletePanelForm());

	}

	private Form<T> createConfirmDeletePanelForm() {
		Form<T> form = new Form<T>("form", new CompoundPropertyModel<T>(entity));
		form.add(createYesAjaxButton());
		form.add(createNoAjaxButton());
		form.setOutputMarkupId(true);
		return form;
	}

	private AjaxButton createNoAjaxButton() {
		return new AjaxButton("noButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				bubblePanel.hide(target);
			}
		};
	}

	private AjaxButton createYesAjaxButton() {
		return new AjaxButton("yesButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				ConfirmDeletePanel.this.onDelete(target, form);
			}
		};
	}

	/**
	 * On delete, if the yes button was pressed
	 */
	public abstract void onDelete(AjaxRequestTarget target, Form<?> form);

}
