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
package org.devproof.portal.module.download.page;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.util.FileResourceStream;
import org.devproof.portal.module.download.entity.DownloadEntity;
import org.devproof.portal.module.download.service.DownloadService;

/**
 * @author Carsten Hufe
 */
public class DownloadRedirectPage extends WebPage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "downloadService")
	private transient DownloadService downloadService;

	public DownloadRedirectPage(final PageParameters params) {
		super(params);
		PortalSession session = (PortalSession) getSession();
		if (params.containsKey("0")) {
			DownloadEntity downloadEntity = this.downloadService.findById(params.getAsInteger("0", 0));
			if (downloadEntity != null && session.hasRight("download.download", downloadEntity.getDownloadRights())) {
				this.downloadService.incrementHits(downloadEntity);
				if (downloadEntity.getUrl().startsWith("file:/")) {
					try {
						URI uri = new URI(downloadEntity.getUrl());
						final File downloadFile = new File(uri);
						if (downloadFile.canRead()) {
							getRequestCycle().setRequestTarget(new ResourceStreamRequestTarget(new FileResourceStream(downloadFile)) {
								@Override
								public String getFileName() {
									return downloadFile.getName();
								}
							});
						}
					} catch (URISyntaxException e) {
						// do nothing
					} catch (FileNotFoundException e) {
						// do nothing
					}
				} else {
					getRequestCycle().setRequestTarget(new RedirectRequestTarget(downloadEntity.getUrl()));
				}
			}
		}
	}
}
