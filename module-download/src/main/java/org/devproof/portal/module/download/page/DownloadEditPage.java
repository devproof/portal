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
package org.devproof.portal.module.download.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.config.Secured;
import org.devproof.portal.core.module.common.component.richtext.FullRichTextArea;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.core.module.right.panel.RightGridPanel;
import org.devproof.portal.core.module.tag.component.TagField;
import org.devproof.portal.module.download.DownloadConstants;
import org.devproof.portal.module.download.entity.Download;
import org.devproof.portal.module.download.entity.DownloadTag;
import org.devproof.portal.module.download.service.DownloadService;
import org.devproof.portal.module.download.service.DownloadTagService;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@Secured(DownloadConstants.AUTHOR_RIGHT)
public class DownloadEditPage extends DownloadBasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "downloadService")
    private DownloadService downloadService;
    @SpringBean(name = "downloadTagService")
    private DownloadTagService downloadTagService;
    private IModel<Download> downloadModel;

    public DownloadEditPage(IModel<Download> downloadModel) {
        super(new PageParameters());
        this.downloadModel = downloadModel;
        add(createDownloadEditForm());
    }

    private Form<Download> createDownloadEditForm() {
        Form<Download> form = newDownloadEditForm();
        form.add(createTitleField());
        form.add(createDescriptionField());
        form.add(createUrlField());
        form.add(createSoftwareVersionField());
        form.add(createDownloadSizeField());
        form.add(createManufacturerField());
        form.add(createLicenceField());
        form.add(createTagField());
        form.add(createPriceField());
        form.add(createHitsField());
        form.add(createNumberOfVotesField());
        form.add(createSumOfRatingField());
        form.add(createManufacturerHomepageField());
        form.add(createViewRightPanel());
        form.add(createDownloadRightPanel());
        form.add(createVoteRightPanel());
        form.setOutputMarkupId(true);
        return form;
    }

    private FormComponent<String> createSoftwareVersionField() {
        return new TextField<String>("softwareVersion");
    }

    private FormComponent<String> createSumOfRatingField() {
        return new RequiredTextField<String>("sumOfRating");
    }

    private FormComponent<String> createNumberOfVotesField() {
        return new RequiredTextField<String>("numberOfVotes");
    }

    private FormComponent<String> createHitsField() {
        return new RequiredTextField<String>("hits");
    }

    private FormComponent<String> createUrlField() {
        return new RequiredTextField<String>("url");
    }

    private FormComponent<String> createDescriptionField() {
        return new FullRichTextArea("description");
    }

    private FormComponent<String> createTitleField() {
        return new RequiredTextField<String>("title");
    }

    private FormComponent<String> createDownloadSizeField() {
        return new TextField<String>("downloadSize");
    }

    private FormComponent<String> createManufacturerField() {
        return new TextField<String>("manufacturer");
    }

    private FormComponent<String> createManufacturerHomepageField() {
        return new TextField<String>("manufacturerHomepage");
    }

    private FormComponent<String> createLicenceField() {
        return new TextField<String>("licence");
    }

    private FormComponent<String> createPriceField() {
        return new TextField<String>("price");
    }

    private TagField<DownloadTag> createTagField() {
        IModel<List<DownloadTag>> downloadListModel = new PropertyModel<List<DownloadTag>>(downloadModel, "tags");
        return new TagField<DownloadTag>("tags", downloadListModel, downloadTagService);
    }

    private Form<Download> newDownloadEditForm() {
        IModel<Download> compoundModel = new CompoundPropertyModel<Download>(downloadModel);
        return new Form<Download>("form", compoundModel) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                DownloadEditPage.this.setVisible(false);
                Download download = downloadModel.getObject();
                download.setBroken(Boolean.FALSE);
                downloadService.save(download);
                setRedirect(false);
                info(getString("msg.saved"));
                setResponsePage(new DownloadPage(new PageParameters("id=" + download.getId())));
            }
        };
    }

    private RightGridPanel createVoteRightPanel() {
        IModel<List<Right>> rightsModel = new PropertyModel<List<Right>>(downloadModel, "allRights");
        return new RightGridPanel("voteRights", "download.vote", rightsModel);
    }

    private RightGridPanel createDownloadRightPanel() {
        IModel<List<Right>> rightsModel = new PropertyModel<List<Right>>(downloadModel, "allRights");
        return new RightGridPanel("downloadRights", "download.download", rightsModel);
    }

    private RightGridPanel createViewRightPanel() {
        IModel<List<Right>> rightsModel = new PropertyModel<List<Right>>(downloadModel, "allRights");
        return new RightGridPanel("viewRights", "download.view", rightsModel);
    }
}
