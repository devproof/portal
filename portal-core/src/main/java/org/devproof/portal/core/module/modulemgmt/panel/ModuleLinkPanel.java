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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.component.ExternalImage;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.service.RegistryService;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLinkEntity;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLinkEntity.LinkType;
import org.devproof.portal.core.module.modulemgmt.query.ModuleLinkQuery;
import org.devproof.portal.core.module.modulemgmt.service.ModuleService;

/**
 * @author Carsten Hufe
 */
public class ModuleLinkPanel extends Panel {

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "moduleLinkDataProvider")
	private transient QueryDataProvider<ModuleLinkEntity> moduleLinkDataProvider;
	@SpringBean(name = "moduleService")
	private transient ModuleService moduleService;
	@SpringBean(name = "registryService")
	private transient RegistryService registryService;

	private final WebMarkupContainer container;
	private final Form<ModuleLinkEntity> form;

	public ModuleLinkPanel(final String id, final LinkType linkType) {
		super(id);
		this.add(new Label("naviTitle", this.getString(linkType.toString().toLowerCase())));
		this.container = new WebMarkupContainer("refreshTable");
		this.container.setOutputMarkupId(true);
		this.add(this.container);

		this.form = new Form<ModuleLinkEntity>("form");
		this.container.add(this.form);

		ModuleLinkQuery query = new ModuleLinkQuery();
		query.setLinkType(linkType);
		this.moduleLinkDataProvider.setQueryObject(query);

		ModuleLinkView dataView = new ModuleLinkView("tableRow", this.moduleLinkDataProvider);
		this.form.add(dataView);
	}

	private class ModuleLinkView extends DataView<ModuleLinkEntity> {
		private static final long serialVersionUID = 1L;

		public ModuleLinkView(final String id, final IDataProvider<ModuleLinkEntity> dataProvider) {
			super(id, dataProvider);
		}

		@Override
		protected void populateItem(final Item<ModuleLinkEntity> item) {

			final ModuleLinkEntity link = item.getModelObject();
			item.add(new Label("sort", Integer.toString(link.getSort())));
			item.add(new Label("pageName", link.getPageName()));
			item.add(new Label("moduleName", link.getModuleName()));
			item.add(new CheckBox("visible", new PropertyModel<Boolean>(link, "visible")) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSelectionChanged(final Object newSelection) {
					ModuleLinkPanel.this.moduleService.save(link);
					ModuleLinkPanel.this.registryService.rebuildRegistries(link.getLinkType());
					Boolean selection = (Boolean) newSelection;
					if (selection) {
						info(this.getString("msg.selected"));
					} else {
						info(this.getString("msg.deselected"));
					}

				}

				@Override
				protected boolean wantOnSelectionChangedNotifications() {
					return true;
				}
			});

			item.add(new AjaxLink<ModuleLinkEntity>("upLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(final AjaxRequestTarget target) {
					ModuleLinkPanel.this.moduleService.moveUp(link);
					ModuleLinkPanel.this.registryService.rebuildRegistries(link.getLinkType());
					target.addComponent(ModuleLinkPanel.this.container);
				}
			}.add(new ExternalImage("upImage", CommonConstants.REF_UP_IMG)));

			item.add(new AjaxLink<ModuleLinkEntity>("downLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(final AjaxRequestTarget target) {
					ModuleLinkPanel.this.moduleService.moveDown(link);
					ModuleLinkPanel.this.registryService.rebuildRegistries(link.getLinkType());
					target.addComponent(ModuleLinkPanel.this.container);
				}
			}.add(new ExternalImage("downImage", CommonConstants.REF_DOWN_IMG)));

			item.add(new AttributeModifier("class", true, new AbstractReadOnlyModel<String>() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getObject() {
					return (item.getIndex() % 2 != 0) ? "even" : "odd";
				}
			}));
		}
	};
}
