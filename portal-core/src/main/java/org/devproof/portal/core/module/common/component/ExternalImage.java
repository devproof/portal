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
package org.devproof.portal.core.module.common.component;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.string.UrlUtils;

/**
 * For external images. If the path doesn't start with a / it corrects the path
 * to the context path
 *
 * @author Carsten Hufe
 */
public class ExternalImage extends WebComponent {

    private static final long serialVersionUID = 1L;
    private IModel<String> urlModel;

    public ExternalImage(String id, String imageUrl) {
        super(id);
        String url = UrlUtils.rewriteToContextRelative(imageUrl, getRequestCycle());
        urlModel = Model.of(url);
        add(AttributeModifier.replace("src", urlModel));
    }

    public ExternalImage(String id, final ResourceReference imageResource) {
        super(id);
        urlModel = new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return getRequestCycle().urlFor(imageResource, getPage().getPageParameters()).toString();
            }
        };
        add(AttributeModifier.replace("src", urlModel));
    }

    @Override
    public boolean isVisible() {
        return StringUtils.isNotBlank(urlModel.getObject());
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        checkComponentTag(tag, "img");
    }

}