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
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.*;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.core.module.common.panel.captcha.CaptchaAjaxButton;
import org.devproof.portal.core.module.common.panel.captcha.CaptchaAjaxLink;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.module.comment.CommentConstants;
import org.devproof.portal.module.comment.config.CommentConfiguration;
import org.devproof.portal.module.comment.entity.CommentEntity;
import org.devproof.portal.module.comment.page.CommentAdminPage;
import org.devproof.portal.module.comment.query.CommentQuery;
import org.devproof.portal.module.comment.service.CommentService;
import org.devproof.portal.module.comment.service.UrlCallback;

/**
 * @author Carsten Hufe
 */
public class CommentPanel extends Panel {

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;
	@SpringBean(name = "commentDataProvider")
	private QueryDataProvider<CommentEntity, CommentQuery> commentDataProvider;
	@SpringBean(name = "commentService")
	private CommentService commentService;
	private IModel<CommentQuery> queryModel;
	private FeedbackPanel feedbackPanel;
	private BubblePanel bubblePanel;

	private CommentDataView dataView;
	private CommentConfiguration configuration;
	private IModel<CommentEntity> commentModel;
	private boolean hasSubmitted = false;

	public CommentPanel(String id, CommentConfiguration configuration) {
		super(id);
		this.configuration = configuration;
		this.queryModel = createCommentQueryModel();
		this.commentModel = createNewCommentModelForForm();
		add(createCSSHeaderContributor());
		add(createBubblePanel());
		add(createNoCommentsHintContainer());
		add(createRepeatingComments());
		add(createFeedbackPanel());
		add(createNewerLink());
		add(createOlderLink());
		add(createCommentForm());
		add(createLoginToWriteCommentMessageContainer());
		setOutputMarkupId(true);
	}

	private IModel<CommentQuery> createCommentQueryModel() {
		IModel<CommentQuery> searchQueryModel = commentDataProvider.getSearchQueryModel();
		CommentQuery query = searchQueryModel.getObject();
        query.setModuleName(configuration.getModuleName());
		query.setModuleContentId(configuration.getModuleContentId());
		return searchQueryModel;
	}

	private Form<CommentEntity> createCommentForm() {
		Form<CommentEntity> form = newCommentForm();
		form.add(createGuestNameContainer());
		form.add(createGuestEmailContainer());
		form.add(createCommentField());
		form.add(createAddCommentButton());
		return form;
	}

	private WebMarkupContainer createGuestNameContainer() {
		WebMarkupContainer guestNameContainer = new WebMarkupContainer("guestNameContainer");
		guestNameContainer.setVisible(PortalSession.get().getUser().isGuestRole());
		guestNameContainer.add(createGuestNameField());
		return guestNameContainer;
	}

	private TextField<String> createGuestNameField() {
		TextField<String> guestNameField = new RequiredTextField<String>("guestName");
		guestNameField.add(StringValidator.lengthBetween(3, 50));
		return guestNameField;
	}

	private WebMarkupContainer createGuestEmailContainer() {
		WebMarkupContainer guestEmailContainer = new WebMarkupContainer("guestEmailContainer");
		guestEmailContainer.setVisible(PortalSession.get().getUser().isGuestRole());
		guestEmailContainer.add(createGuestEmailField());
		return guestEmailContainer;
	}

	private TextField<String> createGuestEmailField() {
		TextField<String> guestEmailField = new RequiredTextField<String>("guestEmail");
		guestEmailField.add(StringValidator.maximumLength(50));
		guestEmailField.add(EmailAddressValidator.getInstance());
		return guestEmailField;
	}

	private TextArea<String> createCommentField() {
		TextArea<String> commentField = new TextArea<String>("comment");
		commentField.add(StringValidator.lengthBetween(10, 3000));
		commentField.setRequired(true);
		return commentField;
	}

	private CaptchaAjaxButton createAddCommentButton() {
		return new CaptchaAjaxButton("addCommentButton", bubblePanel) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClickAndCaptchaValidated(AjaxRequestTarget target) {
				hasSubmitted = true;
				CommentEntity comment = commentModel.getObject();
				String commentStr = comment.getComment();
				commentStr = StringEscapeUtils.escapeHtml(commentStr).replace("\n", "<br />");
				comment.setComment(commentStr);
				comment.setIpAddress(PortalSession.get().getIpAddress());
				dataView.setCurrentPage(0);
				commentService.saveNewComment(comment, getUrlCallback());
				info(getString("saved"));
				target.addComponent(CommentPanel.this);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.addComponent(CommentPanel.this);
			}
		};
	}

	private WebMarkupContainer createLoginToWriteCommentMessageContainer() {
		return new WebMarkupContainer("loginToWriteComment") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return !configuration.isAllowedToWrite();
			}

		};
	}

	private Form<CommentEntity> newCommentForm() {
		CompoundPropertyModel<CommentEntity> compoundModel = new CompoundPropertyModel<CommentEntity>(commentModel);
		return new Form<CommentEntity>("form", compoundModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return !hideInput() && configuration.isAllowedToWrite() && !hasSubmitted;
			}
		};
	}

	private IModel<CommentEntity> createNewCommentModelForForm() {
		CommentEntity comment = new CommentEntity();
		comment.setModuleName(configuration.getModuleName());
		comment.setModuleContentId(configuration.getModuleContentId());
		return Model.of(comment);
	}

	private AjaxLink<Void> createOlderLink() {
		return new AjaxLink<Void>("olderLink") {
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
		};
	}

	private AjaxLink<Void> createNewerLink() {
		return new AjaxLink<Void>("newerLink") {
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

		};
	}

	private FeedbackPanel createFeedbackPanel() {
		feedbackPanel = new FeedbackPanel("feedback");
		feedbackPanel.setOutputMarkupId(true);
		return feedbackPanel;
	}

	private CommentDataView createRepeatingComments() {
		dataView = new CommentDataView("repeatingComments");
		return dataView;
	}

	private WebMarkupContainer createNoCommentsHintContainer() {
		return new WebMarkupContainer("noCommentsHint") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return commentDataProvider.size() == 0;
			}
		};
	}

	private BubblePanel createBubblePanel() {
		bubblePanel = new BubblePanel("bubble");
		return bubblePanel;
	}

	private HeaderContributor createCSSHeaderContributor() {
		return CSSPackageResource.getHeaderContribution(CommentConstants.class, "css/comment.css");
	}

    private class CommentDataView extends DataView<CommentEntity> {
		private static final long serialVersionUID = 1L;

		public CommentDataView(String id) {
			super(id, commentDataProvider);
			setItemsPerPage(getNumberOfPages());
		}

		@Override
		protected void populateItem(Item<CommentEntity> item) {
			item.add(createCommentView(item));
			item.setOutputMarkupId(true);
		}

		private CommentView createCommentView(Item<CommentEntity> item) {
			return new CommentView("commentView", item);
		}
	}

	public int getNumberOfPages() {
		return configurationService.findAsInteger(CommentConstants.CONF_COMMENT_NUMBER_PER_PAGE);
	}

	public boolean hideInput() {
		return false;
	}

	public class CommentView extends Fragment {

		private static final long serialVersionUID = 1L;
		private Item<CommentEntity> item;

		public CommentView(String id, Item<CommentEntity> item) {
			super(id, "commentView", CommentPanel.this);
			this.item = item;
			add(createCommentView());
			add(createCommentContentLabel());
			add(createReportViolationLink());
			add(createAppropriateAuthorPanel());
			item.setOutputMarkupId(true);
		}

		private CaptchaAjaxLink createReportViolationLink() {
			return new CaptchaAjaxLink("reportViolationLink", bubblePanel) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClickAndCaptchaValidated(AjaxRequestTarget target) {
					CommentEntity comment = item.getModelObject();
					commentService.reportViolation(comment, getUrlCallback(), PortalSession.get().getIpAddress());
					bubblePanel.showMessage(getMarkupId(), target, getString("reported"));
				}
			};
		}

		private Label createCommentContentLabel() {
            IModel<String> commentModel = new PropertyModel<String>(item.getModel(), "comment");
            Label commentLabel = new Label("comment", commentModel);
			commentLabel.setEscapeModelStrings(false);
			return commentLabel;
		}

		private CommentInfoPanel createCommentView() {
			return new CommentInfoPanel("commentInfo", item.getModel());
		}

		private Component createAppropriateAuthorPanel() {
			if (isAuthor()) {
				return new CommentAdminView("administration", item);
			} else {
				return new EmptyPanel("administration");
			}
		}
	}

    protected boolean isAuthor() {
        PortalSession session = (PortalSession) getSession();
		return session.hasRight(CommentConstants.AUTHOR_RIGHT);
    }
	public class CommentAdminView extends Fragment {

		private static final long serialVersionUID = 1L;

		private Item<CommentEntity> item;

		public CommentAdminView(String id, Item<CommentEntity> item) {
			super(id, "commentAdminView", CommentPanel.this);
			this.item = item;
			add(createIpAddressLabel());
			add(createAcceptLink());
			add(createRejectLink());
			add(createAcceptedLabel());
			add(createNumberOfBlamesLabel());
		}

		private AjaxLink<Void> createRejectLink() {
			AjaxLink<Void> rejectLink = newRejectLink();
			rejectLink.add(new Image("rejectLinkImage", CommentConstants.REF_REJECT_IMG));
			return rejectLink;
		}

		private AjaxLink<Void> newRejectLink() {
			return new AjaxLink<Void>("rejectLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					commentService.rejectComment(item.getModelObject());
					bubblePanel.showMessage(getMarkupId(), target, getString("rejected"));
					target.addComponent(item);
				}
			};
		}

		private AjaxLink<Void> createAcceptLink() {
			AjaxLink<Void> acceptLink = newAcceptLink();
			acceptLink.add(new Image("acceptLinkImage", CommentConstants.REF_ACCEPT_IMG));
			return acceptLink;
		}

		private AjaxLink<Void> newAcceptLink() {
			return new AjaxLink<Void>("acceptLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					commentService.acceptComment(item.getModelObject());
					bubblePanel.showMessage(getMarkupId(), target, getString("accepted"));
					target.addComponent(item);
				}
			};
		}

		private Label createIpAddressLabel() {
			return new Label("ipAddress", item.getModelObject().getIpAddress());
		}

		private Label createAcceptedLabel() {
			Label acceptedLabel = newAcceptedLabel();
			acceptedLabel.add(createStyleModifier());
			return acceptedLabel;
		}

		private Label newAcceptedLabel() {
			return new Label("accepted", createAcceptedLabelModel()) {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isVisible() {
					CommentEntity comment = item.getModelObject();
					return comment.getReviewed() || comment.getAutomaticBlocked();
				}

			};
		}

		private IModel<String> createAcceptedLabelModel() {
			return new AbstractReadOnlyModel<String>() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getObject() {
					CommentEntity comment = item.getModelObject();
					if (comment.getAutomaticBlocked()) {
						return getString("stateBlocked");
					}
					return comment.getAccepted() ? getString("stateAccepted") : getString("stateRejected");
				}

			};
		}

		private AttributeModifier createStyleModifier() {
			final CommentEntity comment = item.getModelObject();
			return new AttributeModifier("class", true, new AbstractReadOnlyModel<String>() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getObject() {
					if (comment.getAutomaticBlocked()) {
						return "commentBlocked";
					}
					return comment.getAccepted() ? "commentAccepted" : "commentRejected";
				}
			});
		}

		private Label createNumberOfBlamesLabel() {
			CommentEntity comment = item.getModelObject();
			return new Label("numberOfBlames", String.valueOf(comment.getNumberOfBlames()));
		}
	}

	protected UrlCallback getUrlCallback() {
		return new UrlCallback() {
			@Override
			public String getUrl(CommentEntity comment) {
				String requestUrl = getWebRequest().getHttpServletRequest().getRequestURL().toString();
				PageParameters param = new PageParameters();
				param.add(CommentAdminPage.PARAM_ID, String.valueOf(comment.getId()));
				StringBuffer url = new StringBuffer(StringUtils.substringBeforeLast(requestUrl, "/")).append("/");
				url.append(urlFor(CommentAdminPage.class, param));
				return url.toString();
			}
		};
	}

    public IModel<CommentQuery> getCommentQueryModel() {
        return queryModel;
    }
}
