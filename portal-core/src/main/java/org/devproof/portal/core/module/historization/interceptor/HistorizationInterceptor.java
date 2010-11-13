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
package org.devproof.portal.core.module.historization.interceptor;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.io.Serializable;

/**
 * @author Carsten Hufe
 */
// TODO unit test
public class HistorizationInterceptor extends EmptyInterceptor {
    private ApplicationContext applicationContext;
    private HibernateTemplate hibernateTemplate;
    
    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        System.out.println("dirty " + entity + " " + currentState[0]);
        Class<? extends Object> clazz = entity.getClass();
        if(clazz.isAnnotationPresent(Historize.class)) {
            Class<? extends Historizer> historizerType = clazz.getAnnotation(Historize.class).value();
            @SuppressWarnings({"unchecked"})
            Historizer<Object, Object>  historizer = applicationContext.getBean(historizerType);
            Object o = historizer.historize(entity);
            hibernateTemplate.save(o);
        }
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        // TODO
        System.out.println("saved " + entity);
        return super.onSave(entity, id, state, propertyNames, types);
    }

    

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        // TODO delete history
        super.onDelete(entity, id, state, propertyNames, types);
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }
}

