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
package org.devproof.portal.module.comment.panel;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.captcha.CaptchaImageResource;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.module.comment.CommentConstants;
import org.devproof.portal.module.comment.config.CommentConfiguration;
import org.devproof.portal.module.comment.entity.CommentEntity;
import org.devproof.portal.module.comment.query.CommentQuery;
import org.devproof.portal.module.comment.service.CommentService;

/**
 * @author Carsten Hufe
 */
public class CommentPanel extends Panel {

	private static final long serialVersionUID = 1L;

	// @SpringBean(name = "configurationService")
	// private ConfigurationService configurationService;
	@SpringBean(name = "commentDataProvider")
	private QueryDataProvider<CommentEntity> commentDataProvider;
	@SpringBean(name = "commentService")
	private CommentService commentService;
	private CommentQuery query;
	private String captchaChallengeCode;
	private CaptchaImageResource captchaImageResource;

	public CommentPanel(String id, CommentConfiguration configuration) {
		super(id);
		PortalUtil.addJQuery(this);
		setCaptchaChallengeCode();
		setCaptchaImageResource();
		add(CSSPackageResource.getHeaderContribution(CommentConstants.class, "css/comment.css"));
		query = new CommentQuery();
		query.setModuleName(configuration.getModuleName());
		query.setModuleContentId(configuration.getModuleContentId());
		// query.setVisible(Boolean.TRUE);
		commentDataProvider.setQueryObject(query);

		add(new WebMarkupContainer("noCommentsHint") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return commentDataProvider.size() == 0;
			}

		});
		// Repeater
		add(new CommentDataView("listComment"));
		add(new FeedbackPanel("feedback"));
		// Form
		final CommentEntity comment = new CommentEntity();
		comment.setModuleName(configuration.getModuleName());
		comment.setModuleContentId(configuration.getModuleContentId());
		final StatelessForm<CommentEntity> form = new StatelessForm<CommentEntity>("form",
				new CompoundPropertyModel<CommentEntity>(comment));

		WebMarkupContainer guestNameContainer = new WebMarkupContainer("guestNameContainer");
		guestNameContainer.setVisible(PortalSession.get().getUser().isGuestRole());
		TextField<String> guestNameField = new RequiredTextField<String>("guestName");
		guestNameField.add(StringValidator.lengthBetween(3, 50));
		guestNameContainer.add(guestNameField);
		form.add(guestNameContainer);

		WebMarkupContainer guestEmailContainer = new WebMarkupContainer("guestEmailContainer");
		guestEmailContainer.setVisible(PortalSession.get().getUser().isGuestRole());
		TextField<String> guestEmailField = new RequiredTextField<String>("guestEmail");
		guestEmailField.add(StringValidator.maximumLength(50));
		guestEmailField.add(EmailAddressValidator.getInstance());
		guestEmailContainer.add(guestEmailField);
		form.add(guestEmailContainer);

		TextArea<String> commentField = new TextArea<String>("comment");
		commentField.add(StringValidator.lengthBetween(10, 3000));
		commentField.setRequired(true);
		form.add(commentField);
		form.add(new AjaxButton("addCommentButton") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				String commentStr = comment.getComment();
				commentStr = StringEscapeUtils.escapeHtml(commentStr).replace("\n", "<br />");
				comment.setComment(commentStr);
				comment.setIpAddress(((PortalSession) getSession()).getIpAddress());
				form.setVisible(false);
				commentService.save(comment);
				info(getString("saved"));
				target.addComponent(CommentPanel.this);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(CommentPanel.this);
			}

		});
		form.add(createCaptchaContainer());
		add(form);
		setOutputMarkupId(true);
	}

	private WebMarkupContainer createCaptchaContainer() {
		WebMarkupContainer captcha = new WebMarkupContainer("captcha");
		captcha.add(createCaptchaCodeField());
		captcha.add(createCaptchaImage());
		captcha.setVisible(Boolean.FALSE);
		return captcha;
	}

	private FormComponent<String> createCaptchaCodeField() {
		FormComponent<String> fc = new TextField<String>("captchacode", Model.of(""));
		fc.add(createCaptchaValidator());
		return fc;
	}

	private Image createCaptchaImage() {
		return new Image("captchacodeimage", captchaImageResource);
	}

	private void setCaptchaImageResource() {
		captchaImageResource = new CaptchaImageResource(captchaChallengeCode);
	}

	private void setCaptchaChallengeCode() {
		captchaChallengeCode = PortalUtil.randomString(6, 8);
	}

	private AbstractValidator<String> createCaptchaValidator() {
		return new AbstractValidator<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onValidate(IValidatable<String> ivalidatable) {
				if (!captchaChallengeCode.equalsIgnoreCase(ivalidatable.getValue())) {
					captchaImageResource.invalidate();
					error(ivalidatable);
				}
			}

			@Override
			protected String resourceKey() {
				return "wrong.captchacode";
			}
		};
	}

	private class CommentDataView extends DataView<CommentEntity> {
		private static final long serialVersionUID = 1L;

		public CommentDataView(String id) {
			super(id, commentDataProvider);
			// TODO in config
			// setItemsPerPage(10);
		}

		@Override
		protected void populateItem(Item<CommentEntity> item) {
			item.setOutputMarkupId(true);
			item.add(new CommentView("commentView", item));
		}
	}

	public class CommentView extends Fragment {

		private static final long serialVersionUID = 1L;

		private CommentEntity comment;

		public CommentView(String id, Item<CommentEntity> item) {
			super(id, "commentView", CommentPanel.this);
			comment = item.getModelObject();
			add(new CommentInfoPanel("commentInfo", comment));
			Label commentLabel = new Label("comment", comment.getComment());
			commentLabel.setEscapeModelStrings(false);
			add(commentLabel);
			final WebMarkupContainer reportViolationCaptchaContainer = new WebMarkupContainer("reportViolationCaptcha");
			reportViolationCaptchaContainer.setVersioned(false);
			reportViolationCaptchaContainer.setOutputMarkupId(true);
			reportViolationCaptchaContainer.add(new SimpleAttributeModifier("style", "display:none; width: 300px;"));
			add(reportViolationCaptchaContainer);
			AjaxLink<Void> reportViolationLink = new AjaxLink<Void>("reportViolationLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					reportViolationCaptchaContainer.setVisible(true);
					target.addComponent(reportViolationCaptchaContainer);

					String js = "var p = $(\"#" + getMarkupId() + "\");\n var pos = p.position();";
					js += "$(\"#"
							+ reportViolationCaptchaContainer.getMarkupId()
							+ "\").css( {\"position\": \"absolute\",  \"left\": (pos.left) + \"px\", \"top\":(pos.top - $(\"#"
							+ reportViolationCaptchaContainer.getMarkupId() + "\").height() - 3) + \"px\" } );";
					js += "$(\".captchaPopup\").fadeOut(\"fast\");";
					js += "$(\"#" + reportViolationCaptchaContainer.getMarkupId() + "\").fadeIn(\"slow\");";
					target.appendJavascript(js);
				}
			};
			reportViolationLink.setOutputMarkupId(true);
			add(reportViolationLink);

			WebMarkupContainer administrationContainer = new WebMarkupContainer("administration");
			administrationContainer.add(new Label("ipAddress", comment.getIpAddress()));
			if (comment.getVisible()) {
				AjaxLink<Void> deleteLink = new AjaxLink<Void>("deleteLink") {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						commentService.markCommentDeleted(comment);
						info("Deleted (but only hidden ... you can undelete.");
						target.addComponent(CommentPanel.this);
					}
				};
				deleteLink.add(new Image("deleteLinkImage", CommonConstants.REF_DELETE_IMG));
				administrationContainer.add(deleteLink);
			} else {
				AjaxLink<Void> deleteLink = new AjaxLink<Void>("deleteLink") {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						commentService.markCommentUndeleted(comment);
						info("Undeleted");
						target.addComponent(CommentPanel.this);
					}
				};
				deleteLink.add(new Image("deleteLinkImage", CommonConstants.REF_UNDELETE_IMG));
				administrationContainer.add(deleteLink);
			}
			AjaxLink<Void> reviewedLink = new AjaxLink<Void>("reviewedLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					commentService.markReviewed(comment);
					info("Marked as reviewed");
					target.addComponent(CommentPanel.this);
				}
			};
			reviewedLink.add(new Image("reviewedLinkImage", CommentConstants.REF_ACCEPTED_IMG));
			reviewedLink.setVisible(!comment.getReviewed());
			administrationContainer.add(reviewedLink);
			add(administrationContainer);
		}
	}
}
