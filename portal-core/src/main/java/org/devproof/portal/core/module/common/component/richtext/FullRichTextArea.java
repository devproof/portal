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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.MapModel;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.resource.TextTemplateResourceReference;
import org.apache.wicket.util.collections.MiniMap;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.devproof.portal.core.module.common.CommonConstants;

import java.util.Map;

/**
 * @author Carsten Hufe
 */
public class FullRichTextArea extends TextArea<String> {
    private static final long serialVersionUID = 1L;

    public FullRichTextArea(String id) {
        this(id, null);
    }

    public FullRichTextArea(String id, IModel<String> model) {
        super(id, model);
        setOutputMarkupId(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderJavaScriptReference(new PackageResourceReference(FullRichTextArea.class, "ckeditor/ckeditor.js"));
    }

    @Override
    protected void onRender() {
        super.onRender();
        Map<String, Object> variables = new MiniMap<String, Object>(2);
        String requestPath = urlFor(CommonConstants.REF_DEFAULT_CSS, new PageParameters()).toString();
        variables.put("defaultCss", RequestUtils.toAbsolutePath(requestPath, getPageRelativePath()));
        variables.put("markupId", getMarkupId());
        PackageTextTemplate javascript = new PackageTextTemplate(FullRichTextArea.class, "FullRichTextArea.html");
        getResponse().write(javascript.interpolate(variables).asString());
    }
}
