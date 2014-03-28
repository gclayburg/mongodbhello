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

import com.garyclayburg.BootUp;
import com.garyclayburg.persistence.MongoConfig;
import com.garyclayburg.persistence.domain.User;
import com.github.fakemongo.Fongo;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import com.mongodb.Mongo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by IntelliJ IDEA.
 * Date: 3/20/14
 * Time: 1:20 PM
 *
 * @author Gary Clayburg
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MongoConfig.class})
@SpringApplicationConfiguration(classes = BootUp.class)
public class UserRepositoryTest {
    private static final Logger log = LoggerFactory.getLogger(UserRepositoryTest.class);

    @Rule
    public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb("demo-test");

    @Autowired
    private ApplicationContext applicationContext; // nosql-unit requirement

    @SuppressWarnings("SpringJavaAutowiringInspection")  //IntelliJ gets confused by spring boot
    @Autowired private UserRepository userRepository;

    @SuppressWarnings("SpringJavaAutowiringInspection")  //IntelliJ gets confused by spring boot
    @Autowired private AutoUserRepo autoUserRepo;

    @Test
    public void testSpringAutoWiredHelloWorld() throws Exception {
    }

    @Test
    @UsingDataSet(locations = {"/one-user-field-mismatch.json"}, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testFindByEmailInvalid() throws Exception {
        assertNull(autoUserRepo.findByEmail("nonetobefound"));
    }

    @Test
    @UsingDataSet(locations = {"/one-user-field-mismatch.json"}, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testFindByEmailAuto() throws Exception {
        User johan = autoUserRepo.findByEmail("johan@nowhere.info");
        assertEquals("Johan",johan.getFirstName());

    }

    @Test
    @UsingDataSet(locations = {"/one-user-field-mismatch.json"}, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testFindByEmailAutoCase() throws Exception {
        User none = autoUserRepo.findByEmail("Johan@nowhere.info");
        assertNull(none);
        User johan = autoUserRepo.findByEmailIgnoreCase("Johan@nowhere.info");
        assertEquals("Johan",johan.getFirstName());

    }

    @Test
    @UsingDataSet(locations = {"/two-users.json"}, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testCount() {
        long total = userRepository.totalCount();
        assertEquals(2l,total);
    }

    @Test
    @UsingDataSet(locations = {"/one-user-field-mismatch.json"}, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testFieldMismatch() {
        long total = userRepository.totalCount();
        assertEquals(1l,total);
    }

    @Configuration
    @EnableMongoRepositories
    @ComponentScan(basePackageClasses = {UserRepository.class})
    // modified to not load configs from com.johnathanmarksmith.mongodb.example.MongoConfiguration
    static class PersonRepositoryTestConfiguration extends AbstractMongoConfiguration {

        @Override
        protected String getDatabaseName() {
            return "demo-test";
        }

        @Bean
        @Override
        public Mongo mongo() {
            // uses fongo for in-memory tests
            return new Fongo("mongo-test").getMongo();
        }

        @Override
        protected String getMappingBasePackage() {
            return "com.garyclayburg.persistence.domain";
//            return "com.johnathanmarksmith.mongodb.example.domain";
        }

    }
}
