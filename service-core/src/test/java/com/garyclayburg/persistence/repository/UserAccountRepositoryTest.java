/*
 * VisualSync - a tool to visualize user data synchronization
 * Copyright (c) 2015 Gary Clayburg
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
import com.garyclayburg.data.UserService;
import com.garyclayburg.persistence.domain.BogusMailAccount;
import com.garyclayburg.persistence.domain.User;
import com.garyclayburg.persistence.domain.UserAccount;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * Date: 3/20/14
 * Time: 1:20 PM
 *
 * @author Gary Clayburg
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class UserAccountRepositoryTest extends MongoInMemoryTestBase{
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(UserAccountRepositoryTest.class);

    @Rule
    public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb("demo-test");

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    protected UserService userService;

    @SuppressWarnings({"UnusedDeclaration","SpringJavaAutowiredMembersInspection"})
    @Autowired
    private ApplicationContext applicationContext; // nosql-unit requirement

    @Autowired
    @SuppressWarnings({"SpringJavaAutowiringInspection","SpringJavaAutowiredMembersInspection"})
    // IntelliJ confused by spring-boot wiring
    private AutoUserRepo accountRepo;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {
        log.debug("Running test setUp: " + testName.getMethodName());

    }

    @Test
    public void testSpringAutoWiredHelloWorld() throws Exception {
        assertTrue(true);
    }

    @Test
    @UsingDataSet(locations = {"/accountList.json"}, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testFindByEmailAuto() throws Exception {
        User johan = autoUserRepo.findByEmail("johan@nowhere.info");
        assertEquals("Johan",johan.getFirstname());
    }
/*
    @Test
    @UsingDataSet(locations = {"/accountList.json"}, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testFindAccountList() throws Exception {
        User johan = autoUserRepo.findByEmail("johan@nowhere.info");

        assertEquals(2,johan.getAccountList().size());
        assertEquals(2,accountRepo.getAccountList(johan).size());
        assertEquals(2,accountRepo.getAccountList(johan.getId()).size());

    }
*/
    @Test
    public void testAccountList() throws Exception {
        User newUser = new User();
        newUser.setFirstname("Bill");
        newUser.setLastname("Smith");

        UserAccount billUserAccountAD = new UserAccount();
        billUserAccountAD.setUsername("bsmith");
        List<UserAccount> accountList = new ArrayList<>();
        accountList.add(billUserAccountAD);
        newUser.setUserAccounts(accountList);
        autoUserRepo.save(newUser);


        User foundUser = autoUserRepo.findByFirstname("Bill");
        assertEquals("Smith",foundUser.getLastname());
        List<UserAccount> foundUserAccountSet = foundUser.getUserAccounts();
        assertEquals(1,foundUserAccountSet.size());
    }

    @Test
    public void testSubAccountList(){
        User newUser = new User();
        newUser.setFirstname("Steve");
        newUser.setLastname("Smith");

        BogusMailAccount steveAccount = new BogusMailAccount();
        steveAccount.setAccountActive("1");
        steveAccount.setUsername("ssmith");
        steveAccount.setDisplayname("Steve Smith");
        List<UserAccount> accountList = new ArrayList<>();
        accountList.add(steveAccount);
        newUser.setUserAccounts(accountList);
        autoUserRepo.save(newUser);


        User foundUser = autoUserRepo.findByFirstname("Steve");
        assertEquals("Smith",foundUser.getLastname());
        List<UserAccount> foundUserAccountSet = foundUser.getUserAccounts();
        assertEquals(1,foundUserAccountSet.size());

    }

    @Test
    public void testAccountType() throws Exception {
        BogusMailAccount bogus = new BogusMailAccount();
        assertEquals("com.garyclayburg.persistence.domain.BogusMailAccount",bogus.getAccountType());
    }
}
