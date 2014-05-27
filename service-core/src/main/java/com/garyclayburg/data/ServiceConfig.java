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

import com.garyclayburg.importer.CsvImporter;
import com.garyclayburg.persistence.UserChangeController;
import com.garyclayburg.persistence.repository.UserStore;
import com.mongodb.Mongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by IntelliJ IDEA.
 * Date: 3/21/14
 * Time: 2:12 PM
 *
 * @author Gary Clayburg
 */
@Configuration
//@EnableMongoAuditing
@Import(ServiceAttributeConfig.class)
public class ServiceConfig {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(ServiceConfig.class);

    @Autowired
    private Mongo mongoClient;

    @Bean
    public UserService userService() {
        UserService userService = new UserService();
        userService.setMongoClient(mongoClient);
        return userService;
    }

    @Bean
    public CsvImporter csvImporter() {
        return new CsvImporter();
    }

    @Bean
    public UserStore auditedUserRepo() {
        return new UserStore();
    }

    @Bean
    public ProvisionService provisionService() {
        return new ProvisionService();
    }

    @Bean
    public UserChangeController userChangeController() {
        return new UserChangeController();
    }
}
