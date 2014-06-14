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

import com.garyclayburg.data.DBUser;
import com.garyclayburg.data.UserService;
import com.garyclayburg.importer.CsvImporter;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.junit.*;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: gclaybur
 * Date: 3/6/14
 * Time: 12:29 PM
 */
@Ignore("runs too slow; duplicated by in-memory test")
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {LocalServiceConfig.class})
@SpringApplicationConfiguration(classes = {BootUp.class,CsvParseFlapDoodleTest.FlapDoodleMongo.class})
public class CsvParseFlapDoodleTest {

    private static final Logger log = LoggerFactory.getLogger(CsvParseFlapDoodleTest.class);
    private static final String LOCALHOST = "127.0.0.1";
    private static final String DB_NAME = "itest";
    private static final int MONGO_TEST_PORT = 27028;

    @SuppressWarnings({"UnusedDeclaration","SpringJavaAutowiredMembersInspection"})
    @Autowired
    private ApplicationContext applicationContext; // nosql-unit requirement

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private UserService userService;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private CsvImporter csvImporter;

    private static MongodProcess mongoProcess;
    private static Mongo mongo;

    @Rule
    public TestName testName = new TestName();


    @BeforeClass
    public static void initializeDB() throws IOException {

        /*
        RuntimeConfig config = new RuntimeConfig();
        config.setExecutableNaming(new UserTempNaming());

        MongodStarter starter = MongodStarter.getInstance(config);

        MongodExecutable mongoExecutable = starter.prepare(new MongodConfig(Version.V2_2_0,MONGO_TEST_PORT,false));
*/

        MongodStarter runtime = MongodStarter.getDefaultInstance();
        MongodExecutable mongoExecutable = runtime.prepare(new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                                                                   .net(new Net(MONGO_TEST_PORT,Network.localhostIsIPv6()))
                                                                   .build());

        mongoProcess = mongoExecutable.start();

        mongo = new MongoClient(LOCALHOST,MONGO_TEST_PORT);
        mongo.getDB(DB_NAME);
    }

    @AfterClass
    public static void shutdownDB() throws InterruptedException {
        mongo.close();
        mongoProcess.stop();
    }

    @Before
    public void setUp() throws URISyntaxException {
        log.debug("Running test setUp: " + testName.getMethodName());
        userService.dropAllusers();
        importOneCSV("testusers.csv");
    }

    @After
    public void tearDown() {
        log.debug("TearDown test: " + testName.getMethodName());
    }

    @Test
    public void testImport2users() throws URISyntaxException {
        userService.dropAllusers();
        int numImported = importOneCSV("testusers.csv");
        assertEquals(2,numImported);

        DBUser gclaybur = userService.getUserById("500");
        assertEquals("Gary",gclaybur.get("firstname"));
        assertEquals(2,userService.countUsers());
    }

    @Test
    public void testUserModifyExisting() throws URISyntaxException {
        importOneCSV("testusers2.csv");
        DBUser gclaybur = userService.getUserById("500");
        assertEquals("Gary",gclaybur.get("firstname"));
        assertEquals("IA",gclaybur.get("state"));
        assertEquals(2,userService.countUsers());
    }

    @Test
    public void testUserModifyExistingLimited() throws URISyntaxException {
        importOneCSV("testusersLimited.csv");
        DBUser gclaybur = userService.getUserById("500");
        assertEquals("Gary",gclaybur.get("firstname"));
        assertEquals("homeplate",gclaybur.get("address")); //from testusersLimited.csv
        assertEquals("CO",gclaybur.get("state")); // from testusers.csv
        assertEquals(2,userService.countUsers());
    }

    private int importOneCSV(String csvFileName) throws URISyntaxException {
        URL testUsersCsv = this.getClass()
                .getClassLoader()
                .getResource(csvFileName);
        assert testUsersCsv != null;
        File csvInputFile = new File(testUsersCsv.toURI());
        return csvImporter.importFile(csvInputFile);
    }

    @Configuration
    static class FlapDoodleMongo extends AbstractMongoConfiguration {
        @Override
        protected String getDatabaseName() { //userService gets the mongo DB from here
            return "demo-test";
        }

        @Bean
        public Mongo mongo() throws UnknownHostException {
            mongo = new MongoClient(LOCALHOST,MONGO_TEST_PORT);
            return mongo;
        }
    }
}