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
package org.devproof.portal.core.module.tag.panel;

import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.box.panel.BoxTitleVisibility;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.tag.TagConstants;
import org.devproof.portal.core.module.tag.entity.BaseTagEntity;
import org.devproof.portal.core.module.tag.service.TagService;

/**
 * @author Carsten Hufe
 */
public class TagCloudBoxPanel<T extends BaseTagEntity<?>> extends Panel implements BoxTitleVisibility {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;
	private TagService<T> tagService;
	private IModel<T> tagTarget;
	private Class<? extends Page> page;
	private PageParameters params;
	private T selectedTag;
	private DataView<T> dataView;
	private WebMarkupContainer titleContainer;
	private List<T> tags;

	public TagCloudBoxPanel(String id, TagService<T> tagService, IModel<T> tagTarget, Class<? extends Page> page,
			PageParameters params) {
		super(id);
		this.tagService = tagService;
		this.tagTarget = tagTarget;
		this.page = page;
		this.params = params;
		setTags();
		setSelectedTag();
		setVisibility();
		add(createCSSHeaderContributor());
		add(createTagDataView());
		add(createTitleContainer());
	}

	private void setVisibility() {
		setVisible(tags.size() > 0);
	}

	private WebMarkupContainer createTitleContainer() {
		titleContainer = new WebMarkupContainer("title");
		return titleContainer;
	}

	private DataView<T> createTagDataView() {
		ListDataProvider<T> provider = new ListDataProvider<T>(tags);
		return newTagDataView(provider);
	}

	private DataView<T> newTagDataView(ListDataProvider<T> provider) {
		dataView = new DataView<T>("liRow", provider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<T> item) {
				if (isTagSelected(item)) {
					item.add(createClassTagSelectionModifier());
				}
				item.add(createTagLink(item));
			}

			private SimpleAttributeModifier createClassTagSelectionModifier() {
				return new SimpleAttributeModifier("class", "selsidenav");
			}

			private BookmarkablePageLink<Void> createTagLink(Item<T> item) {
				T tag = item.getModelObject();
				BookmarkablePageLink<Void> link = new BookmarkablePageLink<Void>("tagLink", page);
				link.add(createTagLinkLabel(tag));
				if (!isTagSelected(item)) {
					link.setParameter("tag", tag.getTagname());
				}
				return link;
			}

			private boolean isTagSelected(Item<T> item) {
				return item.getModelObject().equals(TagCloudBoxPanel.this.selectedTag);
			}

			private Label createTagLinkLabel(T tag) {
				return new Label("tagLabel", tag.getTagname());
			}
		};
		return dataView;
	}

	private void setSelectedTag() {
		if (params != null && params.containsKey("tag")) {
			selectedTag = tagService.findById(params.getString("tag"));
			tagTarget.setObject(this.selectedTag);
			// add the tag which is not in the top x
			if (selectedTag != null && !tags.contains(selectedTag)) {
				tags.add(selectedTag);
			}
		}
	}

	private void setTags() {
		PortalSession session = (PortalSession) getSession();
		if (session.hasRight(tagService.getRelatedTagRight())) {
			tags = tagService
					.findMostPopularTags(0, configurationService.findAsInteger(TagConstants.CONF_BOX_NUM_TAGS));
		} else {
			tags = tagService.findMostPopularTags(session.getRole(), 0, configurationService
					.findAsInteger(TagConstants.CONF_BOX_NUM_TAGS));
		}
	}

	private HeaderContributor createCSSHeaderContributor() {
		return CSSPackageResource.getHeaderContribution(TagConstants.REF_TAG_CSS);
	}

	public void cleanSelection() {
		selectedTag = null;
	}

	@Override
	public void setTitleVisible(boolean visible) {
		titleContainer.setVisible(visible);
	}
}
