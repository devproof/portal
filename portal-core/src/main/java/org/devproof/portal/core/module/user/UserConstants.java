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
package org.devproof.portal.core.module.user;

/**
 * @author Carsten Hufe
 */
public interface UserConstants {
    String UNKNOWN_USERNAME = "unknown";
    String CONF_REGISTRATION_EMAIL = "spring.emailTemplateRepository.findAll.subject.id.regemail";
    String CONF_RECONFIRMATION_EMAIL = "spring.emailTemplateRepository.findAll.subject.id.reconfirmemail";
    String CONF_PASSWORDFORGOT_EMAIL = "spring.emailTemplateRepository.findAll.subject.id.forgotemail";
    String CONF_NOTIFY_USER_REGISTRATION = "spring.emailTemplateRepository.findAll.subject.id.registereduser";
    String CONF_EMAIL_VALIDATION = "email_validation";
    String CONF_REGISTRATION_REQUIRED_NAME = "registration_required_name";
    String CONF_REGISTRATION_REQUIRED_BIRTHDAY = "registration_required_birthday";
    String ENTITY_CACHE_REGION = "entity.user";
    String QUERY_CACHE_REGION = "query.user";
}
