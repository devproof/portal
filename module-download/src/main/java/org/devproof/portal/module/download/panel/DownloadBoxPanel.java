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
package org.devproof.portal.module.download.panel;

import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.box.panel.BoxTitleVisibility;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.module.download.DownloadConstants;
import org.devproof.portal.module.download.entity.DownloadEntity;
import org.devproof.portal.module.download.page.DownloadPage;
import org.devproof.portal.module.download.service.DownloadService;

/**
 * @author Carsten Hufe
 */
public class DownloadBoxPanel extends Panel implements BoxTitleVisibility {

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "downloadService")
	private DownloadService downloadService;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;
	private WebMarkupContainer titleContainer;

	public DownloadBoxPanel(final String id) {
		super(id);
		PortalSession session = (PortalSession) getSession();
		Integer num = configurationService.findAsInteger(DownloadConstants.CONF_BOX_NUM_LATEST_DOWNLOADS);
		List<DownloadEntity> downloads = downloadService.findAllDownloadsForRoleOrderedByDateDesc(session.getRole(), 0,
				num);

		RepeatingView repeating = new RepeatingView("repeating");
		for (DownloadEntity download : downloads) {
			WebMarkupContainer item = new WebMarkupContainer(repeating.newChildId());
			repeating.add(item);
			BookmarkablePageLink<DownloadPage> link = new BookmarkablePageLink<DownloadPage>("link", DownloadPage.class);
			link.setParameter("id", download.getId());
			link.add(new Label("linkName", download.getTitle()));
			item.add(link);
		}
		add(titleContainer = new WebMarkupContainer("title"));
		add(repeating);
		setVisible(downloads.size() > 0);
	}

	@Override
	public void setTitleVisible(final boolean visible) {
		titleContainer.setVisible(visible);
	}
}
