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

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
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
public class AttributeService {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(AttributeService.class);
    private String scanPackage;
    private ScriptRunner runner;

    public void setScanPackage(String scanPackage,ClassLoader classLoader,ScriptRunner runner) {
        this.scanPackage = scanPackage;
        this.runner = runner;
    }

    public String getScanPackage() {
        return scanPackage;
    }

    public Map<String, String> generateAttributes(User barney) {
        HashMap<String, String> generatedAttributes = new HashMap<String, String>();

        AttributesClass ac = matchAttributesClass();

        Class groovyClass = null;
        try {
            groovyClass = runner.loadClass("com/initech/GeneratedAttributes.groovy");
        } catch (ResourceException e) {
            log.warn("kaboom",e);
        } catch (ScriptException e) {
            log.warn("kaboom",e);
        }

        String targetServer = "myAD";
        String targetAttributeName = "cn";
        TargetAttribute adServer = matchTargetAttribute("myAD","no_attribute_name");

        TargetAttribute ta = matchTargetAttribute(targetServer,targetAttributeName);
        log.info("looking for method");
        Set<Method> attributeMethods = getAllMethods(groovyClass,withAnnotation(TargetAttribute.class));

        List<Method> methodList = new ArrayList<Method>(attributeMethods);
        String attributeValue = null;
        for (Method method : methodList) {
//            Method method = methodList.get(0);
            TargetAttribute annotation = method.getAnnotation(TargetAttribute.class);

            try {
                log.debug(
                        "attribute method found: " + method.getDeclaringClass() + "." + method.getName() + " target: " +
                        annotation.target() + " attribute name: " + annotation.attributeName());
                Object groovyObj = groovyClass.newInstance();
                attributeValue = (String) method.invoke(groovyObj,barney);
                log.debug(
                        "attribute value eval  : " + method.getDeclaringClass() + "." + method.getName() + " target: " +
                        annotation.target() + " attribute name: " + annotation.attributeName() + " generated value: " +
                        attributeValue);
                String attributeName = annotation.attributeName()
                                               .equals("") ? method.getName() : annotation.attributeName();
                generatedAttributes.put(attributeName,attributeValue);
                log.debug("attribute name:value  for target " + annotation.target() + ": [" + attributeName + ":" +
                          attributeValue + "]");
            } catch (InstantiationException e) {
                log.warn("kaboom",e);
            } catch (IllegalAccessException e) {
                log.warn("kaboom",e);
            } catch (InvocationTargetException e) {
                log.warn("kaboom",e);
            }
        }
/*
        } else if (methodList.size() > 1) {
            log.warn("multiple attributes match target:" + targetServer + " with attribute: " + targetAttributeName);
        } else if (methodList.size() < 1) {
            log.warn("no attributes match match target:" + targetServer + " with attribute: " + targetAttributeName);
        }
        */
        return generatedAttributes;
    }

    private AttributesClass matchAttributesClass() {
        return new AttributesClass() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return AttributesClass.class;
            }
        };
    }

    private TargetAttribute matchTargetAttribute(final String target,final String attributeName) {
        return new TargetAttribute() {
            @Override
            public String target() {
                return target;
            }

            @Override
            public String attributeName() {
                return attributeName;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return TargetAttribute.class;
            }
        };
    }

    List<Class> loadAllGroovyClasses() throws IOException, URISyntaxException, ResourceException, ScriptException {
        ArrayList<Class> loadedClasses = new ArrayList<Class>();
        String groovyRootPath = runner.getRoots()[0];
        log.info("Looking for groovy classes in path: " + groovyRootPath);
        File groovyRootFile = new File(groovyRootPath);

        Collection<File> listFiles = FileUtils.listFiles(groovyRootFile,new String[]{"groovy"},true);
        for (File listFile : listFiles) {
            String scriptName = listFile.getPath()
                    .replaceFirst(groovyRootPath,"");
            loadedClasses.add(runner.loadClass(scriptName));
        }
        log.info("Total groovy classes found in groovyRoot: " + listFiles.size());
        return loadedClasses;
    }

    List<Class> findAnnotatedGroovyClasses(Class<? extends Annotation> desiredAnnotation) throws URISyntaxException, ResourceException, ScriptException, IOException {
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
