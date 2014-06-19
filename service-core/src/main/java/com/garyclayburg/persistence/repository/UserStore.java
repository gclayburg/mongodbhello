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

import com.garyclayburg.attributes.AttributeService;
import com.garyclayburg.attributes.GeneratedUser;
import com.garyclayburg.persistence.UserChangeController;
import com.garyclayburg.persistence.domain.User;
import com.garyclayburg.persistence.domain.UserAudit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 4/1/14
 * Time: 11:05 AM
 *
 * @author Gary Clayburg
 */
@Controller
@RequestMapping("/audited-users")
public class UserStore {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(UserStore.class);

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")  // IntelliJ confused by spring-boot wiring
    private AutoUserRepo autoUserRepo;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")  // IntelliJ confused by spring-boot wiring
    private UserAuditRepo userAuditRepo;

    @Autowired
    private AttributeService attributeService;

    @Autowired
    private UserChangeController userChangeController;

    @RequestMapping(value = "/findByFirstname",method = RequestMethod.GET)
    public
    @ResponseBody
    User findByFirstname(@RequestParam(value = "firstname",required = true) String firstname) {
        log.info("returning a found user...");
        return autoUserRepo.findByFirstname(firstname);
    }

    /**
     * Adds auditing support for saving users.  All fields are saved.
     *
     * @param user user to save
     *
     * @return user being saved, after fields such as "id" are inserted, if necessary
     */
    @RequestMapping(value = "/auditedsave",method = RequestMethod.POST)
    public
    @ResponseBody
    User save(@RequestBody User user) {
        User savedUser = autoUserRepo.save(user);
        UserAudit userAudit = new UserAudit(savedUser);
        userAuditRepo.save(userAudit);
        userChangeController.fireUserChangedEvent(savedUser);
        return savedUser;
    }

    @RequestMapping(value = "/findUserAuditById", method = RequestMethod.GET)
    public
    @ResponseBody
    List<UserAudit> findUserAuditById(@RequestParam(value = "id",required = true) String id) {
        return userAuditRepo.findById(id);
    }

    // http://localhost:8080/audited-users/findUserAuditByUserId/534304a0e4b0c2e2f8ad3215
    @RequestMapping(value = "/findUserAuditByUserId/{id}",method = RequestMethod.GET)
    public
    @ResponseBody
    List<UserAudit> findUserAuditByUserId(@PathVariable("id") User user) {
        List<UserAudit> byUserId;
        if (user !=null){
            byUserId = userAuditRepo.findByUser_Id(user.getId());
        } else{
            byUserId = null; //todo better exception handling for invalid input
        }
        return byUserId;
    }

    public GeneratedUser findGeneratedUserByFirstname(String name) {
        User u = findByFirstname(name);
        GeneratedUser generatedUser = new GeneratedUser(u);
        generatedUser.setAttributeService(attributeService);
        return generatedUser;

    }
}
