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
package org.devproof.portal.core.module.historization.service;

import java.util.Date;

/**
 * Fields are required to display it on the
 * default {@link org.devproof.portal.core.module.historization.page.AbstractHistoryPage}
 *
 * @author Carsten Hufe
 */
public interface Historized {
    /**
     * @return executed action
     */
    Action getAction();

    /**
     * @return execution time
     */
    Date getActionAt();

    /**
     * @return modification time
     */
    Date getModifiedAt();

    /**
     * @return author, modified by
     */
    String getModifiedBy();

    /**
     * @return version or revision number
     */
    Integer getVersionNumber();

    /**
     * @return when Action.RESTORED is, there must be a restoring version
     */
    Integer getRestoredFromVersion();
}
