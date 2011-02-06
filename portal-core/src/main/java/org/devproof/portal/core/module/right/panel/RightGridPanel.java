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
package org.devproof.portal.core.module.right.panel;

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
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.core.module.right.service.RightService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private IModel<List<Right>> originalRightsListModel;
    private List<Right> allRights;
    private List<Right> originalSelectedRights;

    public RightGridPanel(String id, String rightPrefix, IModel<List<Right>> selectedRights) {
        super(id);
        this.rightPrefix = rightPrefix;
        this.originalRightsListModel = selectedRights;
        this.originalSelectedRights = createOriginalSelectedRights();
        this.allRights = rightService.findRightsStartingWith(this.rightPrefix);
        add(createRightGridView());
    }

    private GridView<Right> createRightGridView() {
        ListDataProvider<Right> ldp = new ListDataProvider<Right>(allRights);
        GridView<Right> gridView = newRightGridView(ldp);
        gridView.setColumns(3);
        gridView.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
        return gridView;
    }

    private GridView<Right> newRightGridView(ListDataProvider<Right> ldp) {
        return new GridView<Right>("rights_rows", ldp) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(Item<Right> item) {
                setRightOriginalSelectionState(item);
                item.add(createRightCheckBox(item));
                item.add(createRightNameLabel(item));
            }

            private void setRightOriginalSelectionState(Item<Right> item) {
                Right right = item.getModelObject();
                right.setSelected(originalSelectedRights.contains(right));
            }

            private Label createRightNameLabel(Item<Right> item) {
                return new Label("right", item.getModelObject().getDescription());
            }

            private CheckBox createRightCheckBox(Item<Right> item) {
                Right right = item.getModelObject();
                return new CheckBox("right_checkbox", new PropertyModel<Boolean>(right, "selected"));
            }

            @Override
            protected void populateEmptyItem(Item<Right> item) {
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

    private List<Right> createOriginalSelectedRights() {
        List<Right> originalSelectedRights = new ArrayList<Right>();
        List<Right> originalRightsList = originalRightsListModel.getObject();
        for (Iterator<? extends Right> it = originalRightsList.iterator(); it.hasNext();) {
            Right right = it.next();
            if (right.getRight().startsWith(rightPrefix)) {
                originalSelectedRights.add(right);
                it.remove();
            }
        }
        return originalSelectedRights;
    }

    private List<Right> getSelectedRights() {
        List<Right> newRights = new ArrayList<Right>();
        for (Right right : allRights) {
            if (right.isSelected()) {
                newRights.add(right);
            }
        }
        return newRights;
    }

    @Override
    public void updateModel() {
    	originalRightsListModel.getObject().removeAll(originalSelectedRights);
        originalRightsListModel.getObject().addAll(getSelectedRights());
    }
}
