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

package com.garyclayburg.attributes;

import com.garyclayburg.ApplicationSettings;
import com.garyclayburg.persistence.domain.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by IntelliJ IDEA.
 * Date: 4/4/14
 * Time: 9:49 AM
 *
 * @author Gary Clayburg
 */
public class AttributeServiceMultipleClassTest {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(AttributeServiceMultipleClassTest.class);
    private ScriptRunner scriptRunner;
    private AttributeService attributeService;
    private User barney;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() throws Exception {
        log.debug("Running test setUp: " + testName.getMethodName());

    }

    @After
    public void tearDown() throws Exception {
        log.debug("TearDown test: " + testName.getMethodName());
    }

    private void setupAttributeService(URL groovyURL) throws URISyntaxException, IOException {
        assert groovyURL != null;

        String scriptRoot = new File(groovyURL.toURI()).getParentFile()
            .getPath();
        setServices(scriptRoot);
    }

    private void setServices(String scriptRoot) throws IOException {
        scriptRunner = new ScriptRunner();
        scriptRunner.setRoot(new String[]{scriptRoot});
        attributeService = new AttributeService();
        barney = new User();
        barney.setFirstname("Barney");
        barney.setLastname("Rubble");
        barney.setId("12345");
        attributeService.setScriptRunner(scriptRunner);
        attributeService.setPolicyChangeController(new PolicyChangeController());
        ApplicationSettings applicationSettingsMock = Mockito.mock(ApplicationSettings.class);
        when(applicationSettingsMock.isForceRecompileEntryPoints()).thenReturn(true);
        attributeService.setApplicationSettings(applicationSettingsMock);

    }

    @Test
    public void testMultipleClassFile() throws Exception {
        URL groovyURL = this.getClass()
            .getClassLoader()
            .getResource("groovies-multipleclassperfile/emptyscript.groovy");

        setupAttributeService(groovyURL);

        Map<String, String> generatedAttributes = attributeService.getGeneratedAttributes(barney);
        assertEquals(1,generatedAttributes.size());
    }

    @Test
    public void testReverseOrderMultipleClassFile() throws Exception {
        URL groovyURL = this.getClass()
            .getClassLoader()
            .getResource("groovies-reversemultipleclassperfile/emptyscript.groovy");

        setupAttributeService(groovyURL);

        Map<String, String> generatedAttributes = attributeService.getGeneratedAttributes(barney);
        assertEquals(1,generatedAttributes.size());

        Map<String, String> generatedAttributes1 =
            attributeService.getGeneratedAttributes(barney,"Active Directory BCT Domain");
        assertEquals(1,generatedAttributes1.size());

    }



}
