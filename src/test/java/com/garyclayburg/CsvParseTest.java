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
public class CsvParseTest {

    private static final Logger log = LoggerFactory.getLogger(CsvParseTest.class);

    @BeforeClass
    public static void switchOn() {
        log.debug("Switch on");
    }

    @AfterClass
    public static void switchOff() {
        log.debug("Switch off");
    }

    @Before
    public void setUp() {
        log.debug("setUp() test method: " + this);
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
        UserService userService = new UserService();
        userService.dropAllusers();
        importOneCSV("testusers.csv");
        importOneCSV("testusers2.csv");
        User gclaybur = userService.getUserById("500");
        assertEquals("Gary",gclaybur.get("first name"));
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