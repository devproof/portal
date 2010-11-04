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
package org.devproof.portal.core.module.configuration.service;

import org.devproof.portal.core.module.configuration.dao.ConfigurationRepository;
import org.devproof.portal.core.module.configuration.entity.ConfigurationEntity;
import org.devproof.portal.core.module.configuration.registry.ConfigurationRegistryImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author Carsten Hufe
 */
public class ConfigurationServiceImplTest {
    private ConfigurationServiceImpl impl;
    private ConfigurationRepository mock;
    private List<ConfigurationEntity> list;

    @Before
    public void setUp() throws Exception {
        mock = createStrictMock(ConfigurationRepository.class);
        impl = new ConfigurationServiceImpl();
        impl.setConfigurationDao(mock);
        impl.setConfigurationRegistry(new ConfigurationRegistryImpl());
        list = new ArrayList<ConfigurationEntity>();
        ConfigurationEntity c = new ConfigurationEntity();
        c.setKey("input_date_format");
        c.setValue("dd-mm-yyyy");
        c.setType(String.class.getName());
        list.add(c);
    }

    @Test
    public void testSave() {
        ConfigurationEntity e = new ConfigurationEntity();
        e.setKey("foo");
        e.setValue("bar");
        expect(mock.save(e)).andReturn(e);
        replay(mock);
        impl.save(e);
        verify(mock);
    }

    @Test
    public void testDelete() {
        ConfigurationEntity e = new ConfigurationEntity();
        e.setKey("foo");
        e.setValue("bar");
        mock.delete(e);
        replay(mock);
        impl.delete(e);
        verify(mock);
    }

    @Test
    public void testFindById() {
        ConfigurationEntity e = new ConfigurationEntity();
        e.setKey("foo");
        e.setValue("bar");
        expect(mock.findById("foo")).andReturn(e);
        replay(mock);
        assertEquals(impl.findById("foo"), e);
        verify(mock);
    }

    @Test
    public void testFindAll() {
        List<ConfigurationEntity> list = new ArrayList<ConfigurationEntity>();
        list.add(new ConfigurationEntity());
        list.add(new ConfigurationEntity());
        expect(mock.findAll()).andReturn(list);
        replay(mock);
        assertEquals(list, impl.findAll());
        verify(mock);
    }

    @Test
    public void testFindConfigurationGroups() {
        List<String> list = new ArrayList<String>();
        list.add("group1");
        list.add("group2");
        expect(mock.findConfigurationGroups()).andReturn(list);
        replay(mock);
        assertEquals(list, impl.findConfigurationGroups());
        verify(mock);
    }

    @Test
    public void testFindConfigurationsByGroup() {
        List<ConfigurationEntity> list = new ArrayList<ConfigurationEntity>();
        list.add(new ConfigurationEntity());
        list.add(new ConfigurationEntity());
        expect(mock.findConfigurationsByGroup("group")).andReturn(list);
        replay(mock);
        assertEquals(list, impl.findConfigurationsByGroup("group"));
        verify(mock);
    }

    @Test
    public void testFindAsObject() {
        ConfigurationEntity c = new ConfigurationEntity();
        c.setKey("key");
        c.setValue("value");
        c.setType(String.class.getName());
        list.add(c);
        expect(mock.findAll()).andReturn(list);
        replay(mock);
        impl.init();
        assertNotNull(impl.findAsObject("key"));
        verify(mock);
    }

    @Test
    public void testFindAsBoolean() {
        ConfigurationEntity c = new ConfigurationEntity();
        c.setKey("key");
        c.setValue("true");
        c.setType(Boolean.class.getName());
        list.add(c);
        expect(mock.findAll()).andReturn(list);
        replay(mock);
        impl.init();
        assertTrue(impl.findAsBoolean("key"));
        verify(mock);
    }

    @Test
    public void testFindAsDate() {
        ConfigurationEntity c = new ConfigurationEntity();
        c.setKey("key");
        c.setValue("15-01-2008");
        c.setType(Date.class.getName());
        list.add(c);
        expect(mock.findAll()).andReturn(list);
        replay(mock);
        impl.init();
        assertNotNull(impl.findAsDate("key"));
        verify(mock);
    }

    @Test
    public void testFindAsDouble() {
        ConfigurationEntity c = new ConfigurationEntity();
        c.setKey("key");
        c.setValue("12.34");
        c.setType(Double.class.getName());
        list.add(c);
        expect(mock.findAll()).andReturn(list);
        replay(mock);
        impl.init();
        double d = impl.findAsDouble("key");
        assertEquals(d, 12.34, 0.05);
        verify(mock);
    }

    @Test
    public void testFindAsInteger() {
        ConfigurationEntity c = new ConfigurationEntity();
        c.setKey("key");
        c.setValue("12");
        c.setType(Integer.class.getName());
        list.add(c);
        expect(mock.findAll()).andReturn(list);
        replay(mock);
        impl.init();
        assertEquals(impl.findAsInteger("key"), Integer.valueOf(12));
        verify(mock);
    }

    @Test
    public void testFindAsString() {
        ConfigurationEntity c = new ConfigurationEntity();
        c.setKey("key");
        c.setValue("string");
        c.setType(String.class.getName());
        list.add(c);
        expect(mock.findAll()).andReturn(list);
        replay(mock);
        impl.init();
        assertEquals(impl.findAsString("key"), "string");
        verify(mock);
    }

    @Test
    public void testFindAsFile() {
        ConfigurationEntity c = new ConfigurationEntity();
        c.setKey("key");
        c.setValue("java.io.tmpdir");
        c.setType(String.class.getName());
        list.add(c);
        expect(mock.findAll()).andReturn(list);
        replay(mock);
        impl.init();
        assertNotNull(impl.findAsFile("key"));
        verify(mock);
    }

    public enum TestEnum {
        TEST1, TEST2
    }

    @Test
    public void testFindAsEnum() {
        ConfigurationEntity c = new ConfigurationEntity();
        c.setKey("key");
        c.setValue("TEST2");
        c.setType(TestEnum.class.getName());
        list.add(c);
        expect(mock.findAll()).andReturn(list);
        replay(mock);
        impl.init();
        assertEquals(impl.findAsEnum("key"), TestEnum.TEST2);
        verify(mock);
    }
}
