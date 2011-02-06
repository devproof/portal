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
package org.devproof.portal.module.otherpage.service;

import org.devproof.portal.core.module.common.service.CrudService;
import org.devproof.portal.module.otherpage.entity.OtherPage;
import org.devproof.portal.module.otherpage.entity.OtherPageHistorized;

/**
 * @author Carsten Hufe
 */
public interface OtherPageService extends CrudService<OtherPage, Integer> {
    /**
     * Restores an other page from history
     *
     * @param historized historied other page
     */
    void restoreFromHistory(OtherPageHistorized historized);

    /**
     * Returns a new instance of {@link org.devproof.portal.module.otherpage.entity.OtherPage}
     *
     * @return new instance of {@link org.devproof.portal.module.otherpage.entity.OtherPage}
     */
    OtherPage newOtherPageEntity();

    /**
     * Tests if a content id does exist
     *
     * @param contentId
     * @return true if the content-id existss
     */
    boolean existsContentId(String contentId);

    /**
     * finds the {@link org.devproof.portal.module.otherpage.entity.OtherPage} by the contend id
     *
     * @param contentId content id
     * @return other page
     */
    OtherPage findOtherPageByContentId(String contentId);
}
