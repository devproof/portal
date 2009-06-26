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
package org.devproof.portal.core.module.modulemgmt.page;

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.component.TooltipLabel;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.modulemgmt.bean.ModuleBean;
import org.devproof.portal.core.module.modulemgmt.panel.ModuleInfoPanel;
import org.devproof.portal.core.module.modulemgmt.service.ModuleService;

/**
 * @author Carsten Hufe
 */
public class ModuleOverviewPage extends TemplatePage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "moduleService")
	private transient ModuleService moduleService;

	public ModuleOverviewPage(final PageParameters params) {
		super(params);
		List<ModuleBean> modules = this.moduleService.findModules();
		RepeatingView tableRow = new RepeatingView("tableRow");
		this.add(tableRow);
		for (ModuleBean module : modules) {
			WebMarkupContainer row = new WebMarkupContainer(tableRow.newChildId());

			ModuleInfoPanel moduleInfo = new ModuleInfoPanel("tooltip", module.getConfiguration());
			row.add(new TooltipLabel("name", new Label("label", module.getConfiguration().getName()), moduleInfo));
			row.add(new Label("moduleVersion", module.getConfiguration().getModuleVersion()));
			row.add(new ExternalLink("authorHomepageLink", module.getConfiguration().getUrl(), module.getConfiguration().getAuthor()));
			row.add(new Label("portalVersion", module.getConfiguration().getPortalVersion()));
			row.add(new Label("location", module.getLocation()));
			tableRow.add(row);
		}
	}
}
