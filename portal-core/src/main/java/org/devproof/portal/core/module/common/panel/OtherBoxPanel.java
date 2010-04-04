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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
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
    private IModel<BoxEntity> boxModel;

    public OtherBoxPanel(String id, IModel<BoxEntity> boxModel) {
		super(id, boxModel);
        this.boxModel = boxModel;
		add(createTitleLabel());
		add(createContentLabel());
	}

	private Label createContentLabel() {
        IModel<String> contentModel = new PropertyModel<String>(boxModel, "content");
        Label content = new Label("content", contentModel);
		content.setEscapeModelStrings(false);
		return content;
	}

	private Label createTitleLabel() {
        IModel<String> titleModel = new PropertyModel<String>(boxModel, "title");
        titleLabel = new Label("title", titleModel);
		return titleLabel;
	}

	@Override
	public void setTitleVisible(boolean visible) {
		titleLabel.setVisible(visible);
	}
}
