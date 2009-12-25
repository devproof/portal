package org.devproof.portal.core.module.common.dataprovider;

import org.devproof.portal.core.module.common.dao.DataProviderDao;
import org.devproof.portal.core.module.email.entity.EmailTemplateEntity;
import org.easymock.EasyMock;

import junit.framework.TestCase;

public class SortablePersistenceDataProviderImplTest extends TestCase {
	private SortablePersistenceDataProviderImpl<EmailTemplateEntity> impl;
	private DataProviderDao<EmailTemplateEntity> dataProviderDaoMock;
	
	@SuppressWarnings("unchecked")
	protected void setUp() throws Exception {
		dataProviderDaoMock = EasyMock.createMock(DataProviderDao.class);
		impl = new SortablePersistenceDataProviderImpl<EmailTemplateEntity>();
		impl.setEntityClass(EmailTemplateEntity.class);
		impl.setCountQuery("count(something)");
		impl.setSort("subject", true);
		impl.setDataProviderDao(dataProviderDaoMock);
	}

	public void testIterator() {
		fail("Not yet implemented");
	}

	public void testModel() {
		fail("Not yet implemented");
	}

	public void testSize() {
		fail("Not yet implemented");
	}
}
