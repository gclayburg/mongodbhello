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

import com.garyclayburg.ApplicationSettings;
import com.garyclayburg.persistence.domain.User;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyRuntimeException;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;

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

    private final Map<String,Class> groovyClassMap; //script name -> compiled class
    private final Map<String, Throwable> scriptErrors;
    private boolean initiallyScanned = false;

    @Qualifier("policyChangeController")
    @Autowired
    private PolicyChangeController policyChangeController;

    @Autowired
    private ApplicationSettings applicationSettings;

    public void setApplicationSettings(ApplicationSettings applicationSettings) {
        this.applicationSettings = applicationSettings;
    }

    public AttributeService() {
        detectedTargetIds = new HashSet<>();
        groovyClassMap = new HashMap<>();
        scriptErrors = new HashMap<>();
    }

    public void setPolicyChangeController(PolicyChangeController policyChangeController) {
        this.policyChangeController = policyChangeController;
    }

    public void setScriptRunner(ScriptRunner runner) {
        log.info("setting scriptrunner...");
        this.runner = runner;
        if ( runner.getRoots() != null) {
            initiallyScanned = false;
            Runnable runnable = new Runnable() {
                public void run() {
                    /*
                    loading scripts in a background thread improves startup performance, especially when scripts
                    are located on a slow file system such as S3
                     */
                    synchronized(groovyClassMap) {
                        initiallyScanned =true;
                        scanGroovyClasses();
                    }
                }
            };
            Thread t = new Thread(runnable);
            t.setName("pre-load" + String.valueOf(Math.random()).substring(2,6));
            log.info("starting pre-load thread: " + t.getName());
            t.start();
        } else{ // use read-only embedded scripts
            log.warn("Custom groovy policy scripts not found.  Defaulting to read-only embedded groovy policy scripts");
            initiallyScanned = true;
            ClassLoader parent = getClass().getClassLoader();
            String scriptName = "embeddedgroovy/com/embedded/DefaultAttributes.groovy";
            InputStream groovyIS = parent.getResourceAsStream(scriptName);
            StringBuilder sb = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader(groovyIS,Charset.forName(StandardCharsets.UTF_8.name())))){
                int c;
                while ((c = reader.read()) != -1) {
                    sb.append((char) c);
                }
                log.debug("complete default embedded groovy class:\n{}",sb.toString());
                GroovyClassLoader loader = new GroovyClassLoader(parent);
                Class parsedDefaultClass = loader.parseClass(sb.toString(),scriptName);
                groovyClassMap.clear();
                groovyClassMap.put(scriptName,parsedDefaultClass);
            } catch (IOException e) {
                log.warn("could not load embedded groovy scripts",e);
            }
        }
    }

    private void scanGroovyClasses() {
        long startTime = System.nanoTime();
        log.info("     loading groovy scripts...");
        Map<String, Class> annotatedClasses = findAnnotatedGroovyClasses(AttributesClass.class);
        groovyClassMap.clear();
        groovyClassMap.putAll(annotatedClasses);
        long endTime = System.nanoTime();
        log.info("DONE loading groovy scripts... {} millis",((endTime - startTime) / 1000000.0));
    }

    public void removeGroovyClass(Path path){
        if (path.toString().endsWith(".groovy")) {
            String absolutePath = path.toAbsolutePath().toString();
            synchronized(groovyClassMap) {
                log.info("re-loading all groovy because file removed: " + absolutePath);
                scanGroovyClasses();
                /* todo: this won't work well because gse by itself does not delete classes.  if we really want to support this and have it work for multiple classes per file, we need tighter integration to gse and the scriptcache it keeps, or punt and just re-initialize gse each time a delete file event occurs, which would be expensive */
            }
            policyChangeController.firePolicyChangedEvent();
        }
    }

    public void reloadGroovyClass(Path path) {
        if (path.toString().endsWith(".groovy")) {
            String absolutePath = path.toAbsolutePath().toString();
            synchronized(groovyClassMap) {
                log.info("re-loading all groovy because file changed: " + absolutePath);
                /* all scripts are scanned so that we can catch any files that contain multiple classes.  This could be optimized for speed if needed */
                scanGroovyClasses();
            }
            policyChangeController.firePolicyChangedEvent();
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
        HashMap<String, String> attributeValues = new HashMap<>(); //name,value

        if (user != null) {
            log.debug("start generating user attributes for userid: {} target: {}",user.getId(), targetId);
            detectedTargetIds = new HashSet<>();
//        attributeClasses = findAnnotatedGroovyClasses(AttributesClass.class);
            while (!initiallyScanned) {
                //wait until thread to pre-load scripts has started (and locked annotatedGroovyClasses)
                //mostly needed for preventing race condition for fast-running unit tests
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    log.warn("kaboom",e);
                }
            }
            synchronized(groovyClassMap) {
                log.debug("checking for annotated classes");
                if (groovyClassMap.size() > 0) {

                    for (Class groovyAttributeClass : groovyClassMap.values()) {
                        log.debug("checking for annotated classes: {}",groovyAttributeClass.getName());
                        try {
                            Object groovyObj = groovyAttributeClass.newInstance();
                            Set<Method> attributeMethods =
                                    getAllMethods(groovyAttributeClass,withAnnotation(TargetAttribute.class));
                            log.debug("found {} annotated methods in class",attributeMethods.size());
                            List<Method> methodList = new ArrayList<>(attributeMethods);
                            for (Method method : methodList) {
                                String absMethodName = method.getDeclaringClass() + "." + method.getName();
                                TargetAttribute annotation = method.getAnnotation(TargetAttribute.class);
                                log.debug("attribute method found: {} target: {} attribute name: {}",
                                          absMethodName,annotation.target(),annotation.attributeName());
                                detectedTargetIds.add(annotation.target());
                                try {
                                    String attributeValue =
                                            (String) method.invoke(groovyObj,user); //todo figure out what to do when user is null instead of just throwing ugly exception, i.e. using vconsole without any users in db (yet)
                                    synchronized(groovyClassMap) {
                                        scriptErrors.remove(absMethodName);
                                    }
                                    log.debug("attribute value eval  : {} target: {} attribute name: {} generated value: {}",
                                              absMethodName,annotation.target(),annotation.attributeName(),attributeValue);
                                    String attributeName = annotation.attributeName()
                                                                   .equals("") ? method.getName() : annotation.attributeName();
                                    if (targetId == null) {
                                        attributeValues.put(attributeName,attributeValue);
                                    } else if (targetId.equals(annotation.target())) {
                                        attributeValues.put(attributeName,attributeValue);
                                    } else {
                                        log.debug("skipping attribute for target: {}", annotation.target());
                                    }
                                    log.debug("attribute name:value  for target {}: [{}:{}]"
                                              ,annotation.target(),attributeName,attributeValue);
                                } catch (IllegalAccessException e) {
                                    log.warn("Cannot invoke attribute method in groovy: {}",absMethodName,e);
                                    synchronized(groovyClassMap) {
                                        scriptErrors.put(absMethodName,e);
                                    }
                                } catch (InvocationTargetException e) {
                                    log.warn("Cannot call groovy attribute method: {}", absMethodName,e);
                                    synchronized(groovyClassMap) {
                                        scriptErrors.put(absMethodName,e);
                                    }
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
            log.info("Generated {} user attributes for userid {} in {} secs:  ",attributeValues.size(),user.getId(), ((endTime - startTime) / 1000000000.0) );
        }
        return attributeValues;
    }

    Class[] loadAllGroovyClasses(Class<? extends Annotation> desiredAnnotation) {
        Map<String,Class> loadedClasses = new HashMap<>();
        String[] roots = runner.getRoots();
        if (roots != null) {
            String groovyRootPath = roots[0];
            log.info("Looking for groovy classes in path: " + groovyRootPath);
            File groovyRootFile = new File(groovyRootPath);

            Collection<File> listFiles = FileUtils.listFiles(groovyRootFile,new String[]{"groovy"},true);
            log.info("Finished Looking for groovy classes in path: " + groovyRootPath);
            for (File listFile : listFiles) {
                String groovyPathKey = listFile.getPath();
                log.debug("processing groovy class: " + groovyPathKey);
                String scriptFileName = stripScriptRoot(groovyRootPath,groovyPathKey);
                try {
                    Class loadedClass;
                    if (applicationSettings.isForceRecompileEntryPoints()) {
                        loadedClass = runner.loadClass(scriptFileName,false,desiredAnnotation);
                    } else{
                        loadedClass = runner.loadClass(scriptFileName,false,null);
                    }
                    log.info("standard name: {}",applicationSettings.getStandardName());
                    loadedClasses.put(groovyPathKey,loadedClass);
                    synchronized(groovyClassMap) {
                        scriptErrors.remove(groovyPathKey);
                    }
                } catch (ResourceException e) {
                    log.error("Cannot access groovy script to load: " + scriptFileName + " Skipping.",e);
                    synchronized(groovyClassMap) {
                        scriptErrors.put(groovyPathKey,e);
                    }
                    policyChangeController.firePolicyException(e);
                } catch (ScriptException e) {
                    log.error("Cannot parse groovy script: " + scriptFileName + " Skipping.",e);
                    synchronized(groovyClassMap) {
                        scriptErrors.put(groovyPathKey,e);
                    }
                    policyChangeController.firePolicyException(e);
                } catch (GroovyRuntimeException gre){
                    log.error("Cannot compile script: "+scriptFileName,gre);
                    synchronized(groovyClassMap) {
                        scriptErrors.put(groovyPathKey,gre);
                    }
                    policyChangeController.firePolicyException(gre);
                }
            }
            log.info("Total groovy classes found in groovyRoot: " + listFiles.size());
        }
        return runner.getLoadedClasses();
    }

    public String stripScriptRoot(String groovyRootPath,String absPath) {
        log.debug("groovyrootpath {}", groovyRootPath);
        log.debug("abspath {}", absPath);

        String sanitizedGroovyRootPath = Matcher.quoteReplacement(groovyRootPath);
        return absPath.replaceFirst(sanitizedGroovyRootPath,"");
    }

    Map<String, Class> findAnnotatedGroovyClasses(Class<? extends Annotation> desiredAnnotation) {
        Map<String,Class> foundClasses = new HashMap<>();

        Class[] loadedClasses = loadAllGroovyClasses(desiredAnnotation);

        for (Class loadedClass : loadedClasses) {
            log.debug("gse loaded class: " + loadedClass.getName());
            if (loadedClass.isAnnotationPresent(desiredAnnotation)) {
                foundClasses.put(loadedClass.getName(),loadedClass);
                log.info("gse detected {} annotation in groovy class: {}",desiredAnnotation,loadedClass);
            }
        }
        return foundClasses;
    }

    public Map<String, Throwable> getScriptErrors() {
        synchronized(groovyClassMap){
            return scriptErrors;
        }
    }

    public ApplicationSettings getApplicationSettings() {
        return applicationSettings;
    }
}
