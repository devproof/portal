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
package org.devproof.portal.core.module.historization.page;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;

/**
 * Shows the historized dataset and provides restore buttons
 *
 * @author Carsten Hufe
 */
public abstract class AbstractRestoreViewPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    @SpringBean(name = "configurationService")
    private ConfigurationService configurationService;

    public AbstractRestoreViewPage() {
        super(new PageParameters());
        add(newHistorizedView("view"));
        add(newRestoreLink("topRestore"));
        add(newRestoreLink("bottomRestore"));
        add(newBackLink("topBack"));
        add(newBackLink("bottomBack"));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        addSyntaxHighlighter(response);
    }

    private Link<Void> newBackLink(final String markupId) {
        return new Link<Void>(markupId) {
            private static final long serialVersionUID = 9215404203013168802L;

            @Override
            public void onClick() {
                onBack();
            }
        };
    }

    private Link<Void> newRestoreLink(final String markupId) {
        return new Link<Void>(markupId) {
            private static final long serialVersionUID = 9215404203013168802L;

            @Override
            public void onClick() {
                onRestore();
            }
        };
    }

    /**
     * Gets executed when clicking on the restore button
     */
    protected abstract void onRestore();

    /**
     * Gets executed when clicking on the back button
     */
    protected abstract void onBack();

    /**
     * Must return a component which displays the historized part
     *
     * @param markupId markup id for the compoennt
     * @return component
     */
    protected abstract Component newHistorizedView(String markupId);
}
