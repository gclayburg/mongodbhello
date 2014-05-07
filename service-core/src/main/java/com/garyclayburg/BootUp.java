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

package com.garyclayburg;

import com.garyclayburg.data.ServiceConfig;
import com.garyclayburg.persistence.EmbeddedMongoConfig;
import com.garyclayburg.vconsole.VConsole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * Date: 3/26/14
 * Time: 12:24 PM
 *
 * @author Gary Clayburg
 */
@Configuration
@EnableAutoConfiguration
@Import({ServiceConfig.class,EmbeddedMongoConfig.class,RepositoryRestMvcConfiguration.class,VConsole.class})
public class BootUp implements CommandLineRunner{
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(BootUp.class);

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(BootUp.class);
        log.info("Beans loaded by spring / spring boot");
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            log.info(beanName);
        }
        log.info("");
        log.info("Server is ready for e-business");
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
