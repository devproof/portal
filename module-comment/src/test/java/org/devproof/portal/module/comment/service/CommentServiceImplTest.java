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

import junit.framework.TestCase;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.email.service.EmailService;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.service.UserService;
import org.devproof.portal.module.comment.CommentConstants;
import org.devproof.portal.module.comment.dao.CommentDao;
import org.devproof.portal.module.comment.entity.CommentEntity;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.*;

/**
 * @author Carsten Hufe
 */
public class CommentServiceImplTest extends TestCase {
    private CommentServiceImpl impl;
    private CommentDao commentDaoMock;
    private ConfigurationService configurationServiceMock;
    private UserService userServiceMock;

    @Override
    protected void setUp() throws Exception {
        commentDaoMock = createStrictMock(CommentDao.class);
        configurationServiceMock = createStrictMock(ConfigurationService.class);
        userServiceMock = createStrictMock(UserService.class);
        impl = new CommentServiceImpl();
        impl.setCommentDao(commentDaoMock);
        impl.setConfigurationService(configurationServiceMock);
        impl.setUserService(userServiceMock);
        impl.setDisplayDateTimeFormat(new SimpleDateFormat());
    }

    public void testNewCommentEntity() {
        assertNotNull(impl.newCommentEntity());
    }

    public void testDelete() {
        CommentEntity e = new CommentEntity();
        commentDaoMock.delete(e);
        replay(commentDaoMock);
        impl.delete(e);
        verify(commentDaoMock);
    }

    public void testFindById() {
        CommentEntity e = createCommentEntity();
        expect(commentDaoMock.findById(1)).andReturn(e);
        replay(commentDaoMock);
        assertEquals(impl.findById(1), e);
        verify(commentDaoMock);
    }

    public void testSave() {
        CommentEntity e = createCommentEntity();
        expect(commentDaoMock.save(e)).andReturn(e);
        replay(commentDaoMock);
        impl.save(e);
        verify(commentDaoMock);
    }

    public void testSaveNewComment() {
        final StringBuilder called = new StringBuilder();
        final CommentEntity e = createCommentEntity();
        impl = new CommentServiceImpl() {
            @Override
            protected void sendEmailNotificationToAdmins(CommentEntity comment, Integer templateId, String right, UrlCallback urlCallback, String reporterIp) {
                called.append("sendEmailNotificationToAdmins");
                assertEquals(2, templateId.intValue());
                assertEquals(e, comment);
                assertEquals("comment.notify.newcomment", right);
            }
        };
        impl.setConfigurationService(configurationServiceMock);
        impl.setCommentDao(commentDaoMock);
        expect(commentDaoMock.save(e)).andReturn(e);
        expect(configurationServiceMock.findAsInteger(CommentConstants.CONF_NOTIFY_NEW_COMMENT)).andReturn(2);
        UrlCallback urlCallback = createUrlCallback();
        replay(commentDaoMock, configurationServiceMock);
        impl.saveNewComment(e, urlCallback);
        assertEquals("sendEmailNotificationToAdmins", called.toString());
        verify(commentDaoMock, configurationServiceMock);
    }

    public void testRejectComment() {
        CommentEntity e = createCommentEntity();
        commentDaoMock.rejectComment(e);
        commentDaoMock.refresh(e);
        replay(commentDaoMock);
        impl.rejectComment(e);
        verify(commentDaoMock);
    }

    public void testAcceptComment() {
        CommentEntity e = createCommentEntity();
        commentDaoMock.acceptComment(e);
        commentDaoMock.refresh(e);
        replay(commentDaoMock);
        impl.acceptComment(e);
        verify(commentDaoMock);
    }

    public void testFindNumberOfComments_showAll() {
        expect(configurationServiceMock.findAsBoolean(CommentConstants.CONF_COMMENT_SHOW_ONLY_REVIEWED)).andReturn(Boolean.FALSE);
        expect(commentDaoMock.findNumberOfComments("moduleName", "contentId")).andReturn(3l);
        replay(commentDaoMock, configurationServiceMock);
        impl.findNumberOfComments("moduleName", "contentId");
        verify(commentDaoMock, configurationServiceMock);
    }

    public void testFindNumberOfComments_reviewed() {
        expect(configurationServiceMock.findAsBoolean(CommentConstants.CONF_COMMENT_SHOW_ONLY_REVIEWED)).andReturn(Boolean.TRUE);
        expect(commentDaoMock.findNumberOfReviewedComments("moduleName", "contentId")).andReturn(3l);
        replay(commentDaoMock, configurationServiceMock);
        assertEquals(3l, impl.findNumberOfComments("moduleName", "contentId"));
        verify(commentDaoMock, configurationServiceMock);
    }

    public void testFindAllModuleNames() {
        expect(commentDaoMock.findAllModuleNames()).andReturn(Arrays.asList("aaa", "bbb"));
        replay(commentDaoMock);
        assertNotNull(impl.findAllModuleNames());
        verify(commentDaoMock);
    }

    public void testReportViolation_thresholdReached() {
        final StringBuilder called = new StringBuilder();
        final CommentEntity e = createCommentEntity();
        impl = new CommentServiceImpl() {
            @Override
            protected void sendEmailNotificationToAdmins(CommentEntity comment, Integer templateId, String right, UrlCallback urlCallback, String reporterIp) {
                called.append("sendEmailNotificationToAdmins");
                assertEquals(2, templateId.intValue());
                assertEquals(e, comment);
                assertEquals("123.123.123.123", reporterIp);
                assertEquals("comment.notify.autoblocked", right);
            }
        };
        impl.setConfigurationService(configurationServiceMock);
        impl.setCommentDao(commentDaoMock);
        expect(commentDaoMock.save(e)).andReturn(e);
        expect(configurationServiceMock.findAsInteger(CommentConstants.CONF_COMMENT_BLAMED_THRESHOLD)).andReturn(3);
        expect(configurationServiceMock.findAsInteger(CommentConstants.CONF_NOTIFY_AUTOBLOCKED)).andReturn(2);
        UrlCallback urlCallback = createUrlCallback();
        replay(commentDaoMock, configurationServiceMock);
        impl.reportViolation(e, urlCallback, "123.123.123.123");
        assertEquals("sendEmailNotificationToAdmins", called.toString());
        assertEquals(3, e.getNumberOfBlames().intValue());
        assertEquals(Boolean.TRUE, e.getAutomaticBlocked());
        verify(commentDaoMock, configurationServiceMock);
    }

    public void testReportViolation_thresholdNotReached() {
        final StringBuilder called = new StringBuilder();
        final CommentEntity e = createCommentEntity();
        impl = new CommentServiceImpl() {
            @Override
            protected void sendEmailNotificationToAdmins(CommentEntity comment, Integer templateId, String right, UrlCallback urlCallback, String reporterIp) {
                called.append("sendEmailNotificationToAdmins");
                assertEquals(2, templateId.intValue());
                assertEquals(e, comment);
                assertEquals("123.123.123.123", reporterIp);
                assertEquals("comment.notify.violation", right);
            }
        };
        impl.setConfigurationService(configurationServiceMock);
        impl.setCommentDao(commentDaoMock);
        expect(commentDaoMock.save(e)).andReturn(e);
        expect(configurationServiceMock.findAsInteger(CommentConstants.CONF_COMMENT_BLAMED_THRESHOLD)).andReturn(5);
        expect(configurationServiceMock.findAsInteger(CommentConstants.CONF_NOTIFY_VIOLATION)).andReturn(2);
        UrlCallback urlCallback = createUrlCallback();
        replay(commentDaoMock, configurationServiceMock);
        impl.reportViolation(e, urlCallback, "123.123.123.123");
        assertEquals("sendEmailNotificationToAdmins", called.toString());
        assertEquals(3, e.getNumberOfBlames().intValue());
        assertEquals(Boolean.FALSE, e.getAutomaticBlocked());
        verify(commentDaoMock, configurationServiceMock);
    }

    public void testSendEmailNotificationToAdmins() {
        final StringBuilder called = new StringBuilder();
        EmailService emailServiceMock = new EmailServiceMock() {
            private static final long serialVersionUID = 1L;

            @Override
            public void sendEmail(Integer templateId, EmailPlaceholderBean placeholder) {
                called.append("sendEmail");
                assertEquals(2, templateId.intValue());
                assertEquals("peterpan", placeholder.getToUsername());
                assertEquals("Peter", placeholder.getToFirstname());
                assertEquals("Pan", placeholder.getToLastname());
                assertEquals("test@email.tld", placeholder.getToEmail());
                assertEquals("testcreator", placeholder.getUsername());
                assertEquals("hello world", placeholder.getAdditionalPlaceholder().get("COMMENT"));
                assertEquals("testurl", placeholder.getAdditionalPlaceholder().get("COMMENT_URL"));
                assertEquals("123.123.123.123", placeholder.getAdditionalPlaceholder().get("REPORTER_IP"));
                assertNotNull(placeholder.getAdditionalPlaceholder().get("REPORTING_TIME"));
            }
        };
        impl.setEmailService(emailServiceMock);
        CommentEntity e = createCommentEntity();
        UrlCallback urlCallback = createUrlCallback();
        List<UserEntity> users = createUsers();
        expect(userServiceMock.findUserWithRight("testright")).andReturn(users);
        replay(userServiceMock);
        impl.sendEmailNotificationToAdmins(e, 2, "testright", urlCallback, "123.123.123.123");
        assertEquals("sendEmail", called.toString());
        verify(userServiceMock);
    }

    private List<UserEntity> createUsers() {
        UserEntity user = new UserEntity();
        user.setFirstname("Peter");
        user.setLastname("Pan");
        user.setEmail("test@email.tld");
        user.setUsername("peterpan");
        return Arrays.asList(user);
    }

    private UrlCallback createUrlCallback() {
        return new UrlCallback() {
            @Override
            public String getUrl(CommentEntity comment) {
                return "testurl";
            }
        };
    }

    private CommentEntity createCommentEntity() {
        CommentEntity comment = new CommentEntity();
        comment.setId(1);
        comment.setComment("hello world");
        comment.setCreatedBy("testcreator");
        comment.setReviewed(Boolean.FALSE);
        comment.setNumberOfBlames(2);
        return comment;
    }
}
