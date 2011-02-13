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
package org.devproof.portal.module.otherpage.page;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.module.otherpage.OtherPageConstants;
import org.devproof.portal.module.otherpage.entity.OtherPage;
import org.devproof.portal.module.otherpage.service.OtherPageService;

/**
 * @author Carsten Hufe
 */
public class OtherPageBasePage extends TemplatePage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "otherPageService")
	private OtherPageService otherPageService;

	public OtherPageBasePage(PageParameters params) {
		super(params);
		addSyntaxHighlighter();
        add(createCSSHeaderContributor());
	}

    private HeaderContributor createCSSHeaderContributor() {
		return CSSPackageResource.getHeaderContribution(OtherPageConstants.REF_OTHERPAGE_CSS);
	}

    @Override
    protected Component newPageAdminBoxLink(String linkMarkupId, String labelMarkupId) {
        if(isAuthor()) {
            return createOtherPageAddLink(linkMarkupId, labelMarkupId);
        }
        return super.newPageAdminBoxLink(linkMarkupId, labelMarkupId);
    }

	private MarkupContainer createOtherPageAddLink(String linkMarkupId, String labelMarkupId) {
		Link<?> link = newOtherPageAddLink(linkMarkupId);
		link.add(createOtherPageAddLinkLabel(labelMarkupId));
		return link;
	}

	private Link<?> newOtherPageAddLink(String linkMarkupId) {
		return new Link<Void>(linkMarkupId) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				OtherPage newOtherPage = otherPageService.newOtherPageEntity();
				IModel<OtherPage> otherPageModel = Model.of(newOtherPage);
				setResponsePage(new OtherPageEditPage(otherPageModel));
			}
		};
	}

	private Label createOtherPageAddLinkLabel(String labelMarkupId) {
		return new Label(labelMarkupId, getString("createLink"));
	}

	public boolean isAuthor() {
		PortalSession session = (PortalSession) getSession();
		return session.hasRight(OtherPageConstants.AUTHOR_RIGHT);
	}
}
