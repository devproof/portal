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
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author Carsten Hufe
 */
public class DevproofClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {
    public DevproofClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        super(registry, useDefaultFilters);
        addIncludeFilter(new AnnotationTypeFilter(DevproofPage.class));
        addIncludeFilter(new AnnotationTypeFilter(GenericRepository.class));
    }

    @Override
    protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
        try {
            Class<?> clazz = Class.forName(definitionHolder.getBeanDefinition().getBeanClassName());
            if (clazz.isAnnotationPresent(DevproofPage.class)) {
                DevproofPage annotation = clazz.getAnnotation(DevproofPage.class);
                RootBeanDefinition rbd = new RootBeanDefinition(PageConfiguration.class);
                MutablePropertyValues v = rbd.getPropertyValues();
                v.addPropertyValue("mountPath", annotation.mountPath());
                v.addPropertyValue("pageClass", clazz);
                BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(rbd, "helloWorld");
                super.registerBeanDefinition(beanDefinitionHolder, registry);
                System.out.println("Clazz found: " + clazz);
            } else if (clazz.isAnnotationPresent(GenericRepository.class)) {
                Type[] types = clazz.getGenericInterfaces();
                ParameterizedType type = (ParameterizedType) types[0];
                Class<?> entityClazz = (Class<?>) type.getActualTypeArguments()[0];
                GenericRepository annotation = clazz.getAnnotation(GenericRepository.class);
                BeanDefinition bd = BeanDefinitionBuilder.childBeanDefinition("baseGenericDao").addPropertyValue("daoInterface", clazz).addPropertyValue("entityClass", entityClazz).getBeanDefinition();
                BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(bd, annotation.name());
                super.registerBeanDefinition(beanDefinitionHolder, registry);
            } else {
                super.registerBeanDefinition(definitionHolder, registry);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        try {
            Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
            return super.isCandidateComponent(beanDefinition) || clazz.isAnnotationPresent(GenericRepository.class);
        } catch (ClassNotFoundException e) {
            // FIXME logger
            e.printStackTrace();
        }
        return super.isCandidateComponent(beanDefinition);
    }
}
