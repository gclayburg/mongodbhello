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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * Date: 5/13/14
 * Time: 8:51 PM
 *
 * @author Gary Clayburg
 */
@Configuration
@EnableAutoConfiguration
@Import({BootUp.class})
//component scanning apparently can only be configured in one place - BootUp.class in this case
//@ComponentScan(basePackages = {"com.garyclayburg.vconsole"})
public class BootVaadin extends SpringBootServletInitializer {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(BootVaadin.class);

    public static void main(String[] args){
        log.info("running main with args: "+Arrays.toString(args));
        ensureActiveProfile();
        ApplicationContext ctx = SpringApplication.run(BootUp.class,args);
        log.info("active profiles: " + Arrays.toString(ctx.getEnvironment()
                                                               .getActiveProfiles()));

        log.info("Beans loaded by spring / spring boot");
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            log.info(beanName);
        }
        log.info("");
        log.info("BootVaadin Server is ready for e-business");

    }

    //Servlet 3 style web.xml - needed to start app via "mvn jetty:run  -Dspring.profiles.active=mongolocal"
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        log.info("running SpringServletInitializer...");
        ensureActiveProfile();
        return application.sources(applicationClass);
    }

    private static void ensureActiveProfile() { //todo: this is misleading if the spring command line option is used: --spring.profiles.active=mongolocal
        String specifiedProfile = System.getProperty("spring.profiles.active");
        if (specifiedProfile != null) {
            log.info("using specified spring profile: " + specifiedProfile);
        } else{
            String defaultProfile = "mongoembedded";
            System.setProperty("spring.profiles.default",defaultProfile);
            log.info("using default spring profile: " + defaultProfile);
        }
        dumpSystemProperties();
    }

    public static void dumpSystemProperties() {
        log.info("system properties dump");
        Properties systemProperties = System.getProperties();
        TreeMap tm = new TreeMap(systemProperties);
        for (Object o : tm.keySet()) {
            String key = (String) o;
            log.info(key +": "+ tm.get(o));
        }
        Map<String, String> getenv = new TreeMap<>(System.getenv());
        log.info("system environment dump");
        for (String key : getenv.keySet()) {
            log.info("env " + key + ": " + getenv.get(key));
        }
    }

    private static Class<BootVaadin> applicationClass = BootVaadin.class;
}
