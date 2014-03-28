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

package com.garyclayburg.data;

import com.mongodb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: gclaybur
 * Date: 3/12/14
 * Time: 12:59 PM
 */
@Component
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private DBCollection collection;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private Mongo mongoClient;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private MongoDbFactory mongoDbFactory;

    public UserService() {
    }

    public DBUser getUserById(String id) {
        DBUser existingDBUser = null;
        BasicDBObject searchQuery = createUserQuery(id);
        DBObject one = getCollection().findOne(searchQuery);
        if (one != null) {
            existingDBUser = new DBUser(one.toMap()); //MongoDB java driver does not support a direct cast to User
        }
        return existingDBUser;
    }

    private BasicDBObject createUserQuery(String id) {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("uid",id);
        return searchQuery;
    }

    public void dropAllusers() {
        getCollection().drop();
    }

    public long countUsers() {
        return getCollection().count();
    }

    public void saveUser(BasicDBObject document,String uidToMatch) {
        log.info("writing doc");
        Date now = new Date();
        document.put("createDate",now);
        document.put("modifiedDate",now);
//        document.put("_class","com.garyclayburg.persistence.domain.User"); //spring Data needs this to auto-match find() to this class

        BasicDBObject updateObj = new BasicDBObject();
        updateObj.put("$set",document);  //make sure we don't just replace existing user document

        BasicDBObject searchQuery = createUserQuery(uidToMatch);
        getCollection().update(searchQuery,updateObj,true,false);
    }

    public void setMongoClient(Mongo mongoClient) {
        this.mongoClient = mongoClient;
    }

    private DBCollection getCollection() {
        if (collection == null) { //initialize collection only after all spring beans are autowired
            log.debug("available database names on server: " + mongoClient.getDatabaseNames());

            log.debug("db name from factory: " + mongoDbFactory.getDb());
            DB db1;
            db1 = mongoDbFactory.getDb();
            collection = db1.getCollection("user");
            collection.setObjectClass(DBUser.class);

        }
        return collection;
    }
}
