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
package org.devproof.portal.core.module.common.component.richtext;

import java.util.Map;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.util.MapModel;
import org.apache.wicket.util.collections.MiniMap;
import org.apache.wicket.util.template.TextTemplateHeaderContributor;
import org.devproof.portal.core.module.common.util.PortalUtil;

/**
 * @author Carsten Hufe
 */
public class EmailRichTextArea extends TextArea<String> {
	private static final long serialVersionUID = 1L;
	private static final ResourceReference REF_EMAIL_CSS = new ResourceReference(EmailRichTextArea.class,
			"css/email.css");

	public EmailRichTextArea(String id) {
		super(id);
		add(createCKEditorResource());
		add(createCKEditorConfiguration());
		setOutputMarkupId(true);
	}

	private TextTemplateHeaderContributor createCKEditorConfiguration() {
		Map<String, Object> variables = new MiniMap<String, Object>(2);
		variables.put("emailCss", PortalUtil.toUrl(REF_EMAIL_CSS, getRequest()));
		variables.put("markupId", getMarkupId());
		return TextTemplateHeaderContributor.forJavaScript(RichTextArea.class, "EmailRichTextArea.js",
				new MapModel<String, Object>(variables));
	}

	private HeaderContributor createCKEditorResource() {
		return JavascriptPackageResource.getHeaderContribution(RichTextArea.class, "ckeditor/ckeditor.js");
	}
}
