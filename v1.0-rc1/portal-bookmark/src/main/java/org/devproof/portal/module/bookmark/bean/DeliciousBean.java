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
import java.util.ArrayList;
import java.util.List;

/**
 * The whole data of one request from delicious
 * 
 * @author Carsten Hufe
 */
public class DeliciousBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String update;
	private String user;
	private String tag;
	private Integer total;
	private List<DeliciousPostBean> posts = new ArrayList<DeliciousPostBean>();
	private Integer httpCode;
	private String errorMessage = "";

	public String getUpdate() {
		return this.update;
	}

	public String getTag() {
		return this.tag;
	}

	public Integer getTotal() {
		return this.total;
	}

	public List<DeliciousPostBean> getPosts() {
		return this.posts;
	}

	public void setPosts(final List<DeliciousPostBean> posts) {
		this.posts = posts;
	}

	public boolean add(final DeliciousPostBean e) {
		return this.posts.add(e);
	}

	public Integer getHttpCode() {
		return this.httpCode;
	}

	public void setHttpCode(final Integer httpCode) {
		this.httpCode = httpCode;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getUser() {
		return this.user;
	}

	public boolean hasError() {
		return this.httpCode == null || this.httpCode / 100 != 2;
	}
}
