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

import com.garyclayburg.attributes.AttributeService;
import com.garyclayburg.attributes.ScriptRunner;
import com.garyclayburg.filesystem.DirectoryWalker;
import com.garyclayburg.filesystem.WatchDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * Date: 5/27/14
 * Time: 1:11 PM
 *
 * @author Gary Clayburg
 */
@Configuration
public class ServiceAttributeConfig {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(ServiceAttributeConfig.class);

    @Value(value = "${groovyHome:/groov}")
    private String groovyHome;// = "/groovies";

    @Bean
    public ScriptRunner scriptRunner() throws IOException {
        ScriptRunner runner;
        runner = checkSetRoot(groovyHome);
        if (runner!=null){
            return runner;
        }
        runner = checkSetRoot(System.getProperty("groovy.home"));
        if (runner!=null){
            return runner;
        }
        runner = checkSetRoot(System.getProperty("user.home") + System.getProperty("groovy.user.home"));
        if (runner!=null){
            return runner;
        }
        log.warn("Cannot load groovy scripts from any known location");
        return new ScriptRunner();
    }

    private ScriptRunner checkSetRoot(String filePath) throws IOException {
        ScriptRunner scriptRunner = null;
        log.info("checking for groovy scripts at directory: " + filePath);
        if (filePath != null) {
            File f = new File(filePath);
            log.info("checking file");
            if (f.exists()) { //note: directory on s3fs mount will not appear to exist if option -o allow_other is not used when mounting filesystem so that tomcat user has read permission
                String directoryTree = DirectoryWalker.printDirectoryTree(f);
                log.debug("Directory tree: \n" + directoryTree);
                if (f.isDirectory()) {
                    scriptRunner = new ScriptRunner();
                    scriptRunner.setRoot(new String[]{filePath});
                    log.info("Using this directory for groovy scripts: {}",filePath);
                }
            }
        }
        return scriptRunner;
    }

    @Bean
    public AttributeService attributeService() throws IOException {
        AttributeService attributeService = new AttributeService();
        ScriptRunner scriptRunner = scriptRunner();
        if (scriptRunner.getRoots() != null) {
            log.info("setting up AttributeService " + Arrays.toString(scriptRunner.getRoots()));
        }
        attributeService.setScriptRunner(scriptRunner);
        return attributeService;
    }

    @Bean
    @Profile({"mongoembedded","mongolocal"}) //i.e. don't start up thread when running tests
    public WatchDir watchDir() throws IOException {
        ScriptRunner scriptRunner = scriptRunner();
        String[] roots = scriptRunner.getRoots();
        if (roots == null) {
            log.error("Groovy root not configured");
            return null;
        }
        Path watchPath = Paths.get(roots[0]);
        WatchDir watcher = new WatchDir(watchPath,true);
        Thread t = new Thread(watcher,"groovyWatcher");
        t.setDaemon(true);
        t.start();

        return watcher;
    }

}
