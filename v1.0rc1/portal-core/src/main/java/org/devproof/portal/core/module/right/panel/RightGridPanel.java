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
package org.devproof.portal.core.module.right.panel;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.GridView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.right.service.RightService;

/**
 * Lists the rights on an edit panel
 * 
 * @author Carsten Hufe
 */
public class RightGridPanel extends Panel {
	@SpringBean(name = "rightService")
	private RightService rightService;
	private final List<RightEntity> allRights;
	private static final long serialVersionUID = 1L;

	public RightGridPanel(final String id, final String rightPrefix, final List<RightEntity> selectedRights) {
		super(id);
		this.allRights = this.rightService.findRightsStartingWith(rightPrefix);
		ListDataProvider<RightEntity> ldp = new ListDataProvider<RightEntity>(this.allRights);
		GridView<RightEntity> gridView = new GridView<RightEntity>("rights_rows", ldp) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final Item<RightEntity> item) {
				final RightEntity right = item.getModelObject();
				right.setSelected(selectedRights != null && selectedRights.contains(right));
				item.add(new CheckBox("right_checkbox", new PropertyModel<Boolean>(right, "selected")));
				item.add(new Label("right", right.getDescription()));
			}

			@Override
			protected void populateEmptyItem(final Item<RightEntity> item) {
				item.add(new CheckBox("right_checkbox").setVisible(false));
				item.add(new Label("right", ""));
			}
		};
		gridView.setColumns(3);
		this.add(gridView);
	}

	public List<RightEntity> getSelectedRights() {
		List<RightEntity> newRights = new ArrayList<RightEntity>();
		for (RightEntity right : this.allRights) {
			if (right.isSelected()) {
				newRights.add(right);
			}
		}
		return newRights;
	}
}
