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

import com.garyclayburg.MongoInMemoryTestBase;
import com.garyclayburg.persistence.repository.AutoUserRepo;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
/*
all context configuration must be done via spring boot(@SpringApplicationContext) and not regular
JUnit test context (@ContextConfiguration) so that spring boot web controllers can be found
 */
public class UserRestTest extends MongoInMemoryTestBase {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(UserRestTest.class);

    private MockMvc mockMvc;

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

    @Test
    public void testCreateRead() throws Exception {
        User gatlinBoys = new User();
        gatlinBoys.setFirstname("Tommy");
        gatlinBoys.setLastname("Yellow");
        autoUserRepo.save(gatlinBoys);
        mockMvc.perform(get("/visualusers/search/findByFirstname?firstname=Tommy"))
                .andDo(print())
                .andExpect(content().string(containsString("Tommy")));
    }

    @Test
    public void testAuditModifyDate() throws Exception {
        User hank = new User();
        hank.setFirstname("Hank");
        hank.setLastname("Williams");
        autoUserRepo.save(hank);
        User hankSaved = autoUserRepo.findByFirstname("Hank");
        DateTime lastModifiedDate = hankSaved.getLastModifiedDate();
        log.info("modified    : " + lastModifiedDate);

        //resave same user
        Thread.sleep(1500);
        autoUserRepo.save(hank);
        User hank2 = autoUserRepo.findByFirstname("Hank");
        DateTime lastModifiedNow = hank2.getLastModifiedDate();
        log.info("modified now: " + lastModifiedNow);

    }
}
