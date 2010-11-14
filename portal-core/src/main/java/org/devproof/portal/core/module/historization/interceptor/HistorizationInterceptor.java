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
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.Serializable;
import java.util.Iterator;

/**
 * @author Carsten Hufe
 */
// TODO remove
public class HistorizationInterceptor extends EmptyInterceptor {
    private ApplicationContext applicationContext;

//    @Override
//    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
//        resolveAndHistorize(entity);
//        return false;
//    }




//    @Override
//    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
//        resolveAndHistorize(entity);
//        return false;
//    }
//
//    @Override
//    public void postFlush(Iterator entities) {
//        while(entities.hasNext()) {
//            Object entity = entities.next();
//            Class<? extends Object> clazz = entity.getClass();
//            if(clazz.isAnnotationPresent(Historize.class)) {
//                System.out.println("postFlush" + entity);
//                Historizer<Object, Object> historizer = resolveHistorizer(clazz);
//                historizer.historize(entity);
//            }
//        }
//        super.postFlush(entities);
//    }

    

    @Override
    public void afterTransactionCompletion(Transaction tx) {
        // TODO check caching for COnfiguration and remove hibernate chaching
        System.out.println("transaction" + tx);
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        Class<? extends Object> clazz = entity.getClass();
        if(clazz.isAnnotationPresent(Historize.class)) {
            Historizer<Object, Object> historizer = resolveHistorizer(clazz);
            historizer.deleteHistory(entity);
        }
    }
    
    private void resolveAndHistorize(Object entity) {
        Class<? extends Object> clazz = entity.getClass();
        if(clazz.isAnnotationPresent(Historize.class)) {
            Historizer<Object, Object> historizer = resolveHistorizer(clazz);
            historizer.historize(entity, null);
        }
    }

    @SuppressWarnings({"unchecked"})
    private Historizer<Object, Object> resolveHistorizer(Class<? extends Object> clazz) {
        Class<? extends Historizer> historizerType = clazz.getAnnotation(Historize.class).value();
        return (Historizer<Object, Object>) applicationContext.getBean(historizerType);
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

