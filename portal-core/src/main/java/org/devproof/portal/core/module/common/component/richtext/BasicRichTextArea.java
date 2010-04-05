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
package org.devproof.portal.core.module.common.component.richtext;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.util.MapModel;
import org.apache.wicket.util.collections.MiniMap;
import org.apache.wicket.util.template.TextTemplateHeaderContributor;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.util.PortalUtil;

import java.util.Map;

/**
 * @author Carsten Hufe
 */
public class BasicRichTextArea extends TextArea<String> {
    private static final long serialVersionUID = 1L;
    private static final ResourceReference REF_BASE_CSS = new ResourceReference(BasicRichTextArea.class, "css/base.css");
    private boolean baseStyle;

    public BasicRichTextArea(String id) {
        this(id, false);
    }

    public BasicRichTextArea(String id, boolean baseStyle) {
        super(id);
        this.baseStyle = baseStyle;
        add(createCKEditorResource());
        setOutputMarkupId(true);
    }

    private HeaderContributor createCKEditorResource() {
        return JavascriptPackageResource.getHeaderContribution(FullRichTextArea.class, "ckeditor/ckeditor.js");
    }

    @Override
    protected void onRender(MarkupStream markupStream) {
        super.onRender(markupStream);
        Map<String, Object> variables = new MiniMap<String, Object>(2);
        variables.put("defaultCss", PortalUtil.toUrl(baseStyle ? REF_BASE_CSS : CommonConstants.REF_DEFAULT_CSS, getRequest()));
        variables.put("markupId", getMarkupId());
        String javascript = TextTemplateHeaderContributor.forJavaScript(FullRichTextArea.class, "BasicRichTextArea.js", new MapModel<String, Object>(variables)).toString();
        getResponse().write(javascript);
    }
}
