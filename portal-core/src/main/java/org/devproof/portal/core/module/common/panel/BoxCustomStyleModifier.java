/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devproof.portal.core.module.common.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.devproof.portal.core.module.box.entity.Box;

/**
 * Appends a custom style to a box
 *
 * @author Carsten Hufe
 */
public class BoxCustomStyleModifier extends AttributeAppender {
    public BoxCustomStyleModifier(IModel<Box> boxModel) {
        super("class", createAppendModel(boxModel), ";");
    }
    private static IModel<?> createAppendModel(IModel<Box> boxModel) {
        return new PropertyModel<String>(boxModel, "customStyle");
    }
}
