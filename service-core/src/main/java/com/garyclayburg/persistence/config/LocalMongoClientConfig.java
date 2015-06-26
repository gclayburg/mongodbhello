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

package com.garyclayburg.persistence.config;

import com.garyclayburg.persistence.MongoAuditorUserProvider;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 * Date: 3/21/14
 * Time: 4:21 PM
 *
 * @author Gary Clayburg
 */
@Configuration
//@EnableMongoRepositories
@EnableMongoRepositories(basePackages = "com.garyclayburg.persistence")  //make sure spring finds and creates implementation for spring data mongo interfaces in com.garyclayburg.persistence.repository
@EnableMongoAuditing
@Profile("mongolocal")
public class LocalMongoClientConfig extends AbstractMongoConfiguration{
    private static final Logger log = LoggerFactory.getLogger(LocalMongoClientConfig.class);

    @Value(value = "${mongoHost:localhost}")
    private String mongoHost;

    @Value(value = "${mongoPort:27017}")
    private int mongoPort;

    @Value(value = "${mongoUser:#{null}}")  //spring way of assigning null reference to mongoUser when commandline arguments are not specified
    private String mongoUser;

    @Value(value = "${mongoPassword:#{null}}")
    private String mongoPassword;

    @Override
    protected String getDatabaseName() {
        return "demo";
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception {
        log.info("configuring local mongo bean: "+ mongoHost +":"+mongoPort);
        MongoClient mongoClient = null;
        try {
            log.debug("mongoHost: " + mongoHost);
            log.debug("mongoPort: " + mongoPort);
            log.debug("mongoUser: " + mongoUser);
            /*
            if (mongoUser != null) {
                log.debug("using user/password authentication to mongodb");
                MongoCredential credential =
                    MongoCredential.createCredential(mongoUser,getDatabaseName(),mongoPassword.toCharArray());
                List<MongoCredential> credList = new ArrayList<MongoCredential>();
                credList.add(credential);
                mongoClient = new MongoClient(new ServerAddress(mongoHost,mongoPort),credList);
            } else{
            */
                log.debug("attempting no authentication to mongodb");
                mongoClient = new MongoClient(mongoHost,mongoPort);
            /*
            }
            */
        } catch (UnknownHostException e) {
            log.warn("kaboom",e);
        }
        return mongoClient;
    }

    @Override
    protected String getMappingBasePackage() {
        return "com.garyclayburg.persistence.domain";
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return new MongoAuditorUserProvider<String>();
    }

}
