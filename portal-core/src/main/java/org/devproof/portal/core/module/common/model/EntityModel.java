/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.common.model;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * @author Carsten Hufe
 */
public class EntityModel<T extends EntityId> extends LoadableDetachableModel<T> {
    private static final long serialVersionUID = 1L;

    @SpringBean(name = "hibernateTemplate")
    private HibernateTemplate hibernateTemplate;
    private Class<T> entityClass;
    private Integer entityId;

    @SuppressWarnings("unchecked")
    public EntityModel(T entity) {
        super(entity);
        entityId = entity.getId();
        entityClass = (Class<T>) entity.getClass();
        Injector.get().inject(this);
    }

    public EntityModel(Class<T> entityClass, Integer entityId) {
        super();
        this.entityClass = entityClass;
        this.entityId = entityId;
        Injector.get().inject(this);
    }

    @Override
    protected T load() {
        return hibernateTemplate.get(entityClass, entityId);
    }

    public static <T extends EntityId> EntityModel<T> of(T object) {
        return new EntityModel<T>(object);
    }
}
