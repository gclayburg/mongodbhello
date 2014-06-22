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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * Date: 6/21/14
 * Time: 6:57 PM
 *
 * @author Gary Clayburg
 */
public class AttributeServiceManualTest {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(AttributeServiceManualTest.class);
    private ScriptRunner scriptRunner;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() throws IOException, URISyntaxException {
        log.debug("Running test setUp: " + testName.getMethodName());
        URL groovyURL = this.getClass()
            .getClassLoader()
            .getResource("groovies/emptyscript.groovy");

        assert groovyURL != null;

        String scriptRoot = new File(groovyURL.toURI()).getParentFile()
            .getPath();
        scriptRunner = new ScriptRunner();
        scriptRunner.setRoot(new String[]{scriptRoot});

    }

    @After
    public void tearDown() {
        log.debug("Running test tearDown: " + testName.getMethodName());
    }


}