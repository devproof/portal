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
package org.devproof.portal.module.bookmark.service;

import org.devproof.portal.module.bookmark.bean.DeliciousBean;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity;

import java.util.List;

/**
 * Methods to access and sync data from external
 * 
 * @author Carsten Hufe
 * 
 */
public interface SynchronizeService {
	/**
	 * Returns a the data from the account
	 * 
	 * @param username
	 *            del.icio.us username
	 * @param password
	 *            del.icio.us password
	 * @return returns a bean filled with the bookmarks from del.icio.us
	 */
	DeliciousBean getDataFromDelicious(String username, String password, String tags);

	/**
	 * Returns a list with new delicious bookmarks
	 * 
	 * @param bean
	 *            DeliciousBean from getDataFromDelicious
	 * @return List with new bookmarks
	 */
	List<BookmarkEntity> getNewDeliciousBookmarks(DeliciousBean bean);

	/**
	 * Returns a list with updated delicious bookmarks
	 * 
	 * @param bean
	 *            DeliciousBean from getDataFromDelicious
	 * @return List with modified bookmarks
	 */
	List<BookmarkEntity> getModifiedDeliciousBookmarks(DeliciousBean bean);

	/**
	 * Returns a list with delicious bookmarks which were removed
	 * 
	 * @param bean
	 *            DeliciousBean from getDataFromDelicious
	 * @return List with removed bookmarks
	 */
	List<BookmarkEntity> getRemovedDeliciousBookmarks(DeliciousBean bean);
}
