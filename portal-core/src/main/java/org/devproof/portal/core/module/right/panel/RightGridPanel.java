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
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.IFormModelUpdateListener;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
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
	private String rightPrefix;
    private IModel<List<RightEntity>> originalRightsListModel;
    private List<RightEntity> allRights;
    private List<RightEntity> originalSelectedRights;

	public RightGridPanel(String id, String rightPrefix, IModel<List<RightEntity>> selectedRights) {
		super(id);
		this.rightPrefix = rightPrefix;
		this.originalRightsListModel = selectedRights;
		this.originalSelectedRights = createOriginalSelectedRights();
        this.allRights = rightService.findRightsStartingWith(this.rightPrefix);
        add(createRightGridView());
	}

    private GridView<RightEntity> createRightGridView() {
		ListDataProvider<RightEntity> ldp = new ListDataProvider<RightEntity>(allRights);
		GridView<RightEntity> gridView = newRightGridView(ldp);
		gridView.setColumns(3);
		gridView.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
		return gridView;
	}

	private GridView<RightEntity> newRightGridView(ListDataProvider<RightEntity> ldp) {
		return new GridView<RightEntity>("rights_rows", ldp) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<RightEntity> item) {
				setRightOriginalSelectionState(item);
				item.add(createRightCheckBox(item));
				item.add(createRightNameLabel(item));
			}

			private void setRightOriginalSelectionState(Item<RightEntity> item) {
				RightEntity right = item.getModelObject();
				right.setSelected(originalSelectedRights.contains(right));
			}

			private Label createRightNameLabel(Item<RightEntity> item) {
				return new Label("right", item.getModelObject().getDescription());
			}

			private CheckBox createRightCheckBox(Item<RightEntity> item) {
				RightEntity right = item.getModelObject();
				return new CheckBox("right_checkbox", new PropertyModel<Boolean>(right, "selected"));
			}

			@Override
			protected void populateEmptyItem(Item<RightEntity> item) {
				item.add(createHiddenRightCheckBox());
				item.add(createEmptyRightNameLabel());
			}

			private CheckBox createHiddenRightCheckBox() {
				CheckBox cb = new CheckBox("right_checkbox");
				cb.setVisible(false);
				return cb;
			}

			private Label createEmptyRightNameLabel() {
				return new Label("right", "");
			}
		};
	}

	private List<RightEntity> createOriginalSelectedRights() {
		List<RightEntity> originalSelectedRights = new ArrayList<RightEntity>();
        List<RightEntity> originalRightsList = originalRightsListModel.getObject();
		for (Iterator<? extends RightEntity> it = originalRightsList.iterator(); it.hasNext();) {
			RightEntity right = it.next();
			if (right.getRight().startsWith(rightPrefix)) {
				originalSelectedRights.add(right);
				it.remove();
			}
		}
        return originalSelectedRights;
	}

	private List<RightEntity> getSelectedRights() {
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
        // TODO test ob es mit rights setzen funktioniert
		originalRightsListModel.getObject().addAll(getSelectedRights());
	}
}
