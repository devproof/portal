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
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.devproof.portal.core.module.tag.TagConstants;
import org.devproof.portal.core.module.tag.entity.BaseTagEntity;

/**
 * For displaying the tags and mark if one is selected
 * 
 * @author Carsten Hufe
 * 
 */
public class ContentTagPanel<T extends BaseTagEntity<?>> extends Panel {
	private static final long serialVersionUID = 1L;

	private PageParameters params;
	private IModel<List<T>> tagModel;
	private Class<? extends Page> page;
	private String selectedTag;

	public ContentTagPanel(String id, IModel<List<T>> tagModel, Class<? extends Page> page, PageParameters params) {
		super(id);
		this.tagModel = tagModel;
		this.page = page;
		this.params = params;
		setSelectedTag();
		add(createCSSHeaderContributor());
		add(createTagRepeater());
	}

	private RepeatingView createTagRepeater() {
		RepeatingView repeating = new RepeatingView("repeating");
		for (T tag : tagModel.getObject()) {
			repeating.add(createTagItem(repeating.newChildId(), tag));
		}
		return repeating;
	}

	private WebMarkupContainer createTagItem(String id, T tag) {
		WebMarkupContainer item = new WebMarkupContainer(id);
		if (isTagSelected(tag)) {
			item.add(createClassSelectedModifier());
		}
		item.add(createTagLink(tag));
		return item;
	}

	private boolean isTagSelected(T tag) {
		return tag.getTagname().equals(selectedTag);
	}

	private SimpleAttributeModifier createClassSelectedModifier() {
		return new SimpleAttributeModifier("class", "tagViewSelected");
	}

	private BookmarkablePageLink<String> createTagLink(T tag) {
		BookmarkablePageLink<String> link = new BookmarkablePageLink<String>("tagLink", page);
		link.add(createTagLinkLabel(tag));
		if (!isTagSelected(tag)) {
			link.setParameter("tag", tag.getTagname());
		}
		return link;
	}

	private Label createTagLinkLabel(T tag) {
		return new Label("tagName", tag.getTagname());
	}

	private void setSelectedTag() {
		if (params != null && params.containsKey("tag")) {
			selectedTag = params.getString("tag");
		}
	}

	private HeaderContributor createCSSHeaderContributor() {
		return CSSPackageResource.getHeaderContribution(TagConstants.REF_TAG_CSS);
	}
}
