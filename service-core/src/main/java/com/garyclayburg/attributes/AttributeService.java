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

import com.garyclayburg.persistence.domain.User;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

/**
 * Created by IntelliJ IDEA.
 * Date: 4/4/14
 * Time: 9:32 AM
 *
 * @author Gary Clayburg
 */
@Component
public class AttributeService {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(AttributeService.class);

    @Autowired
    private ScriptRunner runner;
    private Set<String> detectedTargetIds;
    private final List<Class> annotatedGroovyClasses;
    private boolean scanned = false;

    public AttributeService() {
        detectedTargetIds = new HashSet<>();
        annotatedGroovyClasses = new ArrayList<>();
    }

    public void setScriptRunner(ScriptRunner runner) {
        log.info("setting scriptrunner...");
        this.runner = runner;
        if ( runner.getRoots() != null) {
                Runnable runnable = new Runnable() {
                    public void run() {
                        synchronized(annotatedGroovyClasses) {
                            scanned=true;
                            log.info("     pre-loading initial groovy scripts...");
                            List<Class> loadedClasses = findAnnotatedGroovyClasses(AttributesClass.class);
                            annotatedGroovyClasses.clear();
                            annotatedGroovyClasses.addAll(loadedClasses);
                            log.info("DONE pre-loading initial groovy scripts...");
                        }
                    }
                };
            Thread t = new Thread(runnable);
            t.setName("pre-load" + String.valueOf(Math.random()).substring(2,6));
            t.start();
        }
    }

    public Set<String> getEntitledTargets(User user) {
        generateAttributes(user,null);
        //todo For now, everyone is entitled to every target
        return detectedTargetIds;
    }

    public Map<String, String> getGeneratedAttributes(User user,String targetId) {

        return generateAttributes(user,targetId);
    }

    public List<GeneratedAttributesBean> getGeneratedAttributesBean(User user) {
        return getGeneratedAttributesBean(user,null);
    }

    public List<GeneratedAttributesBean> getGeneratedAttributesBean(User user,String target) {
        Map<String, String> stringStringMap = generateAttributes(user,target);
        List<GeneratedAttributesBean> generatedAttributesBeans = new ArrayList<>();
        for (String key : stringStringMap.keySet()) {
            GeneratedAttributesBean bean = new GeneratedAttributesBean();
            bean.setAttributeName(key);
            bean.setAttributeValue(stringStringMap.get(key));
            generatedAttributesBeans.add(bean);
        }
        return generatedAttributesBeans;
    }

    public Map<String, String> getGeneratedAttributes(User user) {
        return generateAttributes(user,null);
    }

    private Map<String, String> generateAttributes(User user,String targetId) {
        long startTime = System.nanoTime();
        log.info("start generating user attributes for target: " + targetId);
        HashMap<String, String> attributeValues = new HashMap<>(); //name,value
        detectedTargetIds = new HashSet<>();

        log.info("looking for method");

//        attributeClasses = findAnnotatedGroovyClasses(AttributesClass.class);
        while (!scanned){
            //wait until thread to pre-load scripts has started (and locked annotatedGroovyClasses)
            //mostly needed for preventing race condition for fast-running unit tests
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                log.warn("kaboom",e);
            }
        }
        synchronized(annotatedGroovyClasses) {
            log.debug("checking out annotated classes");
            if (annotatedGroovyClasses.size() > 0) {
                for (Class groovyAttributeClass : annotatedGroovyClasses) {
                    try {
                        Object groovyObj = groovyAttributeClass.newInstance();
                        Set<Method> attributeMethods =
                                getAllMethods(groovyAttributeClass,withAnnotation(TargetAttribute.class));

                        List<Method> methodList = new ArrayList<>(attributeMethods);
                        for (Method method : methodList) {
                            TargetAttribute annotation = method.getAnnotation(TargetAttribute.class);
                            log.debug("attribute method found: " + method.getDeclaringClass() + "." + method.getName() +
                                      " target: " +
                                      annotation.target() + " attribute name: " + annotation.attributeName()
                            );
                            detectedTargetIds.add(annotation.target());
                            try {
                                String attributeValue =
                                        (String) method.invoke(groovyObj,user); //todo figure out what to do when user is null instead of just throwing ugly exception, i.e. using vconsole without any users in db (yet)
                                log.debug("attribute value eval  : " + method.getDeclaringClass() + "." +
                                          method.getName() +
                                          " target: " +
                                          annotation.target() + " attribute name: " + annotation.attributeName() +
                                          " generated value: " +
                                          attributeValue
                                );
                                String attributeName = annotation.attributeName()
                                                               .equals("") ? method.getName() : annotation.attributeName();
                                if (targetId == null) {
                                    attributeValues.put(attributeName,attributeValue);
                                } else if (targetId.equals(annotation.target())) {
                                    attributeValues.put(attributeName,attributeValue);
                                } else {
                                    log.debug("skipping attribute for target: " + annotation.target());
                                }
                                log.debug("attribute name:value  for target " + annotation.target() + ": [" +
                                          attributeName +
                                          ":" +
                                          attributeValue + "]"
                                );
                            } catch (IllegalAccessException e) {
                                log.warn("Cannot invoke attribute method in groovy: " + method.getDeclaringClass() +
                                         "." +
                                         method.getName(),e);
                            } catch (InvocationTargetException e) {
                                log.warn("Cannot call groovy attribute method: " + method.getDeclaringClass() + "." +
                                         method.getName(),e);
                            }

                        }
                    } catch (InstantiationException e) {
                        log.warn("Cannot check groovy script for generated attributes: " +
                                 groovyAttributeClass.getName(),e);
                    } catch (IllegalAccessException e) {
                        log.warn("Cannot check groovy script for generated attributes: ",e);
                    }

                }
            } else {
                log.warn("No groovy scripts found with @AttributesClass annotation. Users will not have generated attributes");
            }
        }
        long endTime = System.nanoTime();
        log.info("Generate user attributes found: " + attributeValues.size());
        log.info("Generate user attributes time:  " + ((endTime - startTime) / 1000000000.0) + " secs");
        return attributeValues;
    }

    List<Class> loadAllGroovyClasses() {
        ArrayList<Class> loadedClasses = new ArrayList<>();
        String groovyRootPath = runner.getRoots()[0];
        //todo deal with exception if groovy root has not been set:
        /*
        2014-05-28 14:07:51,638 [p-bio-8386-exec-5] ERROR o.a.c.c.C.[.[.[.[springAwareVaadinServlet] - Servlet.service() for servlet [springAwareVaadinServlet] in context with path [] threw exception [com.vaadin.server.ServiceException: java.lang.NullPointerException] with root cause
java.lang.NullPointerException: null
	at com.garyclayburg.attributes.AttributeService.loadAllGroovyClasses(AttributeService.java:182) ~[service-core-1.0-SNAPSHOT.jar:na]
	at com.garyclayburg.attributes.AttributeService.findAnnotatedGroovyClasses(AttributeService.java:208) ~[service-core-1.0-SNAPSHOT.jar:na]
	at com.garyclayburg.attributes.AttributeService.generateAttributes(AttributeService.java:116) ~[service-core-1.0-SNAPSHOT.jar:na]
	at com.garyclayburg.attributes.AttributeService.getGeneratedAttributesBean(AttributeService.java:92) ~[service-core-1.0-SNAPSHOT.jar:na]
	at com.garyclayburg.attributes.AttributeService.getGeneratedAttributesBean(AttributeService.java:88) ~[service-core-1.0-SNAPSHOT.jar:na]
	at com.garyclayburg.vconsole.VConsole.populateItems(VConsole.java:197) ~[VConsole.class:na]
	at com.garyclayburg.vconsole.VConsole.init(VConsole.java:112) ~[VConsole.class:na]
	at com.vaadin.ui.UI.doInit(UI.java:625) ~[vaadin-server-7.1.13.jar:7.1.13]
	at com.vaadin.server.communication.UIInitHandler.getBrowserDetailsUI(UIInitHandler.java:223) ~[vaadin-server-7.1.13.jar:7.1.13]

         */
        log.info("Looking for groovy classes in path: " + groovyRootPath);
        File groovyRootFile = new File(groovyRootPath);

        Collection<File> listFiles = FileUtils.listFiles(groovyRootFile,new String[]{"groovy"},true);
        //todo dealwith this exception if s3 is screwey:
        /*
        java.lang.IllegalArgumentException: Parameter 'directory' is not a directory
	at org.apache.commons.io.FileUtils.validateListFilesParameters(FileUtils.java:545) ~[commons-io-2.4.jar:2.4]
	at org.apache.commons.io.FileUtils.listFiles(FileUtils.java:521) ~[commons-io-2.4.jar:2.4]
	at org.apache.commons.io.FileUtils.listFiles(FileUtils.java:691) ~[commons-io-2.4.jar:2.4]
	at com.garyclayburg.attributes.AttributeService.loadAllGroovyClasses(AttributeService.java:186) ~[service-core-1.0-SNAPSHOT.jar:na]
	at com.garyclayburg.attributes.AttributeService.findAnnotatedGroovyClasses(AttributeService.java:208) ~[service-core-1.0-SNAPSHOT.jar:na]
	at com.garyclayburg.attributes.AttributeService.generateAttributes(AttributeService.java:116) ~[service-core-1.0-SNAPSHOT.jar:na]
	at com.garyclayburg.attributes.AttributeService.getGeneratedAttributesBean(AttributeService.java:92) ~[service-core-1.0-SNAPSHOT.jar:na]
	at com.garyclayburg.attributes.AttributeService.getGeneratedAttributesBean(AttributeService.java:88) ~[service-core-1.0-SNAPSHOT.jar:na]
	at com.garyclayburg.vconsole.VConsole.populateItems(VConsole.java:197) ~[VConsole.class:na]
	at com.garyclayburg.vconsole.VConsole.init(VConsole.java:112) ~[VConsole.class:na]
	at com.vaadin.ui.UI.doInit(UI.java:625) ~[vaadin-server-7.1.13.jar:7.1.13]

         */
        log.info("Finished Looking for groovy classes in path: "+groovyRootPath);
        for (File listFile : listFiles) {
            log.debug("processing groovy class: " + listFile.getPath());
            String scriptName = listFile.getPath()
                    .replaceFirst(groovyRootPath,"");
            try {
                loadedClasses.add(runner.loadClass(scriptName));
            } catch (ResourceException e) {
                log.error("Cannot access groovy script to load: " + scriptName + " Skipping.",e);
            } catch (ScriptException e) {
                log.error("Cannot parse groovy script: " + scriptName + " Skipping.",e);
            }
        }
        log.info("Total groovy classes found in groovyRoot: " + listFiles.size());
        return loadedClasses;
    }

    List<Class> findAnnotatedGroovyClasses(Class<? extends Annotation> desiredAnnotation) {
        List<Class> foundClasses = new ArrayList<>();
        List<Class> allGroovyClasses = loadAllGroovyClasses();
        for (Class loadedGroovyClass : allGroovyClasses) {
            if (loadedGroovyClass.isAnnotationPresent(desiredAnnotation)) {
                foundClasses.add(loadedGroovyClass);
                log.info("detected " + desiredAnnotation + " annotation in groovy class: " + loadedGroovyClass);
            }
        }
        return foundClasses;
    }
}
