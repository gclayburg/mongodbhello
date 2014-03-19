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

import com.garyclayburg.data.User;
import com.garyclayburg.data.UserService;
import com.garyclayburg.importer.CsvImporter;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: gclaybur
 * Date: 3/6/14
 * Time: 12:29 PM
 */
public class CsvParseTestIntegration {

    private static final Logger log = LoggerFactory.getLogger(CsvParseTestIntegration.class);
    private UserService userService;

    @BeforeClass
    public static void switchOn() {
        log.debug("Switch on");
    }

    @AfterClass
    public static void switchOff() {
        log.debug("Switch off");
    }

    @Before
    public void setUp() throws URISyntaxException {
        log.debug("setUp() test method: " + this);
        userService = new UserService();
        userService.dropAllusers();
        importOneCSV("testusers.csv");
    }

    @After
    public void tearDown() {
        log.debug("teardown called for test " + this + "\n");
    }

    @Test
    public void testImport2users() throws URISyntaxException {
        UserService userService = new UserService();
        userService.dropAllusers();
        int numImported = importOneCSV("testusers.csv");
        assertEquals(2,numImported);

        User gclaybur = userService.getUserById("500");
        assertEquals("Gary",gclaybur.get("first name"));
        assertEquals(2,userService.countUsers());
    }

    @Test
    public void testUserModifyExisting() throws URISyntaxException {
        importOneCSV("testusers2.csv");
        User gclaybur = userService.getUserById("500");
        assertEquals("Gary",gclaybur.get("first name"));
        assertEquals("IA",gclaybur.get("state"));
        assertEquals(2,userService.countUsers());
    }

    @Test
    public void testUserModifyExistingLimited() throws URISyntaxException {
        importOneCSV("testusersLimited.csv");
        User gclaybur = userService.getUserById("500");
        assertEquals("Gary",gclaybur.get("first name"));
        assertEquals("homeplate",gclaybur.get("address")); //from testusersLimited.csv
        assertEquals("CO",gclaybur.get("state")); // from testusers.csv
        assertEquals(2,userService.countUsers());
    }

    private int importOneCSV(String csvFileName) throws URISyntaxException {
        CsvImporter csvImporter = new CsvImporter();
        URL testUsersCsv = this.getClass().getClassLoader().getResource(csvFileName);
        assert testUsersCsv != null;
        File csvInputFile = new File(testUsersCsv.toURI());
        return csvImporter.importFile(csvInputFile);
    }
}