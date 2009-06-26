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
package org.devproof.portal.core.module.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.time.Time;

/**
 * For downloading a file
 * 
 * @author Carsten Hufe
 */
public class FileResourceStream implements IResourceStream {

	private static final long serialVersionUID = 1L;
	private transient final FileInputStream fis;
	private final File file;

	public FileResourceStream(final File file) throws FileNotFoundException {
		this.file = file;
		this.fis = new FileInputStream(file);
	}

	public void close() throws IOException {
		this.fis.close();
	}

	public String getContentType() {
		return "application/octet-stream";
	}

	public InputStream getInputStream() throws ResourceStreamNotFoundException {
		return this.fis;
	}

	public Locale getLocale() {
		return null;
	}

	public long length() {
		return this.file.length();
	}

	public void setLocale(final Locale locale) {
	}

	public Time lastModifiedTime() {
		return Time.milliseconds(this.file.lastModified());
	}

}
