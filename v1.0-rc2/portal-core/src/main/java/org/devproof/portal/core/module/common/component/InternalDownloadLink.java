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
package org.devproof.portal.core.module.common.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.time.Time;

/**
 * Internal download link with file parameter
 * 
 * @author Carsten Hufe
 */
public abstract class InternalDownloadLink extends StatelessLink {
	private static final long serialVersionUID = 1L;

	public InternalDownloadLink(final String id) {
		super(id);
	}

	@Override
	public void onClick() {

		try {
			final File file = getFile();
			final FileInputStream fis = new FileInputStream(file);

			IResourceStream resourceStream = new IResourceStream() {
				private static final long serialVersionUID = 1L;

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

				public void setLocale(final Locale locale) {
				}

				public Time lastModifiedTime() {
					return Time.milliseconds(file.lastModified());
				}
			};
			getRequestCycle().setRequestTarget(new ResourceStreamRequestTarget(resourceStream) {
				@Override
				public String getFileName() {
					return (file.getName());
				}
			});
		} catch (FileNotFoundException e) {
			throw new UnhandledException(e);
		}
	}

	/**
	 * @return must return the file which you want to download
	 */
	protected abstract File getFile();

}
