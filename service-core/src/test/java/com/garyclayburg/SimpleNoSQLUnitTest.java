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

package com.garyclayburg;

import com.garyclayburg.data.ServiceConfig;
import com.garyclayburg.data.UserService;
import com.garyclayburg.persistence.repository.DummyUserRepository;
import com.garyclayburg.persistence.repository.FongoMongoTestConfig;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import static org.junit.Assert.assertEquals;

/**
 * The purpose of this test is to show nosqlunit + in memory mongo server + basic spring
 * autowired components using JavaConfig style
 * <br></br>Created by IntelliJ IDEA.
 * Date: 3/22/14
 * Time: 9:16 AM
 *
 * @author Gary Clayburg
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceConfig.class,FongoMongoTestConfig.class,DummyUserRepository.class})
public class SimpleNoSQLUnitTest {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(SimpleNoSQLUnitTest.class);
    public static final String DEMO_TEST = "demo-test";
    @Autowired
    private DummyUserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    @SuppressWarnings("UnusedDeclaration")
    private ApplicationContext applicationContext; // nosql-unit requirement

    @Rule
    public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb(DEMO_TEST);

    @Test
    @UsingDataSet(locations = {"/two-users.json"},loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testCount() {

        long total = userRepository.countUnderAgeTmp(DEMO_TEST);
        assertEquals(2l,total);
    }

}