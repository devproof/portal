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
package org.devproof.portal.core.module.modulemgmt.panel;

import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.devproof.portal.core.config.BoxConfiguration;
import org.devproof.portal.core.config.ModuleConfiguration;
import org.devproof.portal.core.config.PageConfiguration;

/**
 * @author Carsten Hufe
 */
public class ModuleInfoPanel extends Panel {

	private static final long serialVersionUID = 1L;

	public ModuleInfoPanel(final String id, final ModuleConfiguration module) {
		super(id);
		// List pages
		RepeatingView tableRow = new RepeatingView("pageRow");
		this.add(tableRow);
		if (module.getPages().isEmpty()) {
			WebMarkupContainer row = new WebMarkupContainer(tableRow.newChildId());
			row.add(new Label("page", this.getString("nopages")));
			row.add(new Label("mountPath", ""));
			tableRow.add(row);
		} else {
			for (PageConfiguration page : module.getPages()) {
				WebMarkupContainer row = new WebMarkupContainer(tableRow.newChildId());
				row.add(new Label("page", page.getPageClass().getSimpleName()));
				row.add(new Label("mountPath", page.getMountPath()));
				tableRow.add(row);
			}
		}
		// List boxes
		tableRow = new RepeatingView("boxRow");
		this.add(tableRow);
		if (module.getBoxes().isEmpty()) {
			WebMarkupContainer row = new WebMarkupContainer(tableRow.newChildId());
			row.add(new Label("box", this.getString("noboxes")));
			row.add(new Label("name", ""));
			tableRow.add(row);
		} else {
			for (BoxConfiguration box : module.getBoxes()) {
				WebMarkupContainer row = new WebMarkupContainer(tableRow.newChildId());
				row.add(new Label("box", box.getBoxClass().getSimpleName()));
				row.add(new Label("name", box.getName()));
				tableRow.add(row);
			}
		}

		// List entities
		tableRow = new RepeatingView("entityRow");
		this.add(tableRow);
		if (module.getEntities().isEmpty()) {
			WebMarkupContainer row = new WebMarkupContainer(tableRow.newChildId());
			row.add(new Label("entity", this.getString("noentities")));
			row.add(new Label("table", ""));
			tableRow.add(row);
		} else {
			for (Class<?> entity : module.getEntities()) {
				WebMarkupContainer row = new WebMarkupContainer(tableRow.newChildId());
				row.add(new Label("entity", entity.getSimpleName()));
				String table = entity.getSimpleName();
				Table tableAnno = entity.getAnnotation(Table.class);
				if (tableAnno != null && StringUtils.isNotEmpty(tableAnno.name())) {
					table = tableAnno.name();
				}
				row.add(new Label("table", table));
				tableRow.add(row);
			}
		}
	}
}
