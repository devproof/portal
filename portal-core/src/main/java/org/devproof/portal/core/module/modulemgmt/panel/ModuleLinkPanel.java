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
package org.devproof.portal.core.module.modulemgmt.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.CommonConstants;
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
	private QueryDataProvider<ModuleLinkEntity> moduleLinkDataProvider;
	@SpringBean(name = "moduleService")
	private ModuleService moduleService;
	@SpringBean(name = "registryService")
	private RegistryService registryService;

	private final WebMarkupContainer container;
	private final Form<ModuleLinkEntity> form;

	public ModuleLinkPanel(final String id, final LinkType linkType) {
		super(id);
		add(new Label("naviTitle", getString(linkType.toString().toLowerCase())));
		container = new WebMarkupContainer("refreshTable");
		container.setOutputMarkupId(true);
		add(container);

		form = new Form<ModuleLinkEntity>("form");
		container.add(form);

		ModuleLinkQuery query = new ModuleLinkQuery();
		query.setLinkType(linkType);
		moduleLinkDataProvider.setQueryObject(query);

		ModuleLinkView dataView = new ModuleLinkView("tableRow", moduleLinkDataProvider);
		form.add(dataView);
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
					moduleService.save(link);
					registryService.rebuildRegistries(link.getLinkType());
					Boolean selection = (Boolean) newSelection;
					if (selection) {
						info(getString("msg.selected"));
					} else {
						info(getString("msg.deselected"));
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
					moduleService.moveUp(link);
					registryService.rebuildRegistries(link.getLinkType());
					target.addComponent(container);
				}
			}.add(new Image("upImage", CommonConstants.REF_UP_IMG)));

			item.add(new AjaxLink<ModuleLinkEntity>("downLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(final AjaxRequestTarget target) {
					moduleService.moveDown(link);
					registryService.rebuildRegistries(link.getLinkType());
					target.addComponent(container);
				}
			}.add(new Image("downImage", CommonConstants.REF_DOWN_IMG)));

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
