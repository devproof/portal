/*
 * Copyright 2009 Carsten Hufe devproof.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.devproof.portal.module.bookmark.bean;

import java.io.Serializable;

/**
 * Represents a "post" line from delicous, that means one bookmark
 * 
 * @author Carsten Hufe
 * 
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
		return this.href;
	}

	public String getHash() {
		return this.hash;
	}

	public String getDescription() {
		return this.description;
	}

	public String getTag() {
		return this.tag;
	}

	public String getTime() {
		return this.time;
	}

	public String getExtended() {
		return this.extended;
	}

	public String getMeta() {
		return this.meta;
	}
}
