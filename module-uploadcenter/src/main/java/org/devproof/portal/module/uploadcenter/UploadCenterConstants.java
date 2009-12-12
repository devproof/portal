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
package org.devproof.portal.module.uploadcenter;

import org.apache.wicket.ResourceReference;

/**
 * @author Carsten Hufe
 */
public class UploadCenterConstants {
	private UploadCenterConstants() {
	}

	public static final String CONF_UPLOADCENTER_FOLDER = "uploadcenter_folder";
	public static final String CONF_UPLOADCENTER_MAXFILES = "uploadcenter_maxfiles";
	public static final String CONF_UPLOADCENTER_MAXSIZE = "uploadcenter_maxsize";

	public static final ResourceReference REF_GALLERY_IMG = new ResourceReference(UploadCenterConstants.class,
			"img/gallerylink.gif");
	public static final ResourceReference REF_DOWNLOAD_IMG = new ResourceReference(UploadCenterConstants.class,
			"img/download.gif");
}
