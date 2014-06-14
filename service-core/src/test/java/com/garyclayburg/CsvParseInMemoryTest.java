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
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

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
public class CsvParseInMemoryTest extends MongoInMemoryTestBase {

    private static final Logger log = LoggerFactory.getLogger(CsvParseInMemoryTest.class);

    @Rule
    public TestName testName = new TestName();

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
        URL testUsersCsv = this.getClass().getClassLoader().getResource(csvFileName);
        assert testUsersCsv != null;
        File csvInputFile = new File(testUsersCsv.toURI());
        return csvImporter.importFile(csvInputFile);
    }
}