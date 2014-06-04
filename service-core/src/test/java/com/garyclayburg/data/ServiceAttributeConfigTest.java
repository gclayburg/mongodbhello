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

package com.garyclayburg.data;

import groovy.lang.GroovyClassLoader;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

public class ServiceAttributeConfigTest {
    private static final Logger log = LoggerFactory.getLogger(ServiceAttributeConfigTest.class);

    @Test
    public void testLoadStream() throws Exception {
        ClassLoader parent = getClass().getClassLoader();
        String scriptName = "embeddedgroovy/com/embedded/DefaultAttributes.groovy";
        InputStream groovyIS = parent.getResourceAsStream(scriptName);

        GroovyClassLoader loader = new GroovyClassLoader(parent);
        Class parseClass = loader.parseClass(groovyIS,scriptName);
        assertNotNull(parseClass);
    }
}