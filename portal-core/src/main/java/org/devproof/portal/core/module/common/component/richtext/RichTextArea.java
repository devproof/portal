/*
 * Copyright 2009 Carsten Hufe devproof.org
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

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.MapModel;
import org.apache.wicket.util.collections.MiniMap;
import org.apache.wicket.util.template.TextTemplateHeaderContributor;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.util.PortalUtil;

/**
 * @author Carsten Hufe
 */
public class RichTextArea extends TextArea<String> {
	private static final long serialVersionUID = 1L;

	public RichTextArea(final String id) {
		super(id);
		init();
	}

	public RichTextArea(final String id, final IModel<String> model) {
		super(id, model);
		init();
	}

	private void init() {
		this.add(JavascriptPackageResource.getHeaderContribution(RichTextArea.class, "tinymce/tiny_mce.js"));
		Map<String, Object> variables = new MiniMap<String, Object>(3);
		variables.put("defaultCss", PortalUtil.toUrl(CommonConstants.REF_DEFAULT_CSS, getRequest()));
		variables.put("iconcodeImg", PortalUtil.toUrl(CommonConstants.REF_ICONCODE_IMG, getRequest()));
		variables.put("string2imgImg", PortalUtil.toUrl(CommonConstants.REF_STRING2IMG_IMG, getRequest()));
		this.add(TextTemplateHeaderContributor.forJavaScript(RichTextArea.class, "RichTextArea.js", new MapModel<String, Object>(variables)));
		this.add(new SimpleAttributeModifier("class", "mceRichTextArea"));
	}
}
