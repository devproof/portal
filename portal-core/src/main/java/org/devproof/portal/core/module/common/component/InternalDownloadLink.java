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
package org.devproof.portal.core.module.common.component;

import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.time.Time;

import java.io.*;
import java.util.Locale;

/**
 * Internal download link with file parameter
 *
 * @author Carsten Hufe
 */
public abstract class InternalDownloadLink extends Link {
    private static final long serialVersionUID = 1L;

    public InternalDownloadLink(String id) {
        super(id);
    }

    @Override
    public void onClick() {
        try {
            File file = getFile();
            FileInputStream fis = new FileInputStream(file);
            getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(createFileResourceStream(file, fis))
                    .setFileName(file.getName())
                    .setContentDisposition(ContentDisposition.ATTACHMENT));
        } catch (FileNotFoundException e) {
            throw new UnhandledException(e);
        }
    }

    private IResourceStream createFileResourceStream(final File file, final FileInputStream fis) {
        return new IResourceStream() {
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

            @Override
            public String getStyle() {
                return null;
            }

            @Override
            public void setStyle(String style) {
            }

            @Override
            public String getVariation() {
                return null;
            }

            @Override
            public void setVariation(String variation) {
            }

            @Override
            public Bytes length() {
                return Bytes.bytes(file.length());
            }

            public void setLocale(Locale locale) {
            }

            public Time lastModifiedTime() {
                return Time.millis(file.lastModified());
            }
        };
    }

    /**
     * @return must return the file which you want to download
     */
    protected abstract File getFile();

}
