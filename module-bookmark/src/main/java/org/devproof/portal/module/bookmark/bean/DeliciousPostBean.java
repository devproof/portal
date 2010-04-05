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
package org.devproof.portal.module.bookmark.bean;

import java.io.Serializable;

/**
 * Represents a "post" line from delicous, that means one bookmark
 *
 * @author Carsten Hufe
 */
public class DeliciousPostBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String href;
    private String hash;
    private String description;
    private String tag;
    private String time;
    private String extended;
    private String meta;

    public String getHref() {
        return href;
    }

    public String getHash() {
        return hash;
    }

    public String getDescription() {
        return description;
    }

    public String getTag() {
        return tag;
    }

    public String getTime() {
        return time;
    }

    public String getExtended() {
        return extended;
    }

    public String getMeta() {
        return meta;
    }
}
