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

import com.garyclayburg.MongoInMemoryTestBase;
import com.garyclayburg.data.DBUser;
import com.garyclayburg.persistence.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * Date: 3/28/14
 * Time: 10:12 AM
 *
 * @author Gary Clayburg
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class CSVUserRepositoryTest extends MongoInMemoryTestBase {
    private static final Logger log = LoggerFactory.getLogger(CSVUserRepositoryTest.class);

    @Before
    public void setUp() throws URISyntaxException {
        log.debug("setUp() test method: " + this);
        userService.dropAllusers();
        importOneCSV("testusers.csv");
    }

    @Test
    public void testName() throws Exception {
        assertTrue(true);

    }

    @Test
    public void testImport2users() throws URISyntaxException {
        userService.dropAllusers();
        int numImported = importOneCSV("testusers.csv");
        assertEquals(2,numImported);

        log.info("find via userService");
        DBUser gclaybur = userService.getUserById("500");
        assertEquals("Gary",gclaybur.get("firstname"));
        log.info("count via userService");
        assertEquals(2,userService.countUsers());
        log.debug("db name from factory" + mongoDbFactory.getDb());

        log.info("Starting find...");
//        User garyUser = autoUserRepo.findByFirstName("Gary");
        User garyUser = autoUserRepo.findByUid("500");
        assertEquals("Gary",garyUser.getFirstname());

    }

    @Test
    public void testImportCaps() throws Exception {
        userService.dropAllusers();
        int numImported = importOneCSV("testusersCAPS.csv");
        assertEquals(2,numImported);
        DBUser gclaybur = userService.getUserById("500");
        assertEquals("Gary",gclaybur.get("firstname"));
        assertEquals(2,userService.countUsers());

        User garyUser = autoUserRepo.findByUid("500");
        assertEquals("Gary",garyUser.getFirstname());

    }

    private int importOneCSV(String csvFileName) throws URISyntaxException {
        URL testUsersCsv = this.getClass().getClassLoader().getResource(csvFileName);
        assert testUsersCsv != null;
        File csvInputFile = new File(testUsersCsv.toURI());
        return csvImporter.importFile(csvInputFile);
    }

}
