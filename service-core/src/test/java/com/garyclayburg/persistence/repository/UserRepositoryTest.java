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
import com.garyclayburg.data.ServiceConfig;
import com.garyclayburg.persistence.domain.User;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * Date: 3/20/14
 * Time: 1:20 PM
 *
 * @author Gary Clayburg
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceConfig.class,FongoMongoTestConfig.class})
@SpringApplicationConfiguration(classes = {BootUp.class})
public class UserRepositoryTest {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(UserRepositoryTest.class);

    @Rule
    public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb("demo-test");

    @SuppressWarnings("UnusedDeclaration")
    @Autowired
    private ApplicationContext applicationContext; // nosql-unit requirement

    @Autowired
    private AutoUserRepo autoUserRepo;

    @Test
    public void testSpringAutoWiredHelloWorld() throws Exception {
        assertTrue(true);
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
        assertEquals("Johan",johan.getFirstname());

    }

    @Test
    @UsingDataSet(locations = {"/one-user-field-mismatch.json"}, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testFindByEmailAutoCase() throws Exception {
        User none = autoUserRepo.findByEmail("Johan@nowhere.info");
        assertNull(none);
        User johan = autoUserRepo.findByEmailIgnoreCase("Johan@nowhere.info");
        assertEquals("Johan",johan.getFirstname());

    }

    @Test
    public void testAuditFields() throws Exception {
        User newUser = new User();
        newUser.setFirstname("Brad");
        newUser.setLastname("Paisley");
        autoUserRepo.save(newUser);

        User foundUser = autoUserRepo.findByFirstname("Brad");
        assertEquals("system user",foundUser.getCreatedBy());
    }

    @Test
    public void testQueryDSL() throws Exception {
        User user = new User();
        user.setFirstname("Collin");
        user.setFirstname("Raye");
        autoUserRepo.save(user);



    }
}
