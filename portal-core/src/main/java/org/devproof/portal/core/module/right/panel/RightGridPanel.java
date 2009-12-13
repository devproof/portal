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
package org.devproof.portal.core.module.right.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.IFormModelUpdateListener;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.GridView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.right.service.RightService;

/**
 * Lists the rights on an edit panel
 * 
 * @author Carsten Hufe
 */
public class RightGridPanel extends Panel implements IFormModelUpdateListener {
	private static final long serialVersionUID = 1L;

	@SpringBean(name = "rightService")
	private RightService rightService;
	private List<RightEntity> allRights;
	private List<RightEntity> originalRightsListReference;
	private List<RightEntity> originalSelectedRights;

	public RightGridPanel(String id, String rightPrefix, IModel<List<RightEntity>> selectedRights) {
		super(id);
		originalRightsListReference = selectedRights.getObject();
		originalSelectedRights = new ArrayList<RightEntity>();
		for (Iterator<? extends RightEntity> it = originalRightsListReference.iterator(); it.hasNext();) {
			RightEntity right = it.next();
			if (right.getRight().startsWith(rightPrefix)) {
				originalSelectedRights.add(right);
				it.remove();
			}
		}
		allRights = rightService.findRightsStartingWith(rightPrefix);
		final Map<RightEntity, CheckBox> keepCheckBoxStateAfterValidation = new HashMap<RightEntity, CheckBox>();
		ListDataProvider<RightEntity> ldp = new ListDataProvider<RightEntity>(allRights);
		GridView<RightEntity> gridView = new GridView<RightEntity>("rights_rows", ldp) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<RightEntity> item) {
				RightEntity right = item.getModel().getObject();
				right.setSelected(originalSelectedRights.contains(right));
				CheckBox checkBox = keepCheckBoxStateAfterValidation.get(right);
				if (checkBox == null) {
					checkBox = new CheckBox("right_checkbox", new PropertyModel<Boolean>(right, "selected"));
					keepCheckBoxStateAfterValidation.put(right, checkBox);
				}
				item.add(checkBox);
				item.add(new Label("right", right.getDescription()));
			}

			@Override
			protected void populateEmptyItem(Item<RightEntity> item) {
				item.add(new CheckBox("right_checkbox").setVisible(false));
				item.add(new Label("right", ""));
			}
		};
		gridView.setColumns(3);
		add(gridView);
	}

	private List<? extends RightEntity> getSelectedRights() {
		List<RightEntity> newRights = new ArrayList<RightEntity>();
		for (RightEntity right : allRights) {
			if (right.isSelected()) {
				newRights.add(right);
			}
		}
		return newRights;
	}

	@Override
	public void updateModel() {
		originalRightsListReference.addAll(getSelectedRights());
	}
}
