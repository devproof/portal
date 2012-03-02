/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.tag.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.devproof.portal.core.module.tag.TagConstants;
import org.devproof.portal.core.module.tag.entity.AbstractTag;

import java.util.List;

/**
 * For displaying the tags and mark if one is selected
 *
 * @author Carsten Hufe
 */
public class TagContentPanel<T extends AbstractTag<?>> extends Panel {
    private static final long serialVersionUID = 1L;

    private IModel<List<T>> tagsModel;
    private Class<? extends Page> page;

    public TagContentPanel(String id, IModel<List<T>> tagsModel, Class<? extends Page> page) {
        super(id);
        this.tagsModel = tagsModel;
        this.page = page;
        add(createRepeatingTags());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(TagConstants.REF_TAG_CSS);
    }

    private Component createRepeatingTags() {
        return new ListView<T>("repeatingTags", tagsModel) {
            private static final long serialVersionUID = 8818475561624333195L;

            @Override
            protected void populateItem(ListItem<T> item) {
                T tag = item.getModelObject();
                item.add(createClassSelectedModifier(tag));
                item.add(createTagLink(tag));
            }
        };
    }


    private boolean isTagSelected(T tag) {
        // TODO getPageParameters wird hier wohl null sein ...
        String selectedTag = getPage().getPageParameters().get(TagConstants.TAG_PARAM).toOptionalString();
        return tag.getTagname().equals(selectedTag);
    }

    private AttributeModifier createClassSelectedModifier(T tag) {
        IModel<String> cssStyle = createClassSelectedModifierModel(tag);
        return AttributeModifier.replace("class", cssStyle);
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
            // TODO geht das noch?
            link.getPageParameters().set("tag", tag.getTagname());
        }
        return link;
    }

    private Label createTagLinkLabel(T tag) {
        return new Label("tagName", new PropertyModel<String>(tag, "tagname"));
    }
}
