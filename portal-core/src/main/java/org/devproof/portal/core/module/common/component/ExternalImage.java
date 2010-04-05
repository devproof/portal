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
package org.devproof.portal.core.module.common.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.UrlUtils;

/**
 * For external images. If the path doesn't start with a / it corrects the path
 * to the context path
 * 
 * @author Carsten Hufe
 */
public class ExternalImage extends WebComponent {

	private static final long serialVersionUID = 1L;

	public ExternalImage(String id, String imageUrl) {
		super(id);
		String url = UrlUtils.rewriteToContextRelative(imageUrl, getRequest());
		add(new AttributeModifier("src", true, Model.of(url)));
		setVisible(!(url == null || "".equals(url)));
	}

	public ExternalImage(String id, ResourceReference imageResource) {
		super(id);
		String url = getRequestCycle().urlFor(imageResource).toString();
		add(new AttributeModifier("src", true, Model.of(url)));
		setVisible(!(url == null || "".equals(url)));
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		checkComponentTag(tag, "img");
	}

}