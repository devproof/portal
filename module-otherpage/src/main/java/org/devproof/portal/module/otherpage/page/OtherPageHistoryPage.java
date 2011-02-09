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
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.config.Secured;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.historization.page.AbstractHistoryPage;
import org.devproof.portal.module.otherpage.OtherPageConstants;
import org.devproof.portal.module.otherpage.entity.OtherPage;
import org.devproof.portal.module.otherpage.entity.OtherPageHistorized;
import org.devproof.portal.module.otherpage.panel.OtherPagePrintPanel;
import org.devproof.portal.module.otherpage.query.OtherPageHistoryQuery;
import org.devproof.portal.module.otherpage.service.OtherPageService;

import java.text.SimpleDateFormat;

/**
 * @author Carsten Hufe
 */
@Secured(OtherPageConstants.AUTHOR_RIGHT)
@ModulePage(mountPath = "/admin/other/history")
public class OtherPageHistoryPage extends AbstractHistoryPage<OtherPageHistorized> {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "otherPageService")
    private OtherPageService otherPageService;
    @SpringBean(name = "otherPageHistoryDataProvider")
    private QueryDataProvider<OtherPageHistorized, OtherPageHistoryQuery> otherPageHistoryDataProvider;
    @SpringBean(name = "displayDateFormat")
    private SimpleDateFormat dateFormat;
    private IModel<OtherPageHistoryQuery> queryModel;
    private IModel<OtherPage> otherPageModel;
    private PageParameters params;

    public OtherPageHistoryPage(PageParameters params) {
        super(params);
        this.params = params;
        this.queryModel = otherPageHistoryDataProvider.getSearchQueryModel();
    }

    private IModel<OtherPage> getOtherPageModel() {
        if(otherPageModel == null) {
            otherPageModel = createOtherPageModel();
        }
        return otherPageModel;
    }

    private LoadableDetachableModel<OtherPage> createOtherPageModel() {
        return new LoadableDetachableModel<OtherPage>() {
            private static final long serialVersionUID = -4042346265134003874L;

            @Override
            protected OtherPage load() {
                return otherPageService.findById(params.getAsInteger("id"));
            }
        };
    }

    @Override
    protected void onBeforeRender() {
        this.queryModel.getObject().setOtherPage(getOtherPageModel().getObject());
        super.onBeforeRender();
    }

    @Override
    protected IModel<String> newHeadlineModel() {
        return new StringResourceModel("headline", this, null);
    }

    @Override
    protected QueryDataProvider<OtherPageHistorized, ?> getQueryDataProvider() {
        return otherPageHistoryDataProvider;
    }

    @Override
    protected Component newHistorizedView(String markupId, IModel<OtherPageHistorized> historizedModel) {
        return new OtherPagePrintPanel(markupId, new PropertyModel<OtherPage>(historizedModel, "convertedOtherPage"));
    }

    @Override
    protected void onRestore(IModel<OtherPageHistorized> restoreModel) {
        otherPageService.restoreFromHistory(restoreModel.getObject());
        info(getString("restored"));
        String id = restoreModel.getObject().getOtherPage().getId().toString();
        setResponsePage(new OtherPageViewPage(new PageParameters("0=" + id)));
    }
}
