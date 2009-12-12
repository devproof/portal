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
package org.devproof.portal.module.download.factory;

import org.apache.wicket.Page;
import org.devproof.portal.core.module.common.factory.CommonPageFactory;
import org.devproof.portal.core.module.common.registry.SharedRegistry;
import org.devproof.portal.module.download.entity.DownloadEntity;
import org.devproof.portal.module.download.page.DownloadEditPage;
import org.devproof.portal.module.download.service.DownloadService;
import org.springframework.beans.factory.InitializingBean;

/**
 * Shared resource: This factory is required for the upload center to create
 * downloads directly from the upload center
 * 
 * @author Carsten Hufe
 * 
 */
public class DownloadEditPageFactory implements CommonPageFactory, InitializingBean {
	private SharedRegistry sharedRegistry;
	private DownloadService downloadService;

	@Override
	public Page newInstance(Object... obj) {
		DownloadEntity download = downloadService.newDownloadEntity();
		download.setUrl((String) obj[0]);
		return new DownloadEditPage(download);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		sharedRegistry.registerResource("createDownloadPage", this);
	}

	public void setDownloadService(DownloadService downloadService) {
		this.downloadService = downloadService;
	}

	public void setSharedRegistry(SharedRegistry sharedRegistry) {
		this.sharedRegistry = sharedRegistry;
	}
}
