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

import com.garyclayburg.MongoInMemoryTestBase;
import com.garyclayburg.attributes.AttributeService;
import com.garyclayburg.attributes.ScriptRunner;
import com.garyclayburg.persistence.domain.User;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: gclaybur
 * Date: 3/13/14
 * Time: 11:12 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ProvisionServiceTest extends MongoInMemoryTestBase {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(ProvisionServiceTest.class);

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    ProvisionService provisionService;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    private AttributeService attributeService;

    @Rule
    public TestName testName = new TestName();


    @Before
    public void setUp() throws Exception {
        log.debug("Running test setUp: " + testName.getMethodName());

        URL groovyURL = this.getClass()
                .getClassLoader()
                .getResource("groovies/emptyscript.groovy");

        assert groovyURL != null;

        String scriptRoot = new File(groovyURL.toURI()).getParentFile()
                .getPath();
        ScriptRunner scriptRunner = new ScriptRunner();
        scriptRunner.setRoot(new String[]{scriptRoot});

        attributeService.setScriptRunner(scriptRunner);
    }

    @Test
    public void testName() throws Exception {
        User user = new User();
        user.setFirstname("Kenny");
        user.setLastname("Chesney");
        user.setId("20148");

        Map<String, String> generatedAttributes;

        generatedAttributes = attributeService.getGeneratedAttributes(user,"o2cusers.csv");
        assertEquals(0,generatedAttributes.size());

        generatedAttributes = attributeService.getGeneratedAttributes(user,"myAD");
        assertEquals(2,generatedAttributes.size());
    }
}
