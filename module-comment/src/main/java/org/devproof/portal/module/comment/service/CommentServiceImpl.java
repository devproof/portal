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
package org.devproof.portal.module.comment.service;

import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.email.service.EmailService;
import org.devproof.portal.core.module.user.entity.User;
import org.devproof.portal.core.module.user.service.UserService;
import org.devproof.portal.module.comment.CommentConstants;
import org.devproof.portal.module.comment.repository.CommentRepository;
import org.devproof.portal.module.comment.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Carsten Hufe
 */
@Service("commentService")
public class CommentServiceImpl implements CommentService {

    private ConfigurationService configurationService;
    private UserService userService;
    private EmailService emailService;
    private CommentRepository commentRepository;
    private DateFormat displayDateTimeFormat;

    @Override
    public Comment newCommentEntity() {
        return new Comment();
    }

    @Override
    @Transactional
    public void delete(Comment entity) {
        commentRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Comment findById(Integer id) {
        return commentRepository.findById(id);
    }

    @Override
    @Transactional
    public void save(Comment entity) {
        commentRepository.save(entity);
    }

    @Override
    @Transactional
    public void saveNewComment(Comment comment, UrlCallback urlCallback) {
        Comment saved = commentRepository.save(comment);
        Integer templateId = configurationService.findAsInteger(CommentConstants.CONF_NOTIFY_NEW_COMMENT);
        sendEmailNotificationToAdmins(saved, templateId, "comment.notify.newcomment", urlCallback, saved.getIpAddress());
    }

    @Override
    @Transactional
    public void rejectComment(Comment comment) {
        commentRepository.rejectComment(comment);
        commentRepository.refresh(comment);
    }

    @Override
    @Transactional
    public void acceptComment(Comment comment) {
        commentRepository.acceptComment(comment);
        commentRepository.refresh(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public long findNumberOfComments(String moduleName, String moduleContentId) {
        boolean showOnlyReviewed = configurationService.findAsBoolean(CommentConstants.CONF_COMMENT_SHOW_ONLY_REVIEWED);
        if (showOnlyReviewed) {
            return commentRepository.findNumberOfReviewedComments(moduleName, moduleContentId);
        } else {
            return commentRepository.findNumberOfComments(moduleName, moduleContentId);
        }
    }

    @Override
    @Transactional
    public void reportViolation(Comment comment, UrlCallback urlCallback, String reporterIp) {
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

    protected void sendEmailNotificationToAdmins(Comment comment, Integer templateId, String right, UrlCallback urlCallback, String reporterIp) {
        EmailPlaceholderBean placeholder = new EmailPlaceholderBean();
        List<User> notifyUsers = userService.findUserWithRight(right);
        for (User notifyUser : notifyUsers) {
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

    @Override
    @Transactional(readOnly = true)
    public List<String> findAllModuleNames() {
        return commentRepository.findAllModuleNames();
    }

    @Autowired(required = false) // required false for integration test
    public void setCommentRepository(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Autowired
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    @Qualifier("displayDateTimeFormat")
    public void setDisplayDateTimeFormat(DateFormat displayDateTimeFormat) {
        this.displayDateTimeFormat = displayDateTimeFormat;
    }
}
