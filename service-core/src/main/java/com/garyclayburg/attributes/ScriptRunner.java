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

package com.garyclayburg.attributes;

import com.garyclayburg.attributes.groovyfork.ForkedGroovyScriptEngine;
import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * Date: 4/4/14
 * Time: 1:06 PM
 *
 * @author Gary Clayburg
 */
public class ScriptRunner {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(ScriptRunner.class);
    private ForkedGroovyScriptEngine gse = null;
    private String[] roots=null;
    public static final Pattern LEADING_SLASH_P = Pattern.compile("^[\\\\/]*");

    public ScriptRunner() {
    }

    public void setRoot(String[] strings) throws IOException {
        if (strings !=null && strings.length >0 && strings[0] !=null) {
            ArrayList<String> al = new ArrayList<>();
            for (String string : strings) {
                String oneRoot = new File(string).getAbsolutePath();
                al.add(oneRoot);
            }
            String[] newRoots = al.toArray(new String[al.size()]);
            log.info("set script root to: " + Arrays.toString(newRoots));
            gse = new ForkedGroovyScriptEngine(newRoots);
            this.roots = newRoots;
        } else {
            log.warn("Cannot setup groovy script root");
        }
    }

    public String[] getRoots() {
        return roots;
    }

    public Object execute(String scriptName,Binding bindingMap) throws ResourceException, ScriptException {
        return gse.run(scriptName,bindingMap);
    }

    public ClassLoader getClassLoader() {
        return gse.getGroovyClassLoader();
    }

    public Class loadClass(String scriptName,boolean forceReload,Class<? extends Annotation> desiredAnnotation) throws ResourceException, ScriptException {
        if (scriptName !=null){
            long startTime = System.nanoTime();
            Matcher matcher = LEADING_SLASH_P.matcher(scriptName);
            String scrubbedName = matcher.replaceAll("");
            log.debug("start loading groovy class: {}",scrubbedName);
            Class groovyClass = gse.loadScriptByName(scrubbedName,forceReload,desiredAnnotation);
            long endtime = System.nanoTime();
            log.info("DONE  loading groovy class: {} in {} microseconds",scrubbedName,((endtime - startTime) / 1000.0));
            return groovyClass;
        } else{
            log.error("Cannot load groovy class: null");
            throw new ResourceException("Cannot load groovy class: null");
        }
    }
    public Class[] getLoadedClasses(){
        return gse.getGroovyClassLoader().getLoadedClasses();
    }
}
