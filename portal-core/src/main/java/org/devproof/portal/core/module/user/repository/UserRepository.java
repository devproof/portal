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
package org.devproof.portal.core.module.user.repository;

import org.devproof.portal.core.config.GenericRepository;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.repository.CrudRepository;
import org.devproof.portal.core.module.role.entity.Role;
import org.devproof.portal.core.module.user.UserConstants;
import org.devproof.portal.core.module.user.entity.User;

import java.util.List;

/**
 * Queries for user stuff
 *
 * @author Carsten Hufe
 */
@GenericRepository("userRepository")
@CacheQuery(region = UserConstants.QUERY_CACHE_REGION)
public interface UserRepository extends CrudRepository<User, Integer> {
	@Query("select u from User u join fetch u.role r join fetch r.rights where u.username like ?")
    User findUserByUsername(String username);

	@Query("select u from User u where u.sessionId = ?")
    User findUserBySessionId(String sessionId);

	@Query("select u from User u where u.email like ?")
	List<User> findUserByEmail(String email);

	@Query("select count(u) from User u where u.username like ?")
	long existsUsername(String username);

	@Query("select count(u) from User u where u.role = ?")
	Long countUserForRole(Role role);

	@Query(value = "from User u where exists (from User eu join eu.role.rights as r where r.right = ? and eu = u)")
	List<User> findUserWithRight(String right);
}
