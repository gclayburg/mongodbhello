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

import com.garyclayburg.delete.DeletionFileVisitor;
import com.garyclayburg.persistence.domain.User;
import groovy.lang.Binding;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * Date: 4/4/14
 * Time: 9:49 AM
 *
 * @author Gary Clayburg
 */
public class AttributeServiceTest {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(AttributeServiceTest.class);
    private ScriptRunner scriptRunner;
    private String scriptRoot;

    @Before
    public void setUp() throws Exception {
        scriptRoot = getScriptRoot("groovies/emptyscript.groovy");
        scriptRunner = new ScriptRunner();
        scriptRunner.setRoot(new String[]{scriptRoot});

    }

    @Test
    public void testOne() throws Exception {
        AttributeService attributeService = new AttributeService();
        User barney = new User();
        barney.setFirstname("Barney");
        barney.setLastname("Rubble");
        barney.setId("12345");

        attributeService.setScanPackage("com.initech",scriptRunner.getClassLoader(),scriptRunner);
        Map<String, String> generatedAttributes = attributeService.generateAttributes(barney);

        Assert.assertEquals("Barney Rubble",generatedAttributes.get("cn"));

        generatedAttributes = attributeService.generateAttributes(barney);

        Assert.assertEquals("Barney Rubble",generatedAttributes.get("cn"));

    }

    @Test
    public void testClassloaderResource() throws Exception {
        ClassLoader cl = scriptRunner.getClassLoader();
        Enumeration<URL> resources = cl.getResources("com/initech");
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            log.info("url: " + url);
        }

        ClassLoader tcl = this.getClass()
                .getClassLoader();
        Enumeration<URL> resources1 = tcl.getResources("com/garyclayburg");
        while (resources1.hasMoreElements()) {
            URL url = resources1.nextElement();
            log.info("thread url: " + url);
        }

    }

    @Test
    public void testLoadGroovyClasses() throws Exception {
        AttributeService attributeService = new AttributeService();
        attributeService.setScanPackage("com.initech",scriptRunner.getClassLoader(),scriptRunner);
        List<Class> classList = attributeService.loadAllGroovyClasses();
        assertEquals(3,classList.size());
    }

    @Test
    public void testFindAnnotatedGroovyClasses() throws Exception {
        AttributeService attributeService = new AttributeService();
        attributeService.setScanPackage("com.initech",scriptRunner.getClassLoader(),scriptRunner);
        List<Class> classList = attributeService.findAnnotatedGroovyClasses(AttributesClass.class);
        assertEquals(1,classList.size());
    }

    @Test
    public void testRunAScript() throws Exception {

        Map<String, Object> bindingMap = new HashMap<String, Object>();
        Binding binding = new Binding(bindingMap);

        Object obj = scriptRunner.execute("somescript.groovy",binding);

        assertEquals("hello",obj);
    }

    @Test
    public void testDeleteTmp() throws Exception {
        DeletionFileVisitor.deletePath(Paths.get(System.getProperty("java.io.tmpdir")),"garbagedir*");
        DeletionFileVisitor.deletePath(Paths.get(System.getProperty("java.io.tmpdir")),"deleteme*");

    }


    private String getScriptRoot(String partialFileName) throws URISyntaxException {
        URL groovyURL = this.getClass()
                .getClassLoader()
                .getResource(partialFileName);

        assert groovyURL != null;
        return new File(groovyURL.toURI()).getParentFile()
                .getPath();

    }
}
