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
package org.devproof.portal.core.module.tag.panel;

import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.tag.TagConstants;
import org.devproof.portal.core.module.tag.entity.BaseTagEntity;
import org.devproof.portal.core.module.tag.service.TagService;

/**
 * @author Carsten Hufe
 */
public class TagCloudBoxPanel<T extends BaseTagEntity<?>> extends Panel {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;

	private T selectedTag;
	private final DataView<T> dataView;

	public TagCloudBoxPanel(final String id, final TagService<T> tagService, final IModel<T> tagTarget,
			final Class<? extends Page> page, final PageParameters params) {
		super(id);
		add(CSSPackageResource.getHeaderContribution(TagConstants.REF_TAG_CSS));
		final PortalSession session = (PortalSession) getSession();
		List<T> tags = null;
		if (session.hasRight(tagService.getRelatedTagRight())) {
			tags = tagService
					.findMostPopularTags(0, configurationService.findAsInteger(TagConstants.CONF_BOX_NUM_TAGS));
		} else {
			tags = tagService.findMostPopularTags(session.getRole(), 0, configurationService
					.findAsInteger(TagConstants.CONF_BOX_NUM_TAGS));
		}
		if (params != null && params.containsKey("tag")) {
			selectedTag = tagService.findById(params.getString("tag"));
			tagTarget.setObject(this.selectedTag);
			// add the tag which is not in the top x
			if (selectedTag != null && !tags.contains(selectedTag)) {
				tags.add(selectedTag);
			}
		}

		ListDataProvider<T> provider = new ListDataProvider<T>(tags);
		dataView = new DataView<T>("liRow", provider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final Item<T> item) {
				final T tag = item.getModelObject();

				final BookmarkablePageLink<Void> link = new BookmarkablePageLink<Void>("tagLink", page);
				link.add(new Label("tagLabel", tag.getTagname()));
				item.add(link);
				if (tag.equals(TagCloudBoxPanel.this.selectedTag)) {
					item.add(new SimpleAttributeModifier("class", "selsidenav"));
				} else {
					link.setParameter("tag", tag.getTagname());
				}
			}

		};
		add(dataView);
		setVisible(tags.size() > 0);
	}

	public void cleanSelection() {
		selectedTag = null;
	}
}
