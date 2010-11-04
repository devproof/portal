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
package org.devproof.portal.core.module.tag.service;

import org.apache.commons.lang.UnhandledException;
import org.devproof.portal.core.module.role.entity.Role;
import org.devproof.portal.core.module.tag.entity.AbstractTag;
import org.devproof.portal.core.module.tag.repository.TagRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Carsten Hufe
 */
public abstract class AbstractTagServiceImpl<T extends AbstractTag<?>> implements TagService<T> {
    private TagRepository<T> tagRepository;

    @Override
    @Transactional
    public void deleteUnusedTags() {
        tagRepository.deleteUnusedTags();
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findMostPopularTags(Integer firstResult, Integer maxResult) {
        return tagRepository.findMostPopularTags(firstResult, maxResult);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findMostPopularTags(Role role, Integer firstResult, Integer maxResult) {
        return tagRepository.findMostPopularTags(role, getRelatedTagRight(), firstResult, maxResult);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findTagsStartingWith(String prefix) {
        return tagRepository.findTagsStartingWith(prefix);
    }

    @Override
    public T newTagEntity(String tag) {
        T obj;
        try {
            obj = tagRepository.getType().newInstance();
        } catch (InstantiationException e) {
            throw new UnhandledException(e);
        } catch (IllegalAccessException e) {
            throw new UnhandledException(e);
        }
        obj.setTagname(tag);
        return obj;
    }

    public abstract String getRelatedTagRight();

    @Override
    @Transactional
    public void delete(T entity) {
        tagRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public T findById(String id) {
        return tagRepository.findById(id);
    }

    @Override
    @Transactional
    public void save(T entity) {
        tagRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public T findByIdAndCreateIfNotExists(String tagName) {
        T tag = findById(tagName);
        if (tag == null) {
            tag = newTagEntity(tagName);
            save(tag);
        }
        return tag;
    }

    public void setTagRepository(TagRepository<T> tagRepository) {
        this.tagRepository = tagRepository;
    }
}
