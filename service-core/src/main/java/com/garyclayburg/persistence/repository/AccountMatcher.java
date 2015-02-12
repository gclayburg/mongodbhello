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

import com.garyclayburg.attributes.AttributeService;
import com.garyclayburg.persistence.domain.QUser;
import com.garyclayburg.persistence.domain.User;
import com.garyclayburg.persistence.domain.UserAccount;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 2/5/15
 * Time: 4:26 PM
 *
 * @author Gary Clayburg
 */
@Controller
//@RequestMapping("/matchusers")
public class AccountMatcher {

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    private AttributeService attributeService;

    @Qualifier("auditedUserRepo")
    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    private UserStore auditedUserRepo;

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(AccountMatcher.class);
    @Autowired
    @SuppressWarnings({"SpringJavaAutowiringInspection","SpringJavaAutowiredMembersInspection"})
    // IntelliJ confused by spring-boot wiring
    protected AutoUserRepo autoUserRepo;

    public User attachAccount(UserAccount scimAccount) {
        User matchedUser = matchAccount(scimAccount);
        User savedUser = null;
        if (matchedUser != null) {
            List<UserAccount> userAccounts = matchedUser.getUserAccounts();

            if (userAccounts != null) {
                ListIterator<UserAccount> listIterator = userAccounts.listIterator();
                boolean updatedAccount = false;
                while (listIterator.hasNext()) {
                    UserAccount userAccount = listIterator.next();
                    String username = userAccount.getUsername();
                    String accountType = userAccount.getAccountType();
                    if (username != null && accountType != null) {
                        if (username.equals(scimAccount.getUsername()) &&
                            accountType.equals(scimAccount.getAccountType())) {
                            listIterator.set(scimAccount);
                            updatedAccount = true;
                        }
                    }

                }
                if (!updatedAccount) {  //add as new account for this matched user
                    userAccounts.add(scimAccount);
                }
            } else {  //user does not have any accounts yet.  This will be his first.
                List<UserAccount> newAccounts = new ArrayList<>();
                newAccounts.add(scimAccount);
                matchedUser.setUserAccounts(newAccounts);
            }
            savedUser = auditedUserRepo.save(matchedUser);
        }
        return savedUser;
    }

    public User matchAccount(UserAccount scimAccount) {
        QUser qUser = new QUser("user");

        User matchedUser = null;
        //default policy matches accounts based on username
        String instanceName = scimAccount.getInstanceName();
        Iterable<User> userList;
        if (scimAccount.getInstanceName() == null) {
            // find users that already have a useraccount with specific instancename
            userList = autoUserRepo.findAll(qUser.userAccounts.any().username.eq(scimAccount.getUsername())
                                                .and(qUser.userAccounts.any().accountType
                                                         .eq(scimAccount.getAccountType()))
                                                .and(qUser.userAccounts.any().instanceName.isNull()));
        } else {
            // find users that alredy have a useraccount without a specific instancename
            userList = autoUserRepo.findAll(qUser.userAccounts.any().username.eq(scimAccount.getUsername())
                                                .and(qUser.userAccounts.any().accountType
                                                         .eq(scimAccount.getAccountType()))
                                                .and(qUser.userAccounts.any().instanceName.eq(instanceName)));
        }
        int matchListSize = Iterables.size(userList);
        if (matchListSize == 1) {
            matchedUser = Iterables.getFirst(userList,null);
            // this scimAccount was matched previously to the stored user
        } else if (matchListSize > 1) {
            log.error("ERR-102 more than one possible account match for username: " + scimAccount.getUsername());
        } else {
            // execute groovy identity policy on everyone to find match
            //todo use closure here to pass in groovy policy for creating username as part of user query?
            List<User> allUsers = autoUserRepo.findAll();  //todo find a more efficient method for this?
            int matches = 0;
            for (User oneUser : allUsers) {
                Map<String, String> generatedAttributes = attributeService.getGeneratedAttributes(oneUser);

                //convention: attributes named username are used for matching during a recon/sync
                String username = generatedAttributes.get("username");
                log.debug("checking username: {} {} {}",username,oneUser.getFirstname(),oneUser.getLastname());
                if (username != null && username.equals(scimAccount.getUsername())) {
                    matches++;
                    if (matchedUser == null) {
                        matchedUser = oneUser; //go with first match
                    }
                }
            }
            log.info("found {} users matching username attribute policy for user {} ",matches,scimAccount
                .getUsername());
        }
        return matchedUser;
    }

    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
}
