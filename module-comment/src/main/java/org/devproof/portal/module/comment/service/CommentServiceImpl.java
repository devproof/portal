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
package org.devproof.portal.module.comment.service;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.email.service.EmailService;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.service.UserService;
import org.devproof.portal.module.comment.CommentConstants;
import org.devproof.portal.module.comment.dao.CommentDao;
import org.devproof.portal.module.comment.entity.CommentEntity;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author Carsten Hufe
 */
public class CommentServiceImpl implements CommentService {

	private ConfigurationService configurationService;
	private UserService userService;
	private EmailService emailService;
	private CommentDao commentDao;
	private DateFormat displayDateTimeFormat;

	@Override
	public CommentEntity newCommentEntity() {
		return new CommentEntity();
	}

	@Override
	public void delete(CommentEntity entity) {
		commentDao.delete(entity);
	}

	@Override
	public List<CommentEntity> findAll() {
		return commentDao.findAll();
	}

	@Override
	public CommentEntity findById(Integer id) {
		return commentDao.findById(id);
	}

	@Override
	public void save(CommentEntity entity) {
		commentDao.save(entity);
	}

	@Override
	public void saveNewComment(CommentEntity comment, UrlCallback urlCallback) {
		CommentEntity saved = commentDao.save(comment);
		Integer templateId = configurationService.findAsInteger(CommentConstants.CONF_NOTIFY_NEW_COMMENT);
		sendEmailNotificationToAdmins(saved, templateId, "comment.notify.newcomment", urlCallback, saved.getIpAddress());
	}

	@Override
	public void rejectComment(CommentEntity comment) {
		commentDao.rejectComment(comment);
		commentDao.refresh(comment);
	}

	@Override
	public void acceptComment(CommentEntity comment) {
		commentDao.acceptComment(comment);
		commentDao.refresh(comment);
	}

	@Override
	public long findNumberOfComments(String moduleName, String moduleContentId) {
		boolean showOnlyReviewed = configurationService.findAsBoolean(CommentConstants.CONF_COMMENT_SHOW_ONLY_REVIEWED);
		if (showOnlyReviewed) {
			return commentDao.findNumberOfReviewedComments(moduleName, moduleContentId);
		} else {
			return commentDao.findNumberOfComments(moduleName, moduleContentId);
		}
	}

	@Override
	public void reportViolation(CommentEntity comment, UrlCallback urlCallback, String reporterIp) {
		int maxNumberOfBlames = configurationService.findAsInteger(CommentConstants.CONF_COMMENT_BLAMED_THRESHOLD);
		int blames = comment.getNumberOfBlames() + 1;
		comment.setNumberOfBlames(blames);
		boolean automaticBlocked = blames >= maxNumberOfBlames;
		if (automaticBlocked && !comment.getReviewed()) {
			comment.setAutomaticBlocked(automaticBlocked);
			save(comment);
			Integer templateId = configurationService.findAsInteger(CommentConstants.CONF_NOTIFY_AUTOBLOCKED);
			sendEmailNotificationToAdmins(comment, templateId, "comment.notify.autoblocked", urlCallback, reporterIp);
		} else {
			save(comment);
			Integer templateId = configurationService.findAsInteger(CommentConstants.CONF_NOTIFY_VIOLATION);
			sendEmailNotificationToAdmins(comment, templateId, "comment.notify.violation", urlCallback, reporterIp);
		}
	}

	protected void sendEmailNotificationToAdmins(CommentEntity comment, Integer templateId, String right,
			UrlCallback urlCallback, String reporterIp) {
		EmailPlaceholderBean placeholder = new EmailPlaceholderBean();
		List<UserEntity> notifyUsers = userService.findUserWithRight(right);
		for (UserEntity notifyUser : notifyUsers) {
			placeholder.setToUsername(notifyUser.getUsername());
			placeholder.setToFirstname(notifyUser.getFirstname());
			placeholder.setToLastname(notifyUser.getLastname());
			placeholder.setToEmail(notifyUser.getEmail());
			placeholder.setUsername(comment.getGuestName() != null ? comment.getGuestName() : comment.getCreatedBy());
			placeholder.put("COMMENT", comment.getComment());
			placeholder.put("COMMENT_URL", urlCallback.getUrl(comment));
			placeholder.put("REPORTER_IP", reporterIp);
			placeholder.put("REPORTING_TIME", displayDateTimeFormat.format(new Date()));
			emailService.sendEmail(templateId, placeholder);
		}
	}

	@Required
	public void setCommentDao(CommentDao commentDao) {
		this.commentDao = commentDao;
	}

	@Required
	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	@Required
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Required
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	@Required
	public void setDisplayDateTimeFormat(DateFormat displayDateTimeFormat) {
		this.displayDateTimeFormat = displayDateTimeFormat;
	}
}
