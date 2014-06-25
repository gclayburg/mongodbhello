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
import org.junit.rules.TestName;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Created by IntelliJ IDEA.
 * Date: 6/25/14
 * Time: 10:48 AM
 *
 * @author Gary Clayburg
 */
public class AttributeServiceTestBase {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(AttributeServiceTestBase.class);
    protected ScriptRunner scriptRunner;
    protected AttributeService attributeService;
    protected User barney;
    protected PolicyChangeController policyChangeControllerMock;

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

    protected void setUpBeansWithRootFromClasspath(String sampleGroovyScript) throws URISyntaxException, IOException {
        URL groovyURL = this.getClass()
            .getClassLoader()
            .getResource(sampleGroovyScript);
        assertNotNull(groovyURL);
        String scriptRoot = new File(groovyURL.toURI()).getParentFile()
            .getPath();
        setUpBeans(scriptRoot);
    }

    protected void setUpBeans(String scriptRoot) throws IOException {
        scriptRunner = new ScriptRunner();
        scriptRunner.setRoot(new String[]{scriptRoot});
        attributeService = new AttributeService();
        barney = new User();
        barney.setFirstname("Barney");
        barney.setLastname("Rubble");
        barney.setId("12345");
        attributeService.setPolicyChangeController(new PolicyChangeController());
        policyChangeControllerMock = Mockito.mock(PolicyChangeController.class);
        attributeService.setPolicyChangeController(policyChangeControllerMock);

        ApplicationSettings applicationSettingsMock = Mockito.mock(ApplicationSettings.class);
        when(applicationSettingsMock.isForceRecompileEntryPoints()).thenReturn(true);
        attributeService.setApplicationSettings(applicationSettingsMock);
        attributeService.setScriptRunner(scriptRunner);

    }
}
