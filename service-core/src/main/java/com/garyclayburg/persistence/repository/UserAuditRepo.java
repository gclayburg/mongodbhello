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
import com.garyclayburg.persistence.domain.UserAudit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 4/1/14
 * Time: 1:39 PM
 *
 * @author Gary Clayburg
 */
//todo add rest interface for audit?
@SuppressWarnings("UnusedDeclaration")  //implementation generated by Spring data
public interface UserAuditRepo extends MongoRepository<UserAudit, String>, QueryDslPredicateExecutor<UserAudit> {
    public UserAudit findByUser(User user);

    List<UserAudit> findById(String id);
}
