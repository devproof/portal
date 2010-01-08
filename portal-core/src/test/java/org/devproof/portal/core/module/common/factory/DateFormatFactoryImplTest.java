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
package org.devproof.portal.core.module.common.factory;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.easymock.EasyMock;
/**
 * 
 * @author Carsten Hufe
 */
public class DateFormatFactoryImplTest extends TestCase {
	private DateFormatFactoryImpl impl;
	private ConfigurationService configurationServiceMock;
	
	@Override
	protected void setUp() throws Exception {
		configurationServiceMock = EasyMock.createMock(ConfigurationService.class);
		impl = new DateFormatFactoryImpl();
		impl.setConfigurationService(configurationServiceMock);
	}

	public void testCreateDisplayDateFormat() {
		String pattern = "dd.MM.yyyy";
		SimpleDateFormat exptectedSdf = new SimpleDateFormat(pattern);
		EasyMock.expect(configurationServiceMock.findAsString("display_date_format")).andReturn(pattern);
		EasyMock.replay(configurationServiceMock);
		SimpleDateFormat sdf = impl.createDisplayDateFormat();
		Date date = new Date();
		assertEquals(exptectedSdf.format(date), sdf.format(date));
		EasyMock.verify(configurationServiceMock);
	}

	public void testCreateDisplayDateTimeFormat() {
		String pattern = "dd.MM.yyyy HH:mm";
		SimpleDateFormat exptectedSdf = new SimpleDateFormat(pattern);
		EasyMock.expect(configurationServiceMock.findAsString("display_date_time_format")).andReturn(pattern);
		EasyMock.replay(configurationServiceMock);
		SimpleDateFormat sdf = impl.createDisplayDateTimeFormat();
		Date date = new Date();
		assertEquals(exptectedSdf.format(date), sdf.format(date));
		EasyMock.verify(configurationServiceMock);
	}

	public void testCreateInputDateFormat() {
		String pattern = "dd-MM-yyyy";
		SimpleDateFormat exptectedSdf = new SimpleDateFormat(pattern);
		EasyMock.expect(configurationServiceMock.findAsString("input_date_format")).andReturn(pattern);
		EasyMock.replay(configurationServiceMock);
		SimpleDateFormat sdf = impl.createInputDateFormat();
		Date date = new Date();
		assertEquals(exptectedSdf.format(date), sdf.format(date));
		EasyMock.verify(configurationServiceMock);
	}

	public void testCreateInputDateTimeFormat() {
		String pattern = "dd-MM-yyyy HH:mm";
		SimpleDateFormat exptectedSdf = new SimpleDateFormat(pattern);
		EasyMock.expect(configurationServiceMock.findAsString("input_date_time_format")).andReturn(pattern);
		EasyMock.replay(configurationServiceMock);
		SimpleDateFormat sdf = impl.createInputDateTimeFormat();
		Date date = new Date();
		assertEquals(exptectedSdf.format(date), sdf.format(date));
		EasyMock.verify(configurationServiceMock);
	}

}
