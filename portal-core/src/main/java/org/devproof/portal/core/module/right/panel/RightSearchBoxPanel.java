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
package org.devproof.portal.core.module.right.panel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.time.Duration;
import org.devproof.portal.core.module.right.query.RightQuery;

/**
 * @author Carsten Hufe
 */
public abstract class RightSearchBoxPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private IModel<RightQuery> queryModel;

    public RightSearchBoxPanel(String id, IModel<RightQuery> queryModel) {
        super(id);
        this.queryModel = queryModel;
        add(createRightSearchForm());
    }

    private Form<RightQuery> createRightSearchForm() {
        Form<RightQuery> form = new Form<RightQuery>("searchForm", new CompoundPropertyModel<RightQuery>(queryModel));
        form.add(createSearchTextField());
        form.setOutputMarkupId(true);
        return form;
    }

    private FormComponent<String> createSearchTextField() {
        FormComponent<String> fc = new TextField<String>("rightAndDescription");
        fc.add(new AjaxRefresh("onkeyup"));
        return fc;
    }

    protected abstract void onSubmit(AjaxRequestTarget target);

    private class AjaxRefresh extends AjaxFormSubmitBehavior {
        private static final long serialVersionUID = 1L;

        public AjaxRefresh(String event) {
            super(event);
            setThrottleDelay(Duration.ONE_SECOND);
        }

        @Override
        protected void onError(AjaxRequestTarget target) {
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target) {
            RightSearchBoxPanel.this.onSubmit(target);
        }
    }
}
