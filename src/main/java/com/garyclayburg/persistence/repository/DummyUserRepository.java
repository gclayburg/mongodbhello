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

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by IntelliJ IDEA.
 * Date: 3/20/14
 * Time: 1:20 PM
 *
 * @author Gary Clayburg
 */
@Repository
public class DummyUserRepository {
    private static final Logger log = LoggerFactory.getLogger(DummyUserRepository.class);

    @Autowired Mongo mongoClient;

    public DummyUserRepository() {
    }

    /**
     * Retrieves basic metadata without using springdata support
     *
     * @param databaseName
     *
     * @return
     */
    public long countUnderAgeTmp(String databaseName) {
        DB db = mongoClient.getDB(databaseName);
        DBCollection collection = db.getCollection("user");
        return collection.count();
    }
}
