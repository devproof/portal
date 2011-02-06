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
package org.devproof.portal.module.otherpage.panel;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.module.otherpage.entity.OtherPage;

/**
 * @author Carsten Hufe
 */
public class OtherPagePrintPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private IModel<OtherPage> otherPageModel;

	public OtherPagePrintPanel(String id, IModel<OtherPage> otherPageModel) {
		super(id);
		this.otherPageModel = otherPageModel;
		add(createMetaInfoPanel());
		add(createContentLabel());
	}

	private MetaInfoPanel<?> createMetaInfoPanel() {
		return new MetaInfoPanel<OtherPage>("metaInfo", otherPageModel) {
            private static final long serialVersionUID = 606108060575073163L;

            @Override
            public boolean isVisible() {
                OtherPage otherPage = otherPageModel.getObject();
                return otherPage.getId() != null;
            }
        };
	}

	private ExtendedLabel createContentLabel() {
		IModel<String> contentModel = new PropertyModel<String>(otherPageModel, "content");
		return new ExtendedLabel("content", contentModel);
	}
}
