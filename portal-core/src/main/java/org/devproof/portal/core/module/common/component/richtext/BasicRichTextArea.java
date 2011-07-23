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
package org.devproof.portal.core.module.common.component.richtext;

import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;
import org.apache.wicket.model.util.MapModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.resource.TextTemplateResourceReference;
import org.apache.wicket.util.collections.MiniMap;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.util.PortalUtil;

import java.util.Map;

/**
 * @author Carsten Hufe
 */
public class BasicRichTextArea extends TextArea<String> {
    private static final long serialVersionUID = 1L;
    private static final ResourceReference REF_BASE_CSS = new PackageResourceReference(BasicRichTextArea.class, "css/base.css");
    private boolean baseStyle;

    public BasicRichTextArea(String id) {
        this(id, false);
    }

    public BasicRichTextArea(String id, boolean baseStyle) {
        super(id);
        this.baseStyle = baseStyle;
        setOutputMarkupId(true);
    }


    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderJavaScriptReference(new PackageResourceReference(FullRichTextArea.class, "ckeditor/ckeditor.js"));
    }

    @Override
    protected void onRender() {
        Map<String, Object> variables = new MiniMap<String, Object>(2);
        variables.put("defaultCss", PortalUtil.toUrl(baseStyle ? REF_BASE_CSS : CommonConstants.REF_DEFAULT_CSS));
        variables.put("markupId", getMarkupId());
        TextTemplateResourceReference javascript = new TextTemplateResourceReference(FullRichTextArea.class, "BasicRichTextArea.js", new MapModel<String, Object>(variables));
        getResponse().write(javascript.toString());
    }
}
