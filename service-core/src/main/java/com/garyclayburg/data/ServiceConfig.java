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
import com.garyclayburg.persistence.repository.AccountMatcher;
import com.garyclayburg.persistence.repository.UserStore;
import com.mongodb.Mongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * Date: 3/21/14
 * Time: 2:12 PM
 *
 * @author Gary Clayburg
 */
@Configuration
@EnableConfigurationProperties
//@EnableMongoAuditing
@Import(ServiceAttributeConfig.class)
public class ServiceConfig {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(ServiceConfig.class);

    @Autowired
    private Mongo mongoClient;

    @Bean
    public UserService userService() {
        log.debug("Creating UserService bean");
        UserService userService = new UserService();
        userService.setMongoClient(mongoClient);
        return userService;
    }

    @Bean
    public CsvImporter csvImporter() {
        log.debug("Creating CsvImporter bean");
        return new CsvImporter();
    }

    @Bean
    public UserStore auditedUserRepo() {
        log.debug("Creating UserStore bean");
        return new UserStore();
    }

    @Bean
    public AccountMatcher accountMatcher(){
        log.debug("Creating AccountMatcher bean");
        return new AccountMatcher();
    }

    @Bean
    public ProvisionService provisionService() {
        log.debug("Creating ProvisionService bean");
        return new ProvisionService();
    }

    @Bean
    public UserChangeController userChangeController() {
        log.info("Creating UserChangeController bean");
        return new UserChangeController();
    }

//http://stackoverflow.com/questions/12371168/how-can-i-log-restful-post-data
    @Bean
    public Filter loggingFilter(){
        AbstractRequestLoggingFilter f = new AbstractRequestLoggingFilter() {

            @Override
            protected void beforeRequest(HttpServletRequest request, String message) {
                log.debug("beforeRequest messsage is: "+message);
            }

            @Override
            protected void afterRequest(HttpServletRequest request, String message) {
                log.debug("afterRequest messsage is: "+message);
            }

            @Override
            protected boolean shouldLog(HttpServletRequest request) {
                //For now, I only care about logging certain REST requests
                return request.getRequestURI() != null ? request.getRequestURI().contains("audited-users") : false;
            }
        };
        f.setIncludeClientInfo(true);
        f.setIncludePayload(true);
        f.setIncludeQueryString(true);
        f.setMaxPayloadLength(200);

        f.setBeforeMessagePrefix("BEFORE REQUEST  [");
        f.setAfterMessagePrefix("AFTER REQUEST    [");
        f.setAfterMessageSuffix("]\n");
        return f;
    }
}
