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
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.core.module.common.panel.captcha.CaptchaAjaxButton;
import org.devproof.portal.core.module.common.panel.captcha.CaptchaAjaxLink;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
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

	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;
	@SpringBean(name = "commentDataProvider")
	private QueryDataProvider<CommentEntity> commentDataProvider;
	@SpringBean(name = "commentService")
	private CommentService commentService;
	private CommentQuery query;
	private FeedbackPanel feedback;
	private BubblePanel bubblePanel;

	private CommentDataView dataView;

	public CommentPanel(String id, CommentConfiguration configuration) {
		super(id);
		PortalUtil.addJQuery(this);
		add(CSSPackageResource.getHeaderContribution(CommentConstants.class, "css/comment.css"));
		query = new CommentQuery();
		query.setModuleName(configuration.getModuleName());
		query.setModuleContentId(configuration.getModuleContentId());
		boolean showOnlyReviewed = configurationService.findAsBoolean(CommentConstants.CONF_COMMENT_SHOW_ONLY_REVIEWED);
		if (showOnlyReviewed) {
			query.setReviewed(Boolean.TRUE);
			query.setAccepted(Boolean.TRUE);
		} else {
			query.setRejected(Boolean.FALSE);
		}
		query.setAutomaticBlocked(Boolean.FALSE);
		// query.setVisible(Boolean.TRUE);
		commentDataProvider.setQueryObject(query);
		bubblePanel = new BubblePanel("bubble");
		add(bubblePanel);

		add(new WebMarkupContainer("noCommentsHint") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return commentDataProvider.size() == 0;
			}

		});
		// Repeater
		add(dataView = new CommentDataView("listComment"));
		add(feedback = new FeedbackPanel("feedback"));
		feedback.setOutputMarkupId(true);

		add(new AjaxLink<Void>("newerLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				dataView.setCurrentPage(dataView.getCurrentPage() - 1);
				target.addComponent(CommentPanel.this);
			}

			@Override
			public boolean isVisible() {
				return dataView.getCurrentPage() != 0;
			}

		});

		add(new AjaxLink<Void>("olderLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				dataView.setCurrentPage(dataView.getCurrentPage() + 1);
				target.addComponent(CommentPanel.this);
			}

			@Override
			public boolean isVisible() {
				return (dataView.getPageCount() - 1) > dataView.getCurrentPage();
			}
		});

		// Form
		final CommentEntity comment = new CommentEntity();
		comment.setModuleName(configuration.getModuleName());
		comment.setModuleContentId(configuration.getModuleContentId());
		final Form<CommentEntity> form = new Form<CommentEntity>("form", new CompoundPropertyModel<CommentEntity>(
				comment));
		if (hideInput()) {
			form.setVisible(false);
		}
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
		form.add(new CaptchaAjaxButton("addCommentButton", bubblePanel) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClickAndCaptchaValidated(AjaxRequestTarget target) {
				String commentStr = comment.getComment();
				commentStr = StringEscapeUtils.escapeHtml(commentStr).replace("\n", "<br />");
				comment.setComment(commentStr);
				comment.setIpAddress(PortalSession.get().getIpAddress());
				form.setVisible(false);
				dataView.setCurrentPage(0);
				commentService.save(comment);
				info(getString("saved"));
				target.addComponent(CommentPanel.this);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(CommentPanel.this);
			}
		});
		add(form);
		setOutputMarkupId(true);
	}

	private class CommentDataView extends DataView<CommentEntity> {
		private static final long serialVersionUID = 1L;

		public CommentDataView(String id) {
			super(id, commentDataProvider);
			setItemsPerPage(getNumberOfPages());
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

		public CommentView(String id, final Item<CommentEntity> item) {
			super(id, "commentView", CommentPanel.this);
			item.setOutputMarkupId(true);
			comment = item.getModelObject();
			add(new CommentInfoPanel("commentInfo", comment));
			Label commentLabel = new Label("comment", comment.getComment());
			commentLabel.setEscapeModelStrings(false);
			add(commentLabel);
			CaptchaAjaxLink reportViolationLink = new CaptchaAjaxLink("reportViolationLink", bubblePanel) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClickAndCaptchaValidated(AjaxRequestTarget target) {
					CommentEntity comment = item.getModelObject();
					commentService.reportViolation(comment);
					bubblePanel.setMessage(getString("reported"));
					bubblePanel.show(getMarkupId(), target);
				}
			};
			add(reportViolationLink);

			WebMarkupContainer administrationContainer = new WebMarkupContainer("administration");
			administrationContainer.add(new Label("ipAddress", comment.getIpAddress()));

			AjaxLink<Void> acceptLink = new AjaxLink<Void>("acceptLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					commentService.acceptComment(comment);
					bubblePanel.setMessage(getString("accepted"));
					bubblePanel.show(getMarkupId(), target);
					target.addComponent(item);
				}
			};
			acceptLink.add(new Image("acceptLinkImage", CommentConstants.REF_ACCEPT_IMG));
			administrationContainer.add(acceptLink);

			AjaxLink<Void> rejectLink = new AjaxLink<Void>("rejectLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					commentService.rejectComment(comment);
					bubblePanel.setMessage(getString("rejected"));
					bubblePanel.show(getMarkupId(), target);
					target.addComponent(item);
				}
			};
			rejectLink.add(new Image("rejectLinkImage", CommentConstants.REF_REJECT_IMG));
			administrationContainer.add(rejectLink);
			IModel<String> accepted = new AbstractReadOnlyModel<String>() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getObject() {
					if (comment.getAutomaticBlocked()) {
						return getString("stateBlocked");
					}
					return comment.getAccepted() ? getString("stateAccepted") : getString("stateRejected");
				}

			};
			Label acceptedLabel = new Label("accepted", accepted) {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isVisible() {
					return comment.getReviewed() || comment.getAutomaticBlocked();
				}

			};
			acceptedLabel.add(new AttributeModifier("class", true, new AbstractReadOnlyModel<String>() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getObject() {
					if (comment.getAutomaticBlocked()) {
						return "commentBlocked";
					}
					return comment.getAccepted() ? "commentAccepted" : "commentRejected";
				}
			}));
			administrationContainer.add(acceptedLabel);
			Label nrOfBlames = new Label("numberOfBlames", String.valueOf(comment.getNumberOfBlames()));
			administrationContainer.add(nrOfBlames);
			add(administrationContainer);
		}
	}

	public int getNumberOfPages() {
		return configurationService.findAsInteger(CommentConstants.CONF_COMMENT_NUMBER_PER_PAGE);
	}

	public boolean hideInput() {
		return false;
	}
}
