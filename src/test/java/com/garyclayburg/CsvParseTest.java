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
import com.mongodb.Mongo;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.RuntimeConfig;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.extract.UserTempNaming;
import org.junit.*;
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
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {LocalServiceConfig.class})
@ContextConfiguration(classes = {ServiceConfig.class,CsvParseTest.FlapDoodleMongo.class})
//@ContextConfiguration(classes = {ServiceConfig.class,CsvParseTest.FongoMongoConfig.class})
public class CsvParseTest {

    private static final Logger log = LoggerFactory.getLogger(CsvParseTest.class);
    private static final String LOCALHOST = "127.0.0.1";
    private static final String DB_NAME = "itest";
    private static final int MONGO_TEST_PORT = 27028;

    @Autowired
    private ApplicationContext applicationContext; // nosql-unit requirement

    @Autowired
    private UserService userService;

    @Autowired
    private CsvImporter csvImporter;

    private static MongodProcess mongoProcess;
    private static Mongo mongo;

    @BeforeClass
    public static void initializeDB() throws IOException {

        RuntimeConfig config = new RuntimeConfig();
        config.setExecutableNaming(new UserTempNaming());

        MongodStarter starter = MongodStarter.getInstance(config);

        MongodExecutable mongoExecutable = starter.prepare(new MongodConfig(Version.V2_2_0,MONGO_TEST_PORT,false));
        mongoProcess = mongoExecutable.start();

        mongo = new Mongo(LOCALHOST,MONGO_TEST_PORT);
        mongo.getDB(DB_NAME);
    }

    @AfterClass
    public static void shutdownDB() throws InterruptedException {
        mongo.close();
        mongoProcess.stop();
    }

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
    static class FlapDoodleMongo {
        @Bean
        public Mongo mongo() throws UnknownHostException {
            mongo = new Mongo(LOCALHOST,MONGO_TEST_PORT);
            mongo.getDB(DB_NAME);
            return mongo;
        }
    }
}