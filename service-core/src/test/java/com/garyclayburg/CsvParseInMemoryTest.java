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
import com.garyclayburg.data.User;
import com.garyclayburg.data.UserService;
import com.garyclayburg.importer.CsvImporter;
import com.github.fakemongo.Fongo;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import com.mongodb.Mongo;
import de.flapdoodle.embed.mongo.MongodProcess;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.<p>
 * This test uses an in-memory MongoDB Java server and MongoDB Java driver.  The test runs faster that a
 * FlapDoodle server, but the MongoDB Java driver imposes limitations on sub-classing BasicDBObject
 * http://stackoverflow.com/questions/15348022/java-lang-classcastexception-when-mapping-to-custom-object
 * <br>User: gclaybur
 * <br>Date: 3/6/14
 * <br>Time: 12:29 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceConfig.class,CsvParseInMemoryTest.FongoMongoConfig.class})
public class CsvParseInMemoryTest {

    private static final Logger log = LoggerFactory.getLogger(CsvParseInMemoryTest.class);

    @Rule
    public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb("demo-test");

    @Autowired
    private ApplicationContext applicationContext; // nosql-unit requirement

    @Autowired
    private UserService userService;

    @Autowired
    private CsvImporter csvImporter;

    private static MongodProcess mongoProcess;
    private static Mongo mongo;

    @Before
    public void setUp() throws URISyntaxException {
        log.debug("setUp() test method: " + this);
        userService.dropAllusers();
        importOneCSV("testusers.csv");
    }

    @After
    public void tearDown() {
        log.debug("teardown called for test " + this + "\n");
    }

    @Test
    public void testImport2users() throws URISyntaxException {
        userService.dropAllusers();
        int numImported = importOneCSV("testusers.csv");
        assertEquals(2,numImported);

        User gclaybur = userService.getUserById("500");
        assertEquals("Gary",gclaybur.get("firstname"));
        assertEquals(2,userService.countUsers());
    }

    @Test
    public void testUserModifyExisting() throws URISyntaxException {
        importOneCSV("testusers2.csv");
        User gclaybur = userService.getUserById("500");
        assertEquals("Gary",gclaybur.get("firstname"));
        assertEquals("IA",gclaybur.get("state"));
        assertEquals(2,userService.countUsers());
    }

    @Test
    public void testUserModifyExistingLimited() throws URISyntaxException {
        importOneCSV("testusersLimited.csv");
        User gclaybur = userService.getUserById("500");
        assertEquals("Gary",gclaybur.get("firstname"));
        assertEquals("homeplate",gclaybur.get("address")); //from testusersLimited.csv
        assertEquals("CO",gclaybur.get("state")); // from testusers.csv
        assertEquals(2,userService.countUsers());
    }

    private int importOneCSV(String csvFileName) throws URISyntaxException {
        URL testUsersCsv = this.getClass().getClassLoader().getResource(csvFileName);
        assert testUsersCsv != null;
        File csvInputFile = new File(testUsersCsv.toURI());
        return csvImporter.importFile(csvInputFile);
    }

    @Configuration
    static class FongoMongoConfig {
        @Bean
        public Mongo mongo() {
            // uses fongo for in-memory tests
            return new Fongo("mongo-test").getMongo();
        }
    }

}