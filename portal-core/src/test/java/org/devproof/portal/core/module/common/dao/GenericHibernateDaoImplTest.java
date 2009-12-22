package org.devproof.portal.core.module.common.dao;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.devproof.portal.core.module.configuration.entity.ConfigurationEntity;
import org.easymock.EasyMock;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;

public class GenericHibernateDaoImplTest extends TestCase {
	private GenericHibernateDaoImpl<ConfigurationEntity, String> impl;
	private SessionFactory sessionFactory;
	private Session session;
	private Query query;
	@Override
	public void setUp() throws Exception {
		sessionFactory = EasyMock.createMock(SessionFactory.class);
		session = EasyMock.createMock(Session.class);
		query = EasyMock.createMock(Query.class);
		impl = new GenericHibernateDaoImpl<ConfigurationEntity, String>(ConfigurationEntity.class);
		impl.setSessionFactory(sessionFactory);
		EasyMock.expect(session.getSessionFactory()).andReturn(sessionFactory);
//		EasyMock.expect(session.isOpen()).andReturn(false);
		EasyMock.expect(sessionFactory.openSession()).andReturn(session);
//		EasyMock.expect(session.getSessionFactory()).andReturn(sessionFactory);
//		SessionHolder sessionHolder = new SessionHolder(session);
//		TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);
	}

	public void testFindById() {
		ConfigurationEntity expectedConfig = newConfiguration();
		EasyMock.expect(session.get(ConfigurationEntity.class, "abc")).andReturn(expectedConfig);
		EasyMock.replay(sessionFactory, session);
		ConfigurationEntity newConfig = impl.findById("abc");
		assertEquals(expectedConfig, newConfig);
		EasyMock.verify(session, sessionFactory);		
	}

	public void testFindAll() {
		List<ConfigurationEntity> expectedConfigs = Arrays.asList(newConfiguration());
		EasyMock.expect(session.createQuery("Select distinct(e) from ConfigurationEntity e")).andReturn(query);
		EasyMock.expect(query.list()).andReturn(expectedConfigs);
		EasyMock.replay(sessionFactory, session, query);
		List<ConfigurationEntity> configs = impl.findAll();
		assertEquals(expectedConfigs.get(0).getKey(), configs.get(0).getKey());
		EasyMock.verify(session, sessionFactory, query);		
	}

	public void testSave() {
		ConfigurationEntity config = newConfiguration();
		EasyMock.expect(session.save(config)).andReturn("abc");
		EasyMock.replay(sessionFactory, session, query);
		impl.save(config);
		EasyMock.verify(session, sessionFactory, query);	
	}

	public void testRefresh() {
		fail("Not yet implemented");
	}

	public void testDelete() {
		fail("Not yet implemented");
	}

	public void testExecuteFinder() {
		fail("Not yet implemented");
	}

	public void testExecuteUpdate() {
		fail("Not yet implemented");
	}

	private ConfigurationEntity newConfiguration() {
		ConfigurationEntity expectedConfig = new ConfigurationEntity();
		expectedConfig.setKey("abc");
		return expectedConfig;
	}
}
