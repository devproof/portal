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
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.devproof.portal.module.comment.CommentConstants;

/**
 * @author Carsten Hufe
 */
public class LazyCommentPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private WebMarkupContainer refreshContainer;

	public LazyCommentPanel(String id, final String moduleName, final String moduleContentId) {
		super(id);
		add(JavascriptPackageResource.getHeaderContribution(CommentConstants.class, "css/jquery-1.3.2.min.js"));
		refreshContainer = new WebMarkupContainer("refreshCommentContainer");
		refreshContainer.setMarkupId("refreshCommentContainer");
		refreshContainer.setOutputMarkupId(true);
		refreshContainer.add(new WebMarkupContainer("comments"));
		refreshContainer.add(new SimpleAttributeModifier("style", "display:none;"));
		add(refreshContainer);
		add(new AjaxLink<Void>("showCommentsLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				refreshContainer.replace(new CommentPanel("comments", moduleName, moduleContentId));
				target.addComponent(refreshContainer);
				target.appendJavascript("$(\"#refreshCommentContainer\").slideDown(\"slow\");");
			}

		});
		add(new AjaxLink<Void>("hideCommentsLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				// refreshContainer.replace(new WebMarkupContainer("comments"));
				target.appendJavascript("$(\"#refreshCommentContainer\").slideUp(\"slow\");");
				// target.addComponent(refreshContainer);
			}
		});
	}
}
