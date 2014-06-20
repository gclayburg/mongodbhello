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

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
    private GroovyScriptEngine gse = null;
    private String[] roots=null;

    public ScriptRunner() {
    }

    public void setRoot(String[] strings) throws IOException {
        ArrayList<String> al = new ArrayList<>();
        for (String string : strings) {
            String oneRoot = new File(string).getAbsolutePath();
            al.add(oneRoot);
        }
        String[] newRoots = al.toArray(new String[al.size()]);
        log.info("set script root to: " + Arrays.toString(newRoots));
        gse = new GroovyScriptEngine(newRoots);
        this.roots = newRoots;
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

    public Class loadClass(String scriptName) throws ResourceException, ScriptException {
        if (scriptName !=null){
            String scrubbedName = scriptName.replaceAll("^[\\\\/]*","");
            log.debug("start loading groovy class: " + scrubbedName);
            Class groovyClass = gse.loadScriptByName(scrubbedName);
            log.info("DONE  loading groovy class: " + scrubbedName);
            return groovyClass;
        } else{
            log.error("Cannot load groovy class: null");
            throw new ResourceException("Cannot load groovy class: null");
        }
    }
}
