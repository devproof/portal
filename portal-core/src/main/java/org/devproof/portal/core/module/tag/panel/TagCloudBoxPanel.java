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
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.box.panel.BoxTitleVisibility;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.tag.TagConstants;
import org.devproof.portal.core.module.tag.TagUtils;
import org.devproof.portal.core.module.tag.entity.BaseTagEntity;
import org.devproof.portal.core.module.tag.service.TagService;

import java.util.List;

/**
 * @author Carsten Hufe
 */
public class TagCloudBoxPanel<T extends BaseTagEntity<?>> extends Panel implements BoxTitleVisibility {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "configurationService")
    private ConfigurationService configurationService;
    private TagService<T> tagService;
    private Class<? extends Page> page;
    private WebMarkupContainer titleContainer;
    private IModel<List<T>> tagsModel;

    public TagCloudBoxPanel(String id, TagService<T> tagService, Class<? extends Page> page) {
        super(id);
        this.tagService = tagService;
        this.page = page;
        this.tagsModel = createTagsModel();
        add(createCSSHeaderContributor());
        add(createRepeatingTags());
        add(createTitleContainer());
    }

    @Override
    public boolean isVisible() {
        return tagsModel.getObject().size() > 0;
    }

    private WebMarkupContainer createTitleContainer() {
        titleContainer = new WebMarkupContainer("title");
        return titleContainer;
    }

    private ListView<T> createRepeatingTags() {
        return new ListView<T>("repeatingTags", tagsModel) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<T> item) {
                item.add(createClassTagSelectionModifier(item.getModelObject()));
                item.add(createTagLink(item));
            }

            private AttributeModifier createClassTagSelectionModifier(T tag) {
                return new AttributeModifier("class", true, createClassSelectedModifierModel(tag));
            }

            private IModel<String> createClassSelectedModifierModel(final T tag) {
                return new AbstractReadOnlyModel<String>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String getObject() {
                        if (isTagSelected(tag)) {
                            return "selsidenav";
                        }
                        return "";
                    }
                };
            }

            private BookmarkablePageLink<Void> createTagLink(ListItem<T> item) {
                T tag = item.getModelObject();
                BookmarkablePageLink<Void> link = new BookmarkablePageLink<Void>("tagLink", page);
                link.add(createTagLinkLabel(tag));
                if (!isTagSelected(item.getModelObject())) {
                    link.setParameter("tag", tag.getTagname());
                }
                return link;
            }

            private Label createTagLinkLabel(T tag) {
                return new Label("tagLabel", tag.getTagname());
            }
        };
    }

    private boolean isTagSelected(T tag) {
        String selectedTag = TagUtils.findSelectedTag();
        return tag.getTagname().equals(selectedTag);
    }

    private T getSelectedTag() {
        PageParameters params = RequestCycle.get().getPageParameters();
        if (params != null && params.containsKey("tag")) {
            return tagService.findById(params.getString("tag"));
        }
        return null;
    }

    private IModel<List<T>> createTagsModel() {
        return new LoadableDetachableModel<List<T>>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected List<T> load() {
                PortalSession session = (PortalSession) getSession();
                final List<T> tags;
                if (session.hasRight(tagService.getRelatedTagRight())) {
                    tags = tagService.findMostPopularTags(0, configurationService.findAsInteger(TagConstants.CONF_BOX_NUM_TAGS));
                } else {
                    tags = tagService.findMostPopularTags(session.getRole(), 0, configurationService.findAsInteger(TagConstants.CONF_BOX_NUM_TAGS));
                }
                T selectedTag = getSelectedTag();
                if (selectedTag != null && !tags.contains(selectedTag)) {
                    tags.add(selectedTag);
                }
                return tags;
            }
        };
    }

    private HeaderContributor createCSSHeaderContributor() {
        return CSSPackageResource.getHeaderContribution(TagConstants.REF_TAG_CSS);
    }

    @Override
    public void setTitleVisible(boolean visible) {
        titleContainer.setVisible(visible);
    }
}
