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

package com.garyclayburg.persistence;

import com.mongodb.Mongo;
import com.mongodb.ServerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * Date: 3/20/14
 * Time: 1:36 PM
 *
 * @author Gary Clayburg
 */
@Configuration
@EnableMongoRepositories
//@ComponentScan(basePackageClasses = {MongoConfig.class})
@ComponentScan(basePackages = "com.garyclayburg.persistence")
public class MongoConfig extends AbstractMongoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(MongoConfig.class);

    @Override
    protected String getDatabaseName() {
        return "demo";
    }

//    @Bean
    @Override
    public Mongo mongo() throws Exception {
        /**
         *
         * this is for a single db
         */

        // return new Mongo();

        /**
         *
         * This is for a relset of db's
         */

        return new Mongo(new ArrayList<ServerAddress>() {{
            add(new ServerAddress("127.0.0.1",27017));
            add(new ServerAddress("127.0.0.1",27027));
            add(new ServerAddress("127.0.0.1",27037));
        }});

    }

    @Override
    protected String getMappingBasePackage() {
//        return "com.johnathanmarksmith.mongodb.example.domain";
        return "com.garyclayburg.persistence.domain";
    }

}
