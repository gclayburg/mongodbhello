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

import java.net.UnknownHostException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: gclaybur
 * Date: 3/12/14
 * Time: 12:59 PM
 */
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final DBCollection collection;

    public UserService() {
        MongoClient mongoclient;
        DB db1 = null;
        try {
            mongoclient = new MongoClient("localhost",27017);
            db1 = mongoclient.getDB("IowaState");
        } catch (UnknownHostException e) {  //todo fix exception handling - maybe after convert to spring?
            log.warn("kaboom",e);
        }
        collection = db1.getCollection("userstore");
        collection.setObjectClass(User.class);
    }

    public User getUserById(String id) {
        BasicDBObject searchQuery = createUserQuery(id);
        return (User) collection.findOne(searchQuery);
    }

    private BasicDBObject createUserQuery(String id) {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("uid",id);
        return searchQuery;
    }

    public void dropAllusers() {
        collection.drop();
    }
    public long countUsers(){
        return collection.count();
    }

    public void saveUser(BasicDBObject document,String uidToMatch) {
        log.info("writing doc");
        Date now = new Date();
        document.put("createDate",now);
        document.put("modifiedDate",now);

        BasicDBObject updateObj = new BasicDBObject();
        updateObj.put("$set",document);  //make sure we don't just replace existing user document

        BasicDBObject searchQuery = createUserQuery(uidToMatch);
        collection.update(searchQuery,updateObj,true,false);
    }
}
