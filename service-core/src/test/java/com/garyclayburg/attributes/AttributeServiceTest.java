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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * Date: 4/4/14
 * Time: 9:49 AM
 *
 * @author Gary Clayburg
 */
public class AttributeServiceTest extends AttributeServiceTestBase{
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(AttributeServiceTest.class);

    @Before
    public void setUp() throws Exception {
        log.debug("Running test setUp: " + testName.getMethodName());
        setUpBeansWithRootFromClasspath("groovies/emptyscript.groovy");
    }

    @Test
    public void testOne() throws Exception {
        Map<String, String> generatedAttributes = attributeService.getGeneratedAttributes(barney);
        Assert.assertEquals("Barney Rubble",generatedAttributes.get("cn"));
        generatedAttributes = attributeService.getGeneratedAttributes(barney);
        Assert.assertEquals("Barney Rubble",generatedAttributes.get("cn"));
    }
    @Test
    public void testOneGeneratedBean() throws Exception {

        List<GeneratedAttributesBean> generatedAttributes = attributeService.getGeneratedAttributesBean(barney);
        boolean found = false;
        for (GeneratedAttributesBean generatedAttribute : generatedAttributes) {
            if (generatedAttribute.getAttributeName().equals("cn")) {
                assertEquals("Barney Rubble",generatedAttribute.getAttributeValue());
                found=true;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testOneGeneratedTargetBean() throws Exception {


        List<GeneratedAttributesBean> generatedAttributes = attributeService.getGeneratedAttributesBean(barney,"myAD");
        assertEquals(2,generatedAttributes.size());
        boolean found = false;
        for (GeneratedAttributesBean generatedAttribute : generatedAttributes) {
            if (generatedAttribute.getAttributeName().equals("cn")) {
                assertEquals("Barney Rubble",generatedAttribute.getAttributeValue());
                found=true;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testKnownTargetList(){
        Set<String> targetList = attributeService.getEntitledTargets(barney);
        assertEquals(2,targetList.size());
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
        Class[] classList = attributeService.loadAllGroovyClasses(null);
        assertEquals(3,classList.length);
    }

    @Test
    public void testReLoadGroovyClasses() throws Exception {
        Class[] classList = attributeService.loadAllGroovyClasses(null);
        assertEquals(3,classList.length);
        String root = scriptRunner.getRoots()[0];
        log.info("groovy root: " + root);
        Path gpath = Paths.get(root + "/emptyscript.groovy");
        log.info("groovy path: " + gpath);
        attributeService.reloadGroovyClass(gpath);
        assertEquals(3,classList.length);
    }

    @Test
    public void testFirePolicyChangeOnReload(){
        String root = scriptRunner.getRoots()[0];
        log.info("groovy root: " + root);
        Path gpath = Paths.get(root + "/emptyscript.groovy");
        log.info("groovy path: " + gpath);

        PolicyChangeController policyChangeControllerMock = Mockito.mock(PolicyChangeController.class);
        attributeService.setPolicyChangeController(policyChangeControllerMock);
        attributeService.reloadGroovyClass(gpath);

        Mockito.verify(policyChangeControllerMock).firePolicyChangedEvent();
    }

    @Test
    public void testFindAnnotatedGroovyClasses() throws Exception {
        Map<String, Class> classList = attributeService.findAnnotatedGroovyClasses(AttributesClass.class);
        assertEquals(1,classList.size());
    }

    @Test
    public void testFindAnnotatedGroovyClassesWithStatic() throws Exception {
        setUpBeansWithRootFromClasspath("groovies-withStaticFields/o2cAttributes.groovy");
        log.debug("looking for annotated classes");
        Map<String, Class> classList = attributeService.findAnnotatedGroovyClasses(AttributesClass.class);
        assertEquals(3,classList.size());
    }

    @Test
    public void testStripPath() throws Exception {
        assertEquals("/com/initech/somejunk.groovy",attributeService.stripScriptRoot("/groovies","/groovies/com/initech/somejunk.groovy"));
    }

    @Test
    public void testStripPathWin() throws Exception {
        assertEquals("\\com\\initech\\somejunk.groovy",attributeService.stripScriptRoot("c:\\dev\\stuff\\visualSyncSDK\\identitypolicy\\src\\main\\groovy","c:\\dev\\stuff\\visualSyncSDK\\identitypolicy\\src\\main\\groovy\\com\\initech\\somejunk.groovy"));
    }


    @Test
    public void testRunAScript() throws Exception {

        Map<String, Object> bindingMap = new HashMap<>();
        Binding binding = new Binding(bindingMap);

        Object obj = scriptRunner.execute("somescript.groovy",binding);

        assertEquals("hello",obj);
    }

}
