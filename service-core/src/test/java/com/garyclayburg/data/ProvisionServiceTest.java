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

import com.garyclayburg.persistence.MongoConfig;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;

/**
 * Created by IntelliJ IDEA.
 * User: gclaybur
 * Date: 3/13/14
 * Time: 11:12 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceConfig.class,MongoConfig.class})
public class ProvisionServiceTest {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(ProvisionServiceTest.class);

    @Autowired UserService userService;

    @Rule
    public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb("demo-test");

    @Autowired
    @SuppressWarnings("UnusedDeclaration")
    private ApplicationContext applicationContext; // nosql-unit requirement

    @Test
    @Ignore
    public void testName() throws Exception {
        ProvisionService provisionService = new ProvisionService();

        UserService userServiceMock = Mockito.mock(UserService.class);

//        when(userServiceMock.getUserById("500")).thenReturn()

//        UserService userService = new UserService();
        DBUser DBUser500 = userService.getUserById("500");

        DBUser u = new DBUser();
//        provisionService.provisionUser();

    }
}
