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

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.devproof.portal.core.module.common.CommonConstants;

/**
 * Shows the edit and delete button
 * 
 * @author Carsten Hufe
 * 
 * @param <T>
 *            Entity type
 */
public abstract class AuthorPanel<T> extends Panel {
	private static final long serialVersionUID = 1L;

	private final T entity;
	private Class<? extends Page> redirectPageClazz = null;
	private PageParameters redirectParams = null;
	private boolean deleted = false;

	public AuthorPanel(final String id, final T entity) {
		super(id);
		this.entity = entity;
		final ModalWindow modalWindow = new ModalWindow("modalWindow");
		modalWindow.setOutputMarkupId(true);
		add(modalWindow);
		add(new AjaxLink<T>("editLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				AuthorPanel.this.onEdit(target);
			}
		}.add(new Image("editImage", CommonConstants.REF_EDIT_IMG)));

		add(new AjaxLink<T>("deleteLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				final ConfirmDeletePanel<T> confirmDeletePanel = new ConfirmDeletePanel<T>(modalWindow.getContentId(),
						entity, modalWindow) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onDelete(final AjaxRequestTarget target, final Form<?> form) {
						AuthorPanel.this.deleted = true;
						modalWindow.close(target);
						AuthorPanel.this.onDelete(target);
					}

				};
				modalWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
					private static final long serialVersionUID = 1L;

					public void onClose(final AjaxRequestTarget target) {
						if (AuthorPanel.this.redirectPageClazz != null && AuthorPanel.this.deleted) {
							setResponsePage(AuthorPanel.this.redirectPageClazz, AuthorPanel.this.redirectParams);
							AuthorPanel.this.deleted = false;
						}
					}
				});
				modalWindow.setContent(confirmDeletePanel);
				modalWindow.show(target);
			}
		}.add(new Image("deleteImage", CommonConstants.REF_DELETE_IMG)));
	}

	public T getEntity() {
		return this.entity;
	}

	public AuthorPanel<T> setRedirectPage(final Class<? extends Page> redirectPageClazz,
			final PageParameters redirectParams) {
		this.redirectPageClazz = redirectPageClazz;
		this.redirectParams = redirectParams;
		return this;
	}

	public abstract void onEdit(AjaxRequestTarget target);

	public abstract void onDelete(AjaxRequestTarget target);
}
