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
package org.devproof.portal.core.config.factory;

import org.devproof.portal.core.config.ModuleConfiguration;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ComponentScanBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * @author Carsten Hufe
 */
public class ModuleScanBeanDefinitionParser extends ComponentScanBeanDefinitionParser {
    private static final String BASE_PACKAGE_ATTRIBUTE = "base-package";
    private static final String MODULE_NAME_ATTRIBUTE = "module-name";
    private static final String AUTHOR_ATTRIBUTE = "author";
    private static final String AUTHOR_URL_ATTRIBUTE = "author-url";
    private static final String MODULE_VERSION_ATTRIBUTE = "module-version";
    private static final String PORTAL_VERSION_ATTRIBUTE = "portal-version";
    private ModuleConfiguration moduleConfiguration;


    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        moduleConfiguration = createModuleConfiguration(element);
        return super.parse(element, parserContext);
    }

    protected static ModuleConfiguration createModuleConfiguration(Element element) {
        ModuleConfiguration c = new ModuleConfiguration();
        c.setName(element.getAttribute(MODULE_NAME_ATTRIBUTE));
        c.setAuthor(element.getAttribute(AUTHOR_ATTRIBUTE));
        c.setUrl(element.getAttribute(AUTHOR_URL_ATTRIBUTE));
        c.setModuleVersion(element.getAttribute(MODULE_VERSION_ATTRIBUTE));
        c.setPortalVersion(element.getAttribute(PORTAL_VERSION_ATTRIBUTE));
        c.setBasePackage(element.getAttribute(BASE_PACKAGE_ATTRIBUTE));
        return c;
    }

    @Override
    protected ClassPathBeanDefinitionScanner createScanner(XmlReaderContext readerContext, boolean useDefaultFilters) {
        return new DevproofClassPathBeanDefinitionScanner(readerContext.getRegistry(), useDefaultFilters, moduleConfiguration);
    }
}
