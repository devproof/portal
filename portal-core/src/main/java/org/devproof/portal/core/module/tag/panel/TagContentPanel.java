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
package org.devproof.portal.core.module.tag.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.devproof.portal.core.module.tag.TagConstants;
import org.devproof.portal.core.module.tag.TagUtils;
import org.devproof.portal.core.module.tag.entity.AbstractTag;

import java.util.List;

/**
 * For displaying the tags and mark if one is selected
 *
 * @author Carsten Hufe
 */
public class TagContentPanel<T extends AbstractTag<?>> extends Panel {
    private static final long serialVersionUID = 1L;

    private IModel<List<T>> tagModel;
    private Class<? extends Page> page;

    public TagContentPanel(String id, IModel<List<T>> tagModel, Class<? extends Page> page) {
        super(id);
        this.tagModel = tagModel;
        this.page = page;
        add(createCSSHeaderContributor());
        add(createRepeatingTags());
    }

    private RepeatingView createRepeatingTags() {
        RepeatingView repeating = new RepeatingView("repeatingTags");
        List<T> tags = tagModel.getObject();
        if(tags != null) {
			for (T tag : tags) {
	            repeating.add(createTagItem(repeating.newChildId(), tag));
	        }
        }
        return repeating;
    }

    private WebMarkupContainer createTagItem(String id, T tag) {
        WebMarkupContainer item = new WebMarkupContainer(id);
        item.add(createClassSelectedModifier(tag));
        item.add(createTagLink(tag));
        return item;
    }

    private boolean isTagSelected(T tag) {
        String selectedTag = TagUtils.findSelectedTag();
        return tag.getTagname().equals(selectedTag);
    }

    private AttributeModifier createClassSelectedModifier(T tag) {
        IModel<String> cssStyle = createClassSelectedModifierModel(tag);
        return new AttributeModifier("class", true, cssStyle);
    }

    private IModel<String> createClassSelectedModifierModel(final T tag) {
        return new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                if (isTagSelected(tag)) {
                    return "tagViewSelected";
                }
                return "";
            }
        };
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
        return new Label("tagName", new PropertyModel<String>(tag, "tagname"));
    }

    private HeaderContributor createCSSHeaderContributor() {
        return CSSPackageResource.getHeaderContribution(TagConstants.REF_TAG_CSS);
    }
}
