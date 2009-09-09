/*
 * Copyright 2009 Carsten Hufe devproof.org
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
package org.devproof.portal.module.otherpage.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.devproof.portal.core.module.common.component.richtext.RichTextArea;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.right.panel.RightGridPanel;
import org.devproof.portal.module.otherpage.entity.OtherPageEntity;
import org.devproof.portal.module.otherpage.service.OtherPageService;

/**
 * @author Carsten Hufe
 */
public class OtherPageEditPage extends OtherPageBasePage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "otherPageService")
	private OtherPageService otherPageService;

	public OtherPageEditPage(final OtherPageEntity otherPage) {
		super(new PageParameters());

		Form<OtherPageEntity> form = new Form<OtherPageEntity>("form", new CompoundPropertyModel<OtherPageEntity>(
				otherPage)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				otherPageService.save(otherPage);
				setRedirect(false);
				info(OtherPageEditPage.this.getString("msg.saved"));
				setResponsePage(new OtherPageViewPage(new PageParameters("0=" + otherPage.getContentId())));

			}
		};
		form.setOutputMarkupId(true);
		add(form);
		form.add(new RightGridPanel("viewright", "otherPage.view", new ListModel<RightEntity>(otherPage.getAllRights())));

		FormComponent<String> fc;

		fc = new RequiredTextField<String>("contentId");
		fc.add(StringValidator.minimumLength(5));
		fc.add(new PatternValidator("[A-Za-z0-9\\_\\._\\-]*"));
		fc.add(new AbstractValidator<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onValidate(final IValidatable<String> ivalidatable) {
				if (otherPageService.existsContentId(ivalidatable.getValue()) && otherPage.getId() == null) {
					error(ivalidatable);
				}
			}

			@Override
			protected String resourceKey() {
				return "existing.contentId";
			}
		});
		form.add(fc);

		fc = new RichTextArea("content");
		fc.setRequired(true);
		fc.add(StringValidator.minimumLength(10));
		form.add(fc);
	}
}
