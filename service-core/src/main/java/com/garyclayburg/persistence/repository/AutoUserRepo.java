/*
 * VisualSync - a tool to visualize user data synchronization
 * Copyright (c) 2014 Gary Clayburg
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.garyclayburg.persistence.repository;

import com.garyclayburg.persistence.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by IntelliJ IDEA.
 * Date: 3/26/14
 * Time: 12:18 PM
 *
 * @author Gary Clayburg
 */
// http://localhost:8080/visualusers/search/findByUid?uid=500
@RepositoryRestResource(collectionResourceRel = "vusers",path = "visualusers")
public interface AutoUserRepo extends MongoRepository<User, String> {
    @SuppressWarnings("UnusedDeclaration")  //may be called via REST
    public User findByEmail(@Param("email") String email);

    @SuppressWarnings("UnusedDeclaration")  //may be called via REST
    public User findByEmailIgnoreCase(@Param("email") String email);

    @SuppressWarnings("UnusedDeclaration")  //may be called via REST
    public User findByUid(@Param("uid") String uid);

    @SuppressWarnings("UnusedDeclaration")  //may be called via REST
    public User findByFirstname(@Param("firstname") String firstName);
}
