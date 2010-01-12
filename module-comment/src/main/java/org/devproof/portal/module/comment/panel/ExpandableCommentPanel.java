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
package org.devproof.portal.module.comment.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.module.comment.CommentConstants;
import org.devproof.portal.module.comment.config.CommentConfiguration;

/**
 * @author Carsten Hufe
 */
public class ExpandableCommentPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private WebMarkupContainer refreshContainer;

	public ExpandableCommentPanel(String id, final CommentConfiguration configuration) {
		super(id);
		add(CSSPackageResource.getHeaderContribution(CommentConstants.class, "css/comment.css"));
		PortalUtil.addJQuery(this);
		refreshContainer = new WebMarkupContainer("refreshCommentContainer");
		refreshContainer.setOutputMarkupId(true);
		refreshContainer.add(new WebMarkupContainer("comments"));
		refreshContainer.add(new SimpleAttributeModifier("style", "display:none;"));
		add(refreshContainer);
		add(new AjaxLink<Void>("showCommentsLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				refreshContainer.replace(new CommentPanel("comments", configuration));
				target.addComponent(refreshContainer);
				target.appendJavascript("$(\"#" + refreshContainer.getMarkupId() + "\").slideDown(\"normal\");");
			}

		});
		add(new AjaxLink<Void>("hideCommentsLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				target.appendJavascript("$(\"#" + refreshContainer.getMarkupId() + "\").slideUp(\"normal\");");
			}
		});
	}
}
