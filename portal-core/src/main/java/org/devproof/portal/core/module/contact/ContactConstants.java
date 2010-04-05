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
package org.devproof.portal.core.module.contact;

import org.apache.wicket.ResourceReference;
import org.devproof.portal.core.module.contact.page.ContactPage;

/**
 * @author Carsten Hufe
 */
public interface ContactConstants {

    String CONF_CONTACTFORM_EMAIL = "spring.emailTemplateDao.findAll.subject.id.contactformemail";
    ResourceReference REF_CONTACT_CSS = new ResourceReference(ContactConstants.class, "css/contact.css");
    String CONTACT_RIGHT = "page." + ContactPage.class.getSimpleName();
}
