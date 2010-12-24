/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.modulemgmt.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.config.Secured;
import org.devproof.portal.core.module.common.component.TooltipLabel;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.modulemgmt.ModuleMgmtConstants;
import org.devproof.portal.core.module.modulemgmt.bean.ModuleBean;
import org.devproof.portal.core.module.modulemgmt.panel.ModuleInfoPanel;
import org.devproof.portal.core.module.modulemgmt.service.ModuleService;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@Secured(ModuleMgmtConstants.ADMIN_RIGHT)
@ModulePage(mountPath = "/admin/modules", registerGlobalAdminLink = true)
public class ModuleOverviewPage extends TemplatePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "moduleService")
    private ModuleService moduleService;

    public ModuleOverviewPage(PageParameters params) {
        super(params);
        add(createRepeatingModuleOverview());
    }

    private RepeatingView createRepeatingModuleOverview() {
        List<ModuleBean> modules = moduleService.findModules();
        RepeatingView table = new RepeatingView("repeatingModuleOverview");
        for (ModuleBean module : modules) {
            table.add(createModuleRow(table.newChildId(), module));
        }
        return table;
    }

    private WebMarkupContainer createModuleRow(String id, ModuleBean module) {
        WebMarkupContainer row = new WebMarkupContainer(id);
        row.add(createTooltipLabel(module));
        row.add(createModuleVersionLabel(module));
        row.add(createAuthorHomepageLink(module));
        row.add(createPortalVersionLabel(module));
        row.add(createLocationLabel(module));
        return row;
    }

    private TooltipLabel createTooltipLabel(ModuleBean module) {
        ModuleInfoPanel moduleTooltip = createModuleTooltipPanel(module);
        Label label = createTooltipLabelLabel(module);
        return new TooltipLabel("name", label, moduleTooltip);
    }

    private Label createTooltipLabelLabel(ModuleBean module) {
        return new Label("label", module.getConfiguration().getName());
    }

    private Label createModuleVersionLabel(ModuleBean module) {
        return new Label("moduleVersion", module.getConfiguration().getModuleVersion());
    }

    private ExternalLink createAuthorHomepageLink(ModuleBean module) {
        return new ExternalLink("authorHomepageLink", module.getConfiguration().getUrl(), module.getConfiguration().getAuthor());
    }

    private Label createPortalVersionLabel(ModuleBean module) {
        return new Label("portalVersion", module.getConfiguration().getPortalVersion());
    }

    private Label createLocationLabel(ModuleBean module) {
        return new Label("location", module.getLocation());
    }

    private ModuleInfoPanel createModuleTooltipPanel(ModuleBean module) {
        return new ModuleInfoPanel("tooltip", module.getConfiguration());
    }
}
