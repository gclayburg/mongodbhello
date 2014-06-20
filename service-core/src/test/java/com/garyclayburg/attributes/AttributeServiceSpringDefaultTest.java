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

import com.garyclayburg.MongoInMemoryTestBase;
import com.garyclayburg.persistence.domain.User;
import com.garyclayburg.persistence.repository.UserStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * Date: 4/7/14
 * Time: 3:56 PM
 *
 * @author Gary Clayburg
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class AttributeServiceSpringDefaultTest extends MongoInMemoryTestBase {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(AttributeServiceSpringDefaultTest.class);

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    AttributeService attributeService;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    ScriptRunner scriptRunner;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    private UserStore auditedUserRepo;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() throws IOException, URISyntaxException {
        log.debug("Running test setUp: " + testName.getMethodName());
    }

    @After
    public void tearDown() throws Exception {
        log.debug("TearDown test: " + testName.getMethodName());

    }

    @Test
    public void testGeneratedUser() {
//        AttributeService attributeService = new AttributeService();
        User luke = new User();
        luke.setFirstname("Luke");
        luke.setLastname("Bryan");
        luke.setId("11223344");
        auditedUserRepo.save(luke);
        log.debug("luke is saved");
        GeneratedUser lukeFound = auditedUserRepo.findGeneratedUserByFirstname("Luke");
        log.debug("check attributes");
        assertEquals("Bryan",lukeFound.getLastname());
        log.debug("check another attribute");
        assertEquals("Luke Bryan",lukeFound.getAttribute("cn"));
    }

}