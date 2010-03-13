package org.devproof.portal.module.comment.service;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;

import org.devproof.portal.module.comment.dao.CommentDao;
import org.devproof.portal.module.comment.entity.CommentEntity;

/**
 * @author Carsten Hufe
 */
public class CommentServiceImplTest extends TestCase {
	private CommentServiceImpl impl;
	private CommentDao mock;

	@Override
	protected void setUp() throws Exception {
		mock = createStrictMock(CommentDao.class);
		impl = new CommentServiceImpl();
		impl.setCommentDao(mock);
	}

	public void testNewCommentEntity() {
		assertNotNull(impl.newCommentEntity());
	}

	public void testDelete() {
		CommentEntity e = new CommentEntity();
		mock.delete(e);
		replay(mock);
		impl.delete(e);
		verify(mock);
	}

	public void testFindById() {
		CommentEntity e = createCommentEntity();
		expect(mock.findById(1)).andReturn(e);
		replay(mock);
		assertEquals(impl.findById(1), e);
		verify(mock);
	}

	public void testSave() {
		CommentEntity e = createCommentEntity();
		expect(mock.save(e)).andReturn(e);
		replay(mock);
		impl.save(e);
		verify(mock);
	}

	public void testSaveNewComment() {
		// impl.saveNewComment(comment, urlCallback)
	}

	public void testRejectComment() {
		fail("Not yet implemented");
	}

	public void testAcceptComment() {
		fail("Not yet implemented");
	}

	public void testFindNumberOfComments() {
		fail("Not yet implemented");
	}

	public void testReportViolation() {
		fail("Not yet implemented");
	}

	public void testSendEmailNotificationToAdmins() {
		fail("Not yet implemented");
	}

	private CommentEntity createCommentEntity() {
		CommentEntity comment = new CommentEntity();
		comment.setId(1);
		return comment;
	}
}
