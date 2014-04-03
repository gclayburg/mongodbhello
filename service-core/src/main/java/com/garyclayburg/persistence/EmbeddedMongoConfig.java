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
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.RuntimeConfig;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.extract.UserTempNaming;

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
@EnableMongoAuditing
//@ComponentScan(basePackageClasses = {MongoConfig.class})
//@ComponentScan(basePackages = "com.garyclayburg.persistence")
public class EmbeddedMongoConfig extends AbstractMongoConfiguration {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(EmbeddedMongoConfig.class);
    private static final String LOCALHOST = "127.0.0.1";
    private static final String DB_NAME = "embedded-demo";
    private static final int MONGO_TEST_PORT = 27029;

    private static MongodProcess mongoProcess;
    private static Mongo mongo;

    @Override
    protected String getDatabaseName() {
        return "demo";
    }

//    @Bean
//    @Override
//    public Mongo mongo() throws Exception {
        /**
         *
         * this is for a single db
         */

        // return new Mongo();

        /**
         *
         * This is for a relset of db's
         */

//        return new MongoClient(new ArrayList<ServerAddress>() {{
//            add(new ServerAddress("127.0.0.1",27017));
//            add(new ServerAddress("127.0.0.1",27027));
//            add(new ServerAddress("127.0.0.1",27037));
//        }});

//    }

    @Bean
    @Override
    public Mongo mongo() throws Exception{
        RuntimeConfig config = new RuntimeConfig();
        config.setExecutableNaming(new UserTempNaming());

        MongodStarter starter = MongodStarter.getInstance(config);

        MongodExecutable mongoExecutable = starter.prepare(new MongodConfig(Version.V2_2_0,MONGO_TEST_PORT,false));
        mongoProcess = mongoExecutable.start();

        mongo = new MongoClient(LOCALHOST,MONGO_TEST_PORT);
        mongo.getDB(DB_NAME);


        log.info("register mongo client shutdown...");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log.info("shutting down mongo client...");
                mongo.close();
                log.info("client closed");
//                mongoProcess.stop();
//                log.info("server closed");
            }
        });


        return new MongoClient(new ArrayList<ServerAddress>() {{
            add(new ServerAddress(LOCALHOST,MONGO_TEST_PORT));
        }});
    }


    @Override
    protected String getMappingBasePackage() {
//        return "com.johnathanmarksmith.mongodb.example.domain";
        return "com.garyclayburg.persistence.domain";
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return new MongoAuditorUserProvider<String>();
    }


}
