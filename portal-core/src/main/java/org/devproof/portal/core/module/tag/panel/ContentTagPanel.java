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

	public ContentTagPanel(final String id, final IModel<List<T>> tagModel, final Class<? extends Page> page,
			final PageParameters params) {
		super(id);
		add(CSSPackageResource.getHeaderContribution(TagConstants.REF_TAG_CSS));
		String selectedTag = null;
		if (params != null && params.containsKey("tag")) {
			selectedTag = params.getString("tag");
		}
		final RepeatingView repeating = new RepeatingView("repeating");
		add(repeating);

		for (final T tag : tagModel.getObject()) {
			final WebMarkupContainer item = new WebMarkupContainer(repeating.newChildId());
			repeating.add(item);
			final BookmarkablePageLink<String> link = new BookmarkablePageLink<String>("tagLink", page);
			link.add(new Label("tagName", tag.getTagname()));
			if (tag.getTagname().equals(selectedTag)) {
				item.add(new SimpleAttributeModifier("class", "tagViewSelected"));
			} else {
				link.setParameter("tag", tag.getTagname());
			}
			item.add(link);
		}
	}
}
