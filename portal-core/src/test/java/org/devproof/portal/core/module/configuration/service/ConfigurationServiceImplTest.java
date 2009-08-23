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
package org.devproof.portal.core.module.configuration.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.devproof.portal.core.module.configuration.dao.ConfigurationDao;
import org.devproof.portal.core.module.configuration.entity.ConfigurationEntity;
import org.devproof.portal.core.module.configuration.registry.ConfigurationRegistryImpl;
import org.easymock.EasyMock;

/**
 * @author Carsten Hufe
 */
public class ConfigurationServiceImplTest extends TestCase {
	private ConfigurationServiceImpl impl;
	private ConfigurationDao mock;
	private List<ConfigurationEntity> list;

	@Override
	public void setUp() throws Exception {
		mock = EasyMock.createStrictMock(ConfigurationDao.class);
		impl = new ConfigurationServiceImpl();
		impl.setConfigurationDao(mock);
		impl.setConfigurationRegistry(new ConfigurationRegistryImpl());
		list = new ArrayList<ConfigurationEntity>();
		ConfigurationEntity c = new ConfigurationEntity();
		c.setKey("date_format");
		c.setValue("dd-mm-yyyy");
		impl.setDateFormat(new SimpleDateFormat(c.getValue()));
		c.setType(String.class.getName());
		list.add(c);
		c = new ConfigurationEntity();
		c.setKey("date_time_format");
		c.setValue("dd-mm-yyyy hh:mm");
		impl.setDateTimeFormat(new SimpleDateFormat(c.getValue()));
		c.setType(String.class.getName());

		list.add(c);

	}

	public void testSave() {
		final ConfigurationEntity e = new ConfigurationEntity();
		e.setKey("foo");
		e.setValue("bar");
		mock.save(e);
		EasyMock.replay(mock);
		impl.save(e);
		EasyMock.verify(mock);
	}

	public void testDelete() {
		final ConfigurationEntity e = new ConfigurationEntity();
		e.setKey("foo");
		e.setValue("bar");
		mock.delete(e);
		EasyMock.replay(mock);
		impl.delete(e);
		EasyMock.verify(mock);
	}

	public void testFindById() {
		final ConfigurationEntity e = new ConfigurationEntity();
		e.setKey("foo");
		e.setValue("bar");
		EasyMock.expect(mock.findById("foo")).andReturn(e);
		EasyMock.replay(mock);
		assertEquals(impl.findById("foo"), e);
		EasyMock.verify(mock);
	}

	public void testFindAll() {
		final List<ConfigurationEntity> list = new ArrayList<ConfigurationEntity>();
		list.add(new ConfigurationEntity());
		list.add(new ConfigurationEntity());
		EasyMock.expect(mock.findAll()).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(list, impl.findAll());
		EasyMock.verify(mock);
	}

	public void testFindConfigurationGroups() {
		final List<String> list = new ArrayList<String>();
		list.add("group1");
		list.add("group2");
		EasyMock.expect(mock.findConfigurationGroups()).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(list, impl.findConfigurationGroups());
		EasyMock.verify(mock);
	}

	public void testFindConfigurationsByGroup() {
		final List<ConfigurationEntity> list = new ArrayList<ConfigurationEntity>();
		list.add(new ConfigurationEntity());
		list.add(new ConfigurationEntity());
		EasyMock.expect(mock.findConfigurationsByGroup("group")).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(list, impl.findConfigurationsByGroup("group"));
		EasyMock.verify(mock);
	}

	public void testFindAsObject() {
		final ConfigurationEntity c = new ConfigurationEntity();
		c.setKey("key");
		c.setValue("value");
		c.setType(String.class.getName());
		list.add(c);
		EasyMock.expect(mock.findAll()).andReturn(list);
		EasyMock.replay(mock);
		impl.init();
		assertNotNull(impl.findAsObject("key"));
		EasyMock.verify(mock);
	}

	public void testFindAsBoolean() {
		final ConfigurationEntity c = new ConfigurationEntity();
		c.setKey("key");
		c.setValue("true");
		c.setType(Boolean.class.getName());
		list.add(c);
		EasyMock.expect(mock.findAll()).andReturn(list);
		EasyMock.replay(mock);
		impl.init();
		assertTrue(impl.findAsBoolean("key"));
		EasyMock.verify(mock);
	}

	public void testFindAsDate() {
		final ConfigurationEntity c = new ConfigurationEntity();
		c.setKey("key");
		c.setValue("15-01-2008");
		c.setType(Date.class.getName());
		list.add(c);
		EasyMock.expect(mock.findAll()).andReturn(list);
		EasyMock.replay(mock);
		impl.init();
		assertNotNull(impl.findAsDate("key"));
		EasyMock.verify(mock);
	}

	public void testFindAsDouble() {
		final ConfigurationEntity c = new ConfigurationEntity();
		c.setKey("key");
		c.setValue("12.34");
		c.setType(Double.class.getName());
		list.add(c);
		EasyMock.expect(mock.findAll()).andReturn(list);
		EasyMock.replay(mock);
		impl.init();
		final double d = impl.findAsDouble("key");
		assertEquals(d, 12.34, 0.05);
		EasyMock.verify(mock);
	}

	public void testFindAsInteger() {
		final ConfigurationEntity c = new ConfigurationEntity();
		c.setKey("key");
		c.setValue("12");
		c.setType(Integer.class.getName());
		list.add(c);
		EasyMock.expect(mock.findAll()).andReturn(list);
		EasyMock.replay(mock);
		impl.init();
		assertEquals(impl.findAsInteger("key"), Integer.valueOf(12));
		EasyMock.verify(mock);
	}

	public void testFindAsString() {
		final ConfigurationEntity c = new ConfigurationEntity();
		c.setKey("key");
		c.setValue("string");
		c.setType(String.class.getName());
		list.add(c);
		EasyMock.expect(mock.findAll()).andReturn(list);
		EasyMock.replay(mock);
		impl.init();
		assertEquals(impl.findAsString("key"), "string");
		EasyMock.verify(mock);
	}

	public void testFindAsFile() {
		final ConfigurationEntity c = new ConfigurationEntity();
		c.setKey("key");
		c.setValue("java.io.tmpdir");
		c.setType(String.class.getName());
		list.add(c);
		EasyMock.expect(mock.findAll()).andReturn(list);
		EasyMock.replay(mock);
		impl.init();
		assertNotNull(impl.findAsFile("key"));
		EasyMock.verify(mock);
	}

	public enum TestEnum {
		TEST1, TEST2
	};

	public void testFindAsEnum() {
		final ConfigurationEntity c = new ConfigurationEntity();
		c.setKey("key");
		c.setValue("TEST2");
		c.setType(TestEnum.class.getName());
		list.add(c);
		EasyMock.expect(mock.findAll()).andReturn(list);
		EasyMock.replay(mock);
		impl.init();
		assertEquals(impl.findAsEnum("key"), TestEnum.TEST2);
		EasyMock.verify(mock);
	}
}
