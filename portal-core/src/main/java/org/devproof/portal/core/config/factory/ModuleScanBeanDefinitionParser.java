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

import org.devproof.portal.core.config.DevproofPage;
import org.devproof.portal.core.config.GenericRepository;
import org.devproof.portal.core.config.PageConfiguration;
import org.devproof.portal.core.module.box.dao.BoxDao;
import org.devproof.portal.core.module.box.entity.BoxEntity;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ChildBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ComponentScanBeanDefinitionParser;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.UUID;

/**
 * @author Carsten Hufe
 */
public class ModuleScanBeanDefinitionParser extends ComponentScanBeanDefinitionParser {
    private static final String BASE_PACKAGE_ATTRIBUTE = "base-package";

//    @Override
//    public BeanDefinition parse(Element element, ParserContext parserContext) {
//        String[] basePackages = StringUtils.tokenizeToStringArray(element.getAttribute(BASE_PACKAGE_ATTRIBUTE),
//				ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
//
//		// Actually scan for bean definitions and register them.
//		ClassPathBeanDefinitionScanner scanner = configureScanner(parserContext, element);
//		scanner.scan(basePackages);
//        return null;
//    }

    @Override
    protected ClassPathBeanDefinitionScanner createScanner(XmlReaderContext readerContext, boolean useDefaultFilters) {
        return new DevproofClassPathBeanDefinitionScanner(readerContext.getRegistry(), useDefaultFilters);
    }
}
