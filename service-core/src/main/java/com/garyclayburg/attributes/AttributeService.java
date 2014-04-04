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
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.reflections.ReflectionUtils.*;

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
    private ClassLoader classLoader;
    private ScriptRunner runner;

    public void setScanPackage(String scanPackage,ClassLoader classLoader,ScriptRunner runner) {
        this.scanPackage = scanPackage;
        this.classLoader = classLoader;
        this.runner = runner;
    }

    public String getScanPackage() {
        return scanPackage;
    }

    public Map<String, String> generateAttributes(User barney) {

        Reflections reflections =
                new Reflections(new ConfigurationBuilder()
                                        .filterInputsBy(new FilterBuilder().includePackage(scanPackage))
                                        .setUrls(ClasspathHelper.forClassLoader(this.classLoader))
                                        .setScanners(new TypeAnnotationsScanner())
                );



//        Set<Class<?>> attributesClass = reflections.getTypesAnnotatedWith(AttributesClass.class);
//        for (Class<?> aClass : attributesClass) {
//            log.info("class found : " + aClass);
//        }


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
        TargetAttribute ta = matchTargetAttribute(targetServer,targetAttributeName);

        Set<Method> cnMethod = getAllMethods(groovyClass,withAnnotation(ta));
        List<Method> methodList = new ArrayList<Method>(cnMethod);
        String attributeValue = null;
        if (methodList.size() ==1 ) {
            Method method = methodList.get(0);
            TargetAttribute annotation = method.getAnnotation(TargetAttribute.class);
//            annotation.
            log.debug("attribute method found: " + method);
            try {
                Object groovyObj = groovyClass.newInstance();
                attributeValue = (String) method.invoke(groovyObj,barney);

            } catch (InstantiationException e) {
                log.warn("kaboom",e);
            } catch (IllegalAccessException e) {
                log.warn("kaboom",e);
            } catch (InvocationTargetException e) {
                log.warn("kaboom",e);
            }
        } else if (methodList.size() > 1) {
            log.warn("multiple attributes match target:" + targetServer + " with attribute: " + targetAttributeName);
        } else if (methodList.size() <1){
            log.warn("no attributes match match target:" + targetServer + " with attribute: " + targetAttributeName);
        }


        HashMap<String, String> generatedAttributes = new HashMap<String, String>();
        generatedAttributes.put(targetAttributeName,"Barney Rubble");
        generatedAttributes.put(targetAttributeName,attributeValue);
        return generatedAttributes;
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
}
