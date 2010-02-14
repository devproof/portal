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
package org.devproof.portal.module.uploadcenter.bean;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author Carsten Hufe
 */
public class FileBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private File file;
	private String name;
	private String size;
	private String date;

	public FileBean(File file, DateFormat dateFormat) {
		this.file = file;
		date = dateFormat.format(new Date(file.lastModified()));
		if (file.isFile()) {
			size = Long.toString(file.length() / 1024) + " KB";
		} else {
			size = "";
		}
		name = file.getName();
	}

	public File getFile() {
		return file;
	}

	public String getDate() {
		return date;
	}

	public String getName() {
		return name;
	}

	public String getSize() {
		return size;
	}
}