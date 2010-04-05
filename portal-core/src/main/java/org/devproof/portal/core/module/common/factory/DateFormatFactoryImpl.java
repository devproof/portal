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
package org.devproof.portal.core.module.common.factory;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.Session;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Required;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Carsten Hufe
 */
public class DateFormatFactoryImpl implements DateFormatFactory {
    private ConfigurationService configurationService;

    @Override
    public SimpleDateFormat createDisplayDateFormat() {
        return createDateFormat("display_date_format");
    }

    @Override
    public SimpleDateFormat createDisplayDateTimeFormat() {
        return createDateFormat("display_date_time_format");
    }

    @Override
    public SimpleDateFormat createInputDateFormat() {
        return createDateFormat("input_date_format");
    }


    @Override
    public SimpleDateFormat createInputDateTimeFormat() {
        return createDateFormat("input_date_time_format");
    }

    @Required
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    private SimpleDateFormat createDateFormat(String formatKey) {
        Locale locale = Locale.getDefault();
        if (RequestCycle.get() != null && Session.get() != null && Session.get().getLocale() != null) {
            locale = Session.get().getLocale();
        }
        String format = configurationService.findAsString(formatKey);
        return new SimpleDateFormat(format, locale);
    }
}
