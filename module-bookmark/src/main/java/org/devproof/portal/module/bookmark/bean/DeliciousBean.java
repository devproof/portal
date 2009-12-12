/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
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
		return update;
	}

	public String getTag() {
		return tag;
	}

	public Integer getTotal() {
		return total;
	}

	public List<DeliciousPostBean> getPosts() {
		return posts;
	}

	public void setPosts(final List<DeliciousPostBean> posts) {
		this.posts = posts;
	}

	public boolean add(final DeliciousPostBean e) {
		return posts.add(e);
	}

	public Integer getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(final Integer httpCode) {
		this.httpCode = httpCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getUser() {
		return user;
	}

	public boolean hasError() {
		return httpCode == null || httpCode / 100 != 2;
	}
}
