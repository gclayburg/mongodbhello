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

import com.garyclayburg.BootUp;
import com.garyclayburg.persistence.domain.User;
import com.garyclayburg.persistence.domain.UserAudit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    private static final Logger log = LoggerFactory.getLogger(BootUp.class);

    @Qualifier("autoUserRepo")
    @Autowired
    private AutoUserRepo autoUserRepo;
    @Qualifier("userAuditRepo")
    @Autowired
    private UserAuditRepo userAuditRepo;

    @RequestMapping(value ="/findByFirstname",method= RequestMethod.GET)
    public @ResponseBody
    User findByFirstname(@RequestParam(value="firstname",required =true) String firstname) {
        log.info("returning a found user...");
        return autoUserRepo.findByFirstname(firstname);
    }

    /**
     * Adds auditing support for saving users.  All fields are saved.
     * @param user user to save
     * @return user being saved, after fields such as "id" are inserted, if necessary
     */
    public User save(User user) {
        User savedUser = autoUserRepo.save(user);
        UserAudit userAudit = new UserAudit(savedUser);
        userAuditRepo.save(userAudit);
        return savedUser;
    }
}
