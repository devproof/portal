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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Page;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.devproof.portal.core.config.*;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.persistence.Entity;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Scans and registers the devproof stuff in spring way
 *
 * @author Carsten Hufe
 */
public class DevproofClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {
    private final Log logger = LogFactory.getLog(getClass());
    private BeanDefinitionRegistry registry;
    private ModuleConfiguration moduleConfiguration;

    public DevproofClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, ModuleConfiguration moduleConfiguration) {
        super(registry, useDefaultFilters);
        this.registry = registry;
        this.moduleConfiguration = moduleConfiguration;
        addIncludeFilter(new AnnotationTypeFilter(ModulePage.class));
        addIncludeFilter(new AnnotationTypeFilter(NavigationBox.class));
        addIncludeFilter(new AnnotationTypeFilter(GenericRepository.class));
        addIncludeFilter(new AnnotationTypeFilter(RegisterGenericDataProvider.class));
        addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        addIncludeFilter(new AnnotationTypeFilter(org.hibernate.annotations.Entity.class));
        registerModuleConfigurationAsSingleton();
    }

    private void registerModuleConfigurationAsSingleton() {
        ConfigurableListableBeanFactory f = (ConfigurableListableBeanFactory)registry;
         String basePackage = moduleConfiguration.getBasePackage();
        if(f.containsBean(basePackage)) {
            throw new IllegalStateException("You tried to register a devproof module twice with the same base-package: " + basePackage + "!");   
        }
        f.registerSingleton(basePackage, moduleConfiguration);
    }

    @Override
    protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
        try {
            @SuppressWarnings({"unchecked"})
            Class<? extends Page> clazz = (Class<? extends Page>) Class.forName(definitionHolder.getBeanDefinition().getBeanClassName());
            if (clazz.isAnnotationPresent(ModulePage.class)) {
                moduleConfiguration.addPageConfiguration(new PageConfiguration(clazz));
            }
            else if (clazz.isAnnotationPresent(NavigationBox.class)) {
                moduleConfiguration.addBox(new BoxConfiguration(clazz));
            }
            else if (clazz.isAnnotationPresent(GenericRepository.class)) {
                BeanDefinitionHolder beanDefinitionHolder = buildGenericRepositoryDefinition(clazz);
                super.registerBeanDefinition(beanDefinitionHolder, registry);
            }
            else if (clazz.isAnnotationPresent(RegisterGenericDataProvider.class)) {
                BeanDefinitionHolder beanDefinitionHolder = buildGenericDataProviderDefinition(clazz);
                super.registerBeanDefinition(beanDefinitionHolder, registry);
            }
            else if (clazz.isAnnotationPresent(Entity.class)
                    || clazz.isAnnotationPresent(org.hibernate.annotations.Entity.class)) {
                moduleConfiguration.addEntity(clazz);
            }
            else {
                super.registerBeanDefinition(definitionHolder, registry);
            }

        } catch (ClassNotFoundException e) {
            logger.fatal(e);
        }
    }

    private BeanDefinitionHolder buildGenericRepositoryDefinition(Class<?> clazz) {
        Class<?> entityClazz = getEntityClazz(clazz);
        GenericRepository annotation = clazz.getAnnotation(GenericRepository.class);
        BeanDefinition bd = BeanDefinitionBuilder.childBeanDefinition("baseGenericDao").addPropertyValue("daoInterface", clazz).addPropertyValue("entityClass", entityClazz).getBeanDefinition();
        return new BeanDefinitionHolder(bd, annotation.value());
    }

    private BeanDefinitionHolder buildGenericDataProviderDefinition(Class<?> clazz) {
        Class<?> entityClazz = clazz;
        Class<?> queryClazz = getQueryClazz(clazz);
        RegisterGenericDataProvider annotation = clazz.getAnnotation(RegisterGenericDataProvider.class);
        BeanDefinition bd = BeanDefinitionBuilder.childBeanDefinition("persistenceDataProvider")
                .setScope(BeanDefinition.SCOPE_PROTOTYPE)
                .addPropertyValue("sort", new SortParam(annotation.sortProperty(), annotation.sortAscending()))
                .addPropertyValue("queryClass", queryClazz)
                .addPropertyValue("entityClass", entityClazz)
                .addPropertyValue("countQuery", annotation.countQuery())
                .addPropertyValue("prefetch", Arrays.asList(annotation.prefetch()))
                .getBeanDefinition();
        return new BeanDefinitionHolder(bd, annotation.value());
    }

    /**
     * Entity class is always the first generic type
     * @param clazz type
     * @return entity class
     */
    private Class<?> getEntityClazz(Class<?> clazz) {
        Type[] types = clazz.getGenericInterfaces();
        ParameterizedType type = (ParameterizedType) types[0];
        return (Class<?>) type.getActualTypeArguments()[0];
    }

    /**
     * Query class is always the second generic type
     * @param clazz type
     * @return query class
     */
    private Class<?> getQueryClazz(Class<?> clazz) {
        Type[] types = clazz.getGenericInterfaces();
        ParameterizedType type = (ParameterizedType) types[0];
        Type[] actualTypeArguments = type.getActualTypeArguments();
        if(actualTypeArguments.length >= 2) {
            return (Class<?>) actualTypeArguments[1];
        }
        return null;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        try {
            Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
            return super.isCandidateComponent(beanDefinition)
                    || clazz.isAnnotationPresent(GenericRepository.class)
                    || clazz.isAnnotationPresent(RegisterGenericDataProvider.class);
        } catch (ClassNotFoundException e) {
            logger.fatal(e);
        }
        return super.isCandidateComponent(beanDefinition);
    }
}
