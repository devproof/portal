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
package org.devproof.portal.core.module.tag;

import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * @author Carsten Hufe
 */
public interface TagConstants {
    String TAG_DEFAULT_SEPERATOR = " ";
    String TAG_SEPERATORS = " ,;";
    String CONF_BOX_NUM_TAGS = "box_num_tags";
    ResourceReference REF_TAG_CSS = new PackageResourceReference(TagConstants.class, "css/tag.css");
    String ENTITY_CACHE_REGION = "entity.content";
    String QUERY_CACHE_REGION = "query.content";
    String TAG_PARAM = "tag";
}
