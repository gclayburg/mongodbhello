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

import com.garyclayburg.persistence.domain.User;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class UserStoreTest {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(UserStoreTest.class);

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {
        log.debug("Running test setUp: " + testName.getMethodName());
    }

    @Test
    @Ignore("requires customdomain 1.0b manually installed maven dependency")
    public void testSimpleReflections() throws Exception {
        Reflections reflections = new Reflections("com.acme");
        Set<Class<? extends User>> modules = reflections.getSubTypesOf(User.class);
        for (Class<? extends User> module : modules) {
            log.debug("name: " + module.getName());
        }
        assertEquals(1,modules.size());

    }

    @Test
    public void testFindGeneratedUserClass() throws Exception {
/*
        Reflections r = new Reflections(
            new ConfigurationBuilder()
                .filterInputsBy(new FilterBuilder().include("com.garyclayburg"))
                .setUrls(ClasspathHelper.forClassLoader())
                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner().filterResultsBy(myClassAnnotationsFilter)));


        Set<Class<? extends User>> modules = r.getSubTypesOf(User.class);
*/
    }

}