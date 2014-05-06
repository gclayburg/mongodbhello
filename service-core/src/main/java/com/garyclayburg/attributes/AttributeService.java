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

    public void setScriptRunner(ScriptRunner runner) {
        log.info("setting scriptrunner...");
        this.runner = runner;
    }

    public Map<String, String> getGeneratedAttributes(User user, String targetId) {
        Map<String, String> generatedAttributes = generateAttributes(user,targetId);

        return generatedAttributes;
    }

    public Map<String,String> getGeneratedAttributes(User user) {
        return generateAttributes(user,null);
    }

    private Map<String, String> generateAttributes(User user,String targetId) {
        long startTime = System.nanoTime();
        log.info("start generating user attributes for target: "+targetId);
        HashMap<String,String> attributeValues = new HashMap<String, String>(); //name,value

        log.info("looking for method");

        List<Class> attributeClasses;
        attributeClasses = findAnnotatedGroovyClasses(AttributesClass.class);
        if (attributeClasses != null) {
            for (Class groovyAttributeClass : attributeClasses) {
                try {
                    Object groovyObj = groovyAttributeClass.newInstance();
                    Set<Method> attributeMethods =
                            getAllMethods(groovyAttributeClass,withAnnotation(TargetAttribute.class));

                    List<Method> methodList = new ArrayList<Method>(attributeMethods);
                    for (Method method : methodList) {
                        TargetAttribute annotation = method.getAnnotation(TargetAttribute.class);
                        log.debug("attribute method found: " + method.getDeclaringClass() + "." + method.getName() +
                                  " target: " +
                                  annotation.target() + " attribute name: " + annotation.attributeName()
                        );
                        try {
                            String attributeValue = (String) method.invoke(groovyObj,user);
                            log.debug("attribute value eval  : " + method.getDeclaringClass() + "." + method.getName() +
                                      " target: " +
                                      annotation.target() + " attribute name: " + annotation.attributeName() +
                                      " generated value: " +
                                      attributeValue
                            );
                            String attributeName = annotation.attributeName()
                                                           .equals("") ? method.getName() : annotation.attributeName();
                            if (targetId == null) {
                                attributeValues.put(attributeName,attributeValue);
                            } else if (targetId.equals(annotation.target())){
                                attributeValues.put(attributeName,attributeValue);
                            } else{
                                log.debug("skipping attribute for target: "+annotation.target());
                            }
                            log.debug(
                                    "attribute name:value  for target " + annotation.target() + ": [" + attributeName +
                                    ":" +
                                    attributeValue + "]");
                        } catch (IllegalAccessException e) {
                            log.warn("Cannot invoke attribute method in groovy: " + method.getDeclaringClass() + "." +
                                     method.getName(),e);
                        } catch (InvocationTargetException e) {
                            log.warn("Cannot call groovy attribute method: " + method.getDeclaringClass() + "." +
                                     method.getName(),e);
                        }

                    }
                } catch (InstantiationException e) {
                    log.warn(
                            "Cannot check groovy script for generated attributes: " + groovyAttributeClass.getName(),e);
                } catch (IllegalAccessException e) {
                    log.warn("Cannot check groovy script for generated attributes: ",e);
                }

            }
        } else {
            log.warn("No groovy scripts found with @AttributesClass annotation. Users will not have generated attributes");
        }
        long endTime = System.nanoTime();
        log.info("Generate user attributes found: " + attributeValues.size());
        log.info("Generate user attributes time:  " + ((endTime - startTime) / 1000000000.0) + " secs");
        return attributeValues;
    }

    List<Class> loadAllGroovyClasses() {
        ArrayList<Class> loadedClasses = new ArrayList<Class>();
        String groovyRootPath = runner.getRoots()[0];
        log.info("Looking for groovy classes in path: " + groovyRootPath);
        File groovyRootFile = new File(groovyRootPath);

        Collection<File> listFiles = FileUtils.listFiles(groovyRootFile,new String[]{"groovy"},true);
        for (File listFile : listFiles) {
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
        List<Class> foundClasses = new ArrayList<Class>();
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
