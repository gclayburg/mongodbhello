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

import com.garyclayburg.MongoInMemoryTestBase;
import com.garyclayburg.persistence.domain.QUser;
import com.garyclayburg.persistence.domain.QUserAudit;
import com.garyclayburg.persistence.domain.User;
import com.garyclayburg.persistence.domain.UserAudit;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by IntelliJ IDEA.
 * Date: 4/1/14
 * Time: 11:21 AM
 *
 * @author Gary Clayburg
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class AuditedUserRepoTest extends MongoInMemoryTestBase {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(AuditedUserRepoTest.class);
    private MockMvc mockMvc;

    @Qualifier("auditedUserRepo")
    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    private UserStore auditedUserRepo;

    @SuppressWarnings({"SpringJavaAutowiringInspection","SpringJavaAutowiredMembersInspection"})
    @Autowired
    private UserAuditRepo userAuditRepo;
    private User hank;
    private User hankAgain;

    @Rule
    public TestName testName = new TestName();


    @Before
    public void setUp() throws Exception {
        log.debug("Running test setUp: " + testName.getMethodName());
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        autoUserRepo.deleteAll();
        userAuditRepo.deleteAll();

        hank = new User();
        hank.setFirstname("Hank");
        hank.setLastname("Williams");
        hank.setUid("1234");

        // same name, differenet objects, possibly different mongo _id (@Id)
        hankAgain = new User();
        hankAgain.setFirstname("Hank");
        hankAgain.setLastname("Williams");
        hankAgain.setUid("1234");

    }

    @Test
    public void testName() throws Exception {
        assertTrue(true);

    }

    @Test
    public void testHelloRest() throws Exception {
        autoUserRepo.save(hank);

        mockMvc.perform(get("/audited-users/findByFirstname?firstname=Hank"))
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Williams")));

    }

    @Test
    public void testSave() throws Exception {
        auditedUserRepo.save(hank);
        User hankFound = auditedUserRepo.findByFirstname("Hank");
        assertEquals("Williams",hankFound.getLastname());
        assertEquals(1,autoUserRepo.count());
        assertEquals(1,userAuditRepo.count());
    }

    @Test
    public void testSearchAudit() throws Exception {
        auditedUserRepo.save(hank);

        List<UserAudit> hankAuditEntry = userAuditRepo.findByUser_Id(hank.getId());
        assertEquals("Williams",hankAuditEntry.get(0)
                .getUser()
                .getLastname());

        hankAgain = new User();
        hankAgain.setId(hank.getId()); //query must contain id of saved user
        log.debug("hank id: "+hank.getId());
        List<UserAudit> hankAgainAuditEntry = userAuditRepo.findByUser_Id(hankAgain.getId());
        assertEquals("Williams",hankAgainAuditEntry.get(0).getUser().getLastname());
    }

    @Test
    public void testSaveHankTwice() {
        String id = "123456";
        hank.setId(id);
        auditedUserRepo.save(hank);
        auditedUserRepo.save(hank);
        assertEquals(2,userAuditRepo.findByUser_Id(id)
                .size());
    }

    @Test
    public void testSaveTwice() throws Exception {
        String id = "123";
        hank.setId(id);
        auditedUserRepo.save(hank);
        hankAgain.setId(id);
        auditedUserRepo.save(hankAgain);
//        assertEquals(2,autoUserRepo.count());
        assertEquals(2,userAuditRepo.findByUser_Id(hank.getId())
                .size());
    }

    @Test
    public void testSearchAuditList() throws Exception {
        auditedUserRepo.save(hank);
        hank.setLastname("Williams Jr");
        auditedUserRepo.save(hank);
        assertEquals(1,autoUserRepo.count());
        assertEquals(2,userAuditRepo.count());
        log.info("hank id: " + hank.getId());
        List<UserAudit> userAuditList = userAuditRepo.findById(hank.getId());

        assertThat(userAuditList,empty());
    }

    @Test
    public void testFindQ() throws Exception {
        auditedUserRepo.save(hank);
        hank.setLastname("Williams Jr");
        auditedUserRepo.save(hank);
        QUser qUser = new QUser("user");  //QUser is generated by querydsl maven generate source phase
        Iterable<User> userList = autoUserRepo.findAll(qUser.firstname.eq("Hank"));
        for (User user : userList) {
            log.info("user is: " + user.getId() + " " + user.getFirstname() + " " + user.toString());
        }
    }


    @Test
    public void testFindQuserAudit() throws Exception {
        auditedUserRepo.save(hank);
        hank.setLastname("Williams Jr");
        auditedUserRepo.save(hank);
        QUserAudit qUserAudit = new QUserAudit("whatisthis");

        Iterable<UserAudit> hankIterable = userAuditRepo.findAll(qUserAudit.user.firstname.eq("Hank"));
        ArrayList<UserAudit> userAuditList = Lists.newArrayList(hankIterable);
        assertEquals(2,userAuditList.size());
        assertEquals("Williams",userAuditList.get(0).getUser().getLastname());
        assertEquals("Williams Jr",userAuditList.get(1).getUser().getLastname());

        UserAudit hankFoundFromQuery = userAuditRepo.findOne(qUserAudit.user.lastname.eq("Williams"));

        assertEquals("Williams",hankFoundFromQuery.getUser().getLastname());

        hankFoundFromQuery = userAuditRepo.findOne(qUserAudit.user.lastname.eq("Williams")
                                                           .and(qUserAudit.user.firstname.eq("Seth")));
        assertNull(hankFoundFromQuery);
    }

    @Test
    public void testSearchAuditWithoutId() throws Exception {
        auditedUserRepo.save(hank);
        assertTrue(userAuditRepo.findByUser_Id(hankAgain.getId()).isEmpty());
    }

    @Test
    public void testEmptyObjectsString(){
        log.info("hank is: "+hank.toString());
        UserAudit ua = new UserAudit();
        log.info("useraudit is "+ua.toString());
    }

    @Test
    public void testSaveTwiceWithId() throws Exception {
        hank.setId("1234");
        auditedUserRepo.save(hank);

        hankAgain.setId("1234");

        auditedUserRepo.save(hankAgain);
        User hankFound = auditedUserRepo.findByFirstname("Hank");
        assertEquals("Williams",hankFound.getLastname());
        assertEquals(1,autoUserRepo.count());
        assertEquals(2,userAuditRepo.count());
    }

    @Test
    public void testSaveTwiceReusedId() throws Exception {
        User hankSaved = auditedUserRepo.save(hank);
        log.info("saved id: " + hankSaved.getId());

        hankAgain.setId(hankSaved.getId());

        auditedUserRepo.save(hankAgain);
        User hankFound = auditedUserRepo.findByFirstname("Hank");
        assertEquals("Williams",hankFound.getLastname());
        assertEquals(1,autoUserRepo.count());
    }

    @Test
    public void testSaveRest() throws Exception {
        auditedUserRepo.save(hank);

        mockMvc.perform(get("/audited-users/findByFirstname?firstname=Hank"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Williams")));

    }

}
