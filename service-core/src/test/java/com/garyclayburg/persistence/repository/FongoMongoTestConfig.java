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

import com.garyclayburg.persistence.MongoAuditorUserProvider;
import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Created by IntelliJ IDEA.
 * Date: 3/28/14
 * Time: 12:50 PM
 *
 * @author Gary Clayburg
 */

@Configuration
@EnableMongoRepositories
@EnableMongoAuditing
public class FongoMongoTestConfig extends AbstractMongoConfiguration {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(FongoMongoTestConfig.class);

    @Override
    protected String getDatabaseName() {
        return "demo-test"; // use db demo-test
    }

    @Bean
    @Override
    public Mongo mongo() {
        // uses fongo for in-memory tests
        log.info("configuring in-memory mongo");
        return new Fongo("fongo-mongo-test-from-FongoMongoTestConfig_class").getMongo();
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
