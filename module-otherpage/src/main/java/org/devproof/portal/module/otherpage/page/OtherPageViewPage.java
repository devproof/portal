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
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.module.otherpage.entity.OtherPage;
import org.devproof.portal.module.otherpage.service.OtherPageService;

/**
 * @author Carsten Hufe
 */
@ModulePage(mountPath = "/other", indexMountedPath = true)
public class OtherPageViewPage extends OtherPageBasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "otherPageService")
    private OtherPageService otherPageService;
    private PageParameters params;
    private IModel<OtherPage> otherPageModel;

    public OtherPageViewPage(PageParameters params) {
        super(params);
        this.params = params;
        this.otherPageModel = createOtherPageModel();
        add(createAuthorContainer());
        add(createContentLabel());
    }

    @Override
    protected void onBeforeRender() {
        redirectToErrorPageIfHasNoRights();
        super.onBeforeRender();
    }

    private IModel<OtherPage> createOtherPageModel() {
        return new LoadableDetachableModel<OtherPage>() {
            private static final long serialVersionUID = 1722157251195970885L;

            @Override
            protected OtherPage load() {
                Integer id = getContentId();
                OtherPage otherPage = otherPageService.findById(id);
                if (otherPage == null) {
                    otherPage = otherPageService.newOtherPageEntity();
                    otherPage.setCreatedAt(PortalUtil.now());
                    otherPage.setModifiedAt(PortalUtil.now());
                    otherPage.setCreatedBy("");
                    otherPage.setModifiedBy("");
                    otherPage.setContent(getString("noContent"));
                }
                return otherPage;
            }
        };
    }

    private WebMarkupContainer createAuthorContainer() {
        WebMarkupContainer authorContainer = newAuthorContainer();
        authorContainer.add(createAppropriateAuthorPanel());
        authorContainer.add(createMetaInfoPanel());
        return authorContainer;
    }

    private WebMarkupContainer newAuthorContainer() {
        return new WebMarkupContainer("authorContainer") {
            private static final long serialVersionUID = -1832624008608526956L;

            @Override
            public boolean isVisible() {
                return isAuthor();
            }
        };
    }

    private Component createContentLabel() {
        IModel<String> contentModel = new PropertyModel<String>(otherPageModel, "content");
        return new ExtendedLabel("content", contentModel);
    }

    private Component createMetaInfoPanel() {
        return new MetaInfoPanel<OtherPage>("metaInfo", otherPageModel) {
            private static final long serialVersionUID = -1832624008608526956L;

            @Override
            public boolean isVisible() {
                OtherPage otherPage = otherPageModel.getObject();
                return otherPage.getId() != null;
            }
        };
    }

    private void redirectToErrorPageIfHasNoRights() {
        OtherPage otherPage = otherPageModel.getObject();
        if (otherPage != null && hasRightToViewOtherPage(otherPage)) {
            throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(getString("missing.right"), getRequestURL()));
        }
    }

    private WebMarkupContainer createAppropriateAuthorPanel() {
        if (isAuthor()) {
            return createAuthorPanel();
        } else {
            return createHiddenAuthorPanel();
        }
    }

    private WebMarkupContainer createHiddenAuthorPanel() {
        WebMarkupContainer authorPanel = new WebMarkupContainer("authorButtons");
        authorPanel.setVisible(false);
        return authorPanel;
    }

    private AuthorPanel<OtherPage> createAuthorPanel() {
        AuthorPanel<OtherPage> authorPanel = newAuthorPanel();
        authorPanel.setRedirectPage(OtherPagePage.class, new PageParameters("infoMsg=" + getString("msg.deleted")));
        return authorPanel;
    }

    private AuthorPanel<OtherPage> newAuthorPanel() {
        return new AuthorPanel<OtherPage>("authorButtons", otherPageModel) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onDelete(AjaxRequestTarget target) {
                otherPageService.delete(otherPageModel.getObject());
            }

            @Override
            public void onEdit(AjaxRequestTarget target) {
                setResponsePage(new OtherPageEditPage(otherPageModel));
            }

            @Override
            protected MarkupContainer newHistorizationLink(String markupId) {
                return new BookmarkablePageLink<OtherPageHistoryPage>(markupId, OtherPageHistoryPage.class) {
                    private static final long serialVersionUID = 1918205848493398092L;

                    @Override
                    public PageParameters getPageParameters() {
                        PageParameters params = new PageParameters();
                        params.put("id", otherPageModel.getObject().getId());
                        return params;
                    }

                    @Override
                    public boolean isVisible() {
                        return isDeleteButtonVisible();
                    }
                };
            }

            @Override
            public boolean isDeleteButtonVisible() {
                OtherPage otherPage = otherPageModel.getObject();
                return otherPage.getId() != null;
            }
        };
    }

    private boolean hasRightToViewOtherPage(OtherPage page) {
        PortalSession session = (PortalSession) getSession();
        return !session.hasRight("otherPage.view") && !session.hasRight(page.getViewRights());
    }

    private Integer getContentId() {
        String id = params.getString("0");
        return Integer.valueOf(id);
    }
}
