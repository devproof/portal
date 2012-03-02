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
package org.devproof.portal.module.comment.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.module.comment.CommentConstants;
import org.devproof.portal.module.comment.config.CommentConfiguration;
import org.devproof.portal.module.comment.service.CommentService;

/**
 * @author Carsten Hufe
 */
public class ExpandableCommentPanel extends Panel {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "commentService")
    private CommentService commentService;
    private WebMarkupContainer refreshContainer;
    private boolean visible = false;
    private CommentConfiguration configuration;

    public ExpandableCommentPanel(String id, CommentConfiguration configuration) {
        super(id);
        this.configuration = configuration;
        add(createRefreshCommentContainer());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(new CssResourceReference(CommentConstants.class, "css/comment.css"));
        PortalUtil.addJQuery(response);
    }

    private CommentPanel createCommentPanel() {
        return new CommentPanel("comments", configuration);
    }

    public void show(AjaxRequestTarget target) {
        refreshContainer.replace(createCommentPanel());
        target.add(refreshContainer);
        target.appendJavaScript("$(\"#" + refreshContainer.getMarkupId() + "\").slideDown(\"normal\");");
    }

    public void hide(AjaxRequestTarget target) {
        target.appendJavaScript("$(\"#" + refreshContainer.getMarkupId() + "\").slideUp(\"normal\");");
    }

    public void toggle(AjaxRequestTarget target) {
        if (visible) {
            hide(target);
        } else {
            show(target);
        }
        visible = !visible;
    }

    public boolean isCommentsVisible() {
        return visible && isVisible();
    }

    @Override
    public boolean isVisible() {
        return configuration.isAllowedToView();
    }

    private WebMarkupContainer createRefreshCommentContainer() {
        refreshContainer = new WebMarkupContainer("refreshCommentContainer");
        refreshContainer.add(createEmptyCommentPanel());
        refreshContainer.add(createDisplayNoneModifier());
        refreshContainer.setOutputMarkupId(true);
        return refreshContainer;
    }

    private EmptyPanel createEmptyCommentPanel() {
        return new EmptyPanel("comments");
    }

    private AttributeModifier createDisplayNoneModifier() {
        return AttributeModifier.replace("style", "display:none;");
    }
}
