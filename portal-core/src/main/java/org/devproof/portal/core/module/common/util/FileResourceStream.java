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
package org.devproof.portal.core.module.common.util;

import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.time.Time;

import java.io.*;
import java.util.Locale;

/**
 * For downloading a file
 * 
 * @author Carsten Hufe
 */
public class FileResourceStream implements IResourceStream {

	private static final long serialVersionUID = 1L;
	private FileInputStream fis;
	private File file;

	public FileResourceStream(File file) throws FileNotFoundException {
		this.file = file;
		fis = new FileInputStream(file);
	}

	public void close() throws IOException {
		fis.close();
	}

	public String getContentType() {
		return "application/octet-stream";
	}

	public InputStream getInputStream() throws ResourceStreamNotFoundException {
		return fis;
	}

	public Locale getLocale() {
		return null;
	}

	public long length() {
		return file.length();
	}

	public void setLocale(Locale locale) {
	}

	public Time lastModifiedTime() {
		return Time.milliseconds(file.lastModified());
	}

}
