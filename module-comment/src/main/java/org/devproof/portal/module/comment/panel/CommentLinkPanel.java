/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devproof.portal.module.comment.panel;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.module.comment.CommentConstants;
import org.devproof.portal.module.comment.config.CommentConfiguration;
import org.devproof.portal.module.comment.service.CommentService;

/**
 * @author Carsten Hufe
 */
public abstract class CommentLinkPanel extends Panel {
    private static final long serialVersionUID = 688295761944565000L;
    @SpringBean(name = "commentService")
    private CommentService commentService;
    private CommentConfiguration configuration;
    private WebMarkupContainer commentLink;

    public CommentLinkPanel(String id, CommentConfiguration configuration) {
        super(id);
        this.configuration = configuration;
        addJQuery();
        add(createCSSHeaderContributor());
        add(createCommentLink());
    }

    private Component createCommentLink() {
        commentLink = newCommentLink();
        commentLink.add(createCommentsLinkLabel());
        commentLink.setOutputMarkupId(true);
        return commentLink;
    }

    private Label createCommentsLinkLabel() {
        return new Label("commentsLinkLabel", createLinkLabelTextModel());
    }

    private AjaxLink<Void> newCommentLink() {
        return new AjaxLink<Void>("commentsLink") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                target.addComponent(commentLink);
                CommentLinkPanel.this.onClick(target);
            }
        };
    }

    private IModel<String> createLinkLabelTextModel() {
        return new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                if (isCommentPanelVisible()) {
                    return getString("hideComments");
                } else {
                    long numberOfComments = commentService.findNumberOfComments(configuration.getModuleName(), configuration.getModuleContentId());
                    return numberOfComments == 0 && configuration.isAllowedToWrite() ? getString("writeComment") : getString("numberOfComments", Model.of(numberOfComments));
                }
            }
        };
    }

    private void addJQuery() {
        PortalUtil.addJQuery(this);
    }

    private HeaderContributor createCSSHeaderContributor() {
        return CSSPackageResource.getHeaderContribution(CommentConstants.class, "css/comment.css");
    }

    @Override
    public boolean isVisible() {
        return configuration.isAllowedToView();
    }

    /**
     * Called to check if the comment panel is visible, required for the label text
     * @return true if visible
     */
    protected abstract boolean isCommentPanelVisible();

    /**
     * Called when the linked was clicked
     * @param target ajax request target
     */
    protected abstract void onClick(AjaxRequestTarget target);
}
