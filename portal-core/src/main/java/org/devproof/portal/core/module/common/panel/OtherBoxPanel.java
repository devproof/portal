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
package org.devproof.portal.core.module.common.panel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.devproof.portal.core.module.box.entity.BoxEntity;
import org.devproof.portal.core.module.box.panel.BoxTitleVisibility;

/**
 * Box with your own content
 * 
 * @author Carsten Hufe
 */
public class OtherBoxPanel extends Panel implements BoxTitleVisibility {

	private static final long serialVersionUID = 1L;

	private Label titleLabel;

	public OtherBoxPanel(final String id, final IModel<BoxEntity> model) {
		super(id, model);
		BoxEntity box = model.getObject();
		add(titleLabel = new Label("title", box.getTitle()));
		add(new Label("content", box.getContent()).setEscapeModelStrings(false));
	}

	@Override
	public void setTitleVisible(final boolean visible) {
		titleLabel.setVisible(visible);
	}
}
