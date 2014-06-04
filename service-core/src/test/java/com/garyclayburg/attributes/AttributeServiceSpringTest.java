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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * Date: 4/7/14
 * Time: 3:56 PM
 *
 * @author Gary Clayburg
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class AttributeServiceSpringTest extends MongoInMemoryTestBase {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(AttributeServiceSpringTest.class);

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    AttributeService attributeService;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    ScriptRunner scriptRunner;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    private UserStore auditedUserRepo;

    @Before
    public void setUp() throws IOException, URISyntaxException {
        log.debug("setUp() test method: " + this);
        URL groovyURL = this.getClass()
                .getClassLoader()
                .getResource("groovies/emptyscript.groovy");

        assert groovyURL != null;

        String scriptRoot = new File(groovyURL.toURI()).getParentFile()
                .getPath();
        scriptRunner.setRoot(new String[]{scriptRoot});

        attributeService.setScriptRunner(scriptRunner);

    }

    @Test
    public void testGeneratedUser() {
//        AttributeService attributeService = new AttributeService();
        User luke = new User();
        luke.setFirstname("Luke");
        luke.setLastname("Bryan");
        luke.setId("11223345");
        auditedUserRepo.save(luke);

        GeneratedUser lukeFound = auditedUserRepo.findGeneratedUserByFirstname("Luke");

        assertEquals("Bryan",lukeFound.getLastname());
        assertEquals("Luke Bryan",lukeFound.getAttribute("cn"));
    }

}