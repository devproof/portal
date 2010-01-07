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
package org.devproof.portal.module.comment.config;

/**
 * @author Carsten Hufe
 */
public class CommentConfiguration {
	private String moduleName;
	private String moduleContentId;
	private String readRight;
	private String writeRight;
	
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getModuleContentId() {
		return moduleContentId;
	}
	public void setModuleContentId(String moduleContentId) {
		this.moduleContentId = moduleContentId;
	}
	public String getReadRight() {
		return readRight;
	}
	public void setReadRight(String readRight) {
		this.readRight = readRight;
	}
	public String getWriteRight() {
		return writeRight;
	}
	public void setWriteRight(String writeRight) {
		this.writeRight = writeRight;
	}
}
