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

package com.garyclayburg.persistence.domain;

import com.garyclayburg.BootUp;
import com.garyclayburg.persistence.repository.AutoUserRepo;
import com.garyclayburg.persistence.repository.FongoMongoTestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by IntelliJ IDEA.
 * Date: 3/30/14
 * Time: 11:53 AM
 *
 * @author Gary Clayburg
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
/*
all context configuration must be done via spring boot(@SpringApplicationContext) and not regular
JUnit test context (@ContextConfiguration) so that spring boot web controllers can be found
 */
@SpringApplicationConfiguration(classes = {BootUp.class,FongoMongoTestConfig.class})
public class UserRestTest {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(UserRestTest.class);

    private MockMvc mockMvc;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    WebApplicationContext webApplicationContext;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    AutoUserRepo autoUserRepo;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testName() throws Exception {
        assertTrue(true);

    }

    //http://localhost:8080/visualusers
    @Test
    public void testHelloRest() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("visualusers")));

    }
}
