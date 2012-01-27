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
package org.devproof.portal.module.otherpage;


import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * @author Carsten Hufe
 */
public interface OtherPageConstants {
    String ENTITY_CACHE_REGION = "entity.content";
    String QUERY_CACHE_REGION = "query.content";
    String AUTHOR_RIGHT = "otherPage.author";
    String HANDLER_KEY = "otherPage";
    ResourceReference REF_OTHERPAGE_CSS = new CssResourceReference(OtherPageConstants.class, "css/otherpage.css");
}
