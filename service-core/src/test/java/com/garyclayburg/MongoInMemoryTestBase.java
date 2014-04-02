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

import com.garyclayburg.data.UserService;
import com.garyclayburg.importer.CsvImporter;
import com.garyclayburg.persistence.repository.AutoUserRepo;
import com.garyclayburg.persistence.repository.FongoMongoTestConfig;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;

/**
 * Created by IntelliJ IDEA.
 * Date: 4/1/14
 * Time: 8:38 PM
 *
 * @author Gary Clayburg
 */
//@ContextConfiguration(classes = {BootUp.class,FongoMongoTestConfig.class})
@WebAppConfiguration
@SpringApplicationConfiguration(classes = {BootUp.class,FongoMongoTestConfig.class})
public class MongoInMemoryTestBase {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(MongoInMemoryTestBase.class);

    @Rule
    public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb("demo-test");

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    protected UserService userService;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    protected CsvImporter csvImporter;

    @Autowired
    @SuppressWarnings({"UnusedDeclaration","SpringJavaAutowiredMembersInspection"})
    protected ApplicationContext applicationContext; // nosql-unit requirement

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    protected AutoUserRepo autoUserRepo;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    protected MongoDbFactory mongoDbFactory;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    protected WebApplicationContext webApplicationContext;

}
