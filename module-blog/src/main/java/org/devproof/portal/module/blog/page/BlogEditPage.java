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
package org.devproof.portal.module.blog.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.config.Secured;
import org.devproof.portal.core.module.common.component.richtext.FullRichTextArea;
import org.devproof.portal.core.module.mount.panel.MountInputPanel;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.core.module.right.panel.RightGridPanel;
import org.devproof.portal.core.module.tag.component.TagField;
import org.devproof.portal.module.blog.BlogConstants;
import org.devproof.portal.module.blog.entity.Blog;
import org.devproof.portal.module.blog.entity.BlogTag;
import org.devproof.portal.module.blog.service.BlogService;
import org.devproof.portal.module.blog.service.BlogTagService;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@Secured(BlogConstants.AUTHOR_RIGHT)
public class BlogEditPage extends BlogBasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "blogService")
    private BlogService blogService;
    @SpringBean(name = "blogTagService")
    private BlogTagService blogTagService;
    private IModel<Blog> blogModel;
    private MountInputPanel mountInputPanel;

    public BlogEditPage(IModel<Blog> blogModel) {
        super(new PageParameters());
        this.blogModel = blogModel;
        add(createBlogEditForm());
    }

    private Form<Blog> createBlogEditForm() {
        Form<Blog> form = newBlogEditForm();
        form.add(createHeadlineField());
        form.add(createContentField());
        form.add(createTagField());
        form.add(createMountInputPanel());
        form.add(createViewRightPanel());
        form.add(createCommentRightPanel());
        form.setOutputMarkupId(true);
        return form;
    }

    private Form<Blog> newBlogEditForm() {
        return new Form<Blog>("form", new CompoundPropertyModel<Blog>(blogModel)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                BlogEditPage.this.setVisible(false);
                Blog blog = getModelObject();
                blogService.save(blog);
                mountInputPanel.storeMountPoints();
                setRedirect(false);
                setResponsePage(BlogPage.class, new PageParameters("id=" + blog.getId()));
                info(getString("msg.saved"));
            }
        };
    }

    private MountInputPanel createMountInputPanel() {
        mountInputPanel = new MountInputPanel("mountUrls", BlogConstants.HANDLER_KEY, createBlogIdModel());
        return mountInputPanel;
    }

    private IModel<String> createBlogIdModel() {
        return new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = 1340993990243817302L;

            @Override
            public String getObject() {
                Integer id = blogModel.getObject().getId();
                if(id != null) {
                    return id.toString();
                }
                return null;
            }
        };
    }

    private RightGridPanel createViewRightPanel() {
        IModel<List<Right>> allRightsModel = new PropertyModel<List<Right>>(blogModel, "allRights");
        return new RightGridPanel("viewright", "blog.view", allRightsModel);
    }

    private RightGridPanel createCommentRightPanel() {
        IModel<List<Right>> allRightsModel = new PropertyModel<List<Right>>(blogModel, "allRights");
        return new RightGridPanel("commentright", "blog.comment", allRightsModel);
    }

    private TagField<BlogTag> createTagField() {
        IModel<List<BlogTag>> blogListModel = new PropertyModel<List<BlogTag>>(blogModel, "tags");
        return new TagField<BlogTag>("tags", blogListModel, blogTagService);
    }

    private FormComponent<String> createContentField() {
        return new FullRichTextArea("content");
    }

    private FormComponent<String> createHeadlineField() {
        return new RequiredTextField<String>("headline");
    }
}
