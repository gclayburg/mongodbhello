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
        Date now = new Date();
        document.put("createDate",now);
        document.put("modifiedDate",now);

        BasicDBObject searchQuery = createUserQuery(uidToMatch);
        collection.update(searchQuery,document,true,false);
    }
}
