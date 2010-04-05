/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.module.otherpage.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.devproof.portal.core.module.common.component.richtext.FullRichTextArea;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.right.panel.RightGridPanel;
import org.devproof.portal.module.otherpage.entity.OtherPageEntity;
import org.devproof.portal.module.otherpage.service.OtherPageService;

import java.util.List;

/**
 * @author Carsten Hufe
 */
public class OtherPageEditPage extends OtherPageBasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "otherPageService")
    private OtherPageService otherPageService;
    private IModel<OtherPageEntity> otherPageModel;

    public OtherPageEditPage(IModel<OtherPageEntity> otherPageModel) {
        super(new PageParameters());
        this.otherPageModel = otherPageModel;
        add(createOtherPageEditForm());
    }

    private Form<OtherPageEntity> createOtherPageEditForm() {
        Form<OtherPageEntity> form = newOtherPageEditForm();
        form.add(createContentIdField());
        form.add(createContentField());
        form.add(createViewRightPanel());
        form.setOutputMarkupId(true);
        return form;
    }

    private FormComponent<String> createContentField() {
        return new FullRichTextArea("content");
    }

    private FormComponent<String> createContentIdField() {
        FormComponent<String> fc = new RequiredTextField<String>("contentId");
        fc.add(createContentIdPatternValidator());
        fc.add(createContentIdValidator());
        return fc;
    }

    private PatternValidator createContentIdPatternValidator() {
        return new PatternValidator("[A-Za-z0-9\\_\\._\\-]*");
    }

    private AbstractValidator<String> createContentIdValidator() {
        return new AbstractValidator<String>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onValidate(IValidatable<String> ivalidatable) {
                OtherPageEntity otherPage = otherPageModel.getObject();
                if (otherPageService.existsContentId(ivalidatable.getValue()) && otherPage.getId() == null) {
                    error(ivalidatable);
                }
            }

            @Override
            protected String resourceKey() {
                return "existing.contentId";
            }
        };
    }

    private RightGridPanel createViewRightPanel() {
        IModel<List<RightEntity>> allRightsModel = new PropertyModel<List<RightEntity>>(otherPageModel, "allRights");
        return new RightGridPanel("viewright", "otherPage.view", allRightsModel);
    }

    private Form<OtherPageEntity> newOtherPageEditForm() {
        IModel<OtherPageEntity> compoundModel = new CompoundPropertyModel<OtherPageEntity>(otherPageModel);
        return new Form<OtherPageEntity>("form", compoundModel) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                OtherPageEntity otherPage = otherPageModel.getObject();
                otherPageService.save(otherPage);
                setRedirect(false);
                info(OtherPageEditPage.this.getString("msg.saved"));
                setResponsePage(new OtherPageViewPage(new PageParameters("0=" + otherPage.getContentId())));
            }
        };
    }
}
