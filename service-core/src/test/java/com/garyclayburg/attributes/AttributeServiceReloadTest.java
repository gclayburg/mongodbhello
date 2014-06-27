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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA. Date: 6/23/14 Time: 5:07 PM
 *
 * @author Gary Clayburg
 */
public class AttributeServiceReloadTest extends AttributeServiceTestBase {
  @SuppressWarnings("UnusedDeclaration")
  private static final Logger log = LoggerFactory.getLogger(AttributeServiceMultipleClassTest.class);

  @Override
  @Before
  public void setUp() throws Exception {
    log.debug("Running test setUp: " + testName.getMethodName());
    wipeTmpFiles();
  }

  @After
  public void tearDown() throws Exception {

    log.debug("TearDown test: " + testName.getMethodName());
    wipeTmpFiles();
  }

  private void wipeTmpFiles() throws IOException {
    System.gc();  //without this, the test will sometimes fail on windows when deleting files.  code smell? yes. bug? you bet.
    // http://stackoverflow.com/questions/991489/i-cant-delete-a-file-in-java
    //http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4715154
    String baseDir = "grooviesStatic";
    DeletionFileVisitor.deletePath(Paths.get(System.getProperty("java.io.tmpdir")),baseDir);
  }

  @Test
  public void testStaticDependentClass() throws Exception {
    setupStaticGroovies();
    Set<String> entitledTargets = attributeService.getEntitledTargets(barney);
    assertThat(entitledTargets,hasSize(1));
    assertThat(entitledTargets,contains("Active Directory ADT Domain"));
  }

  @Test
  public void testStaticDependentClassReload() throws Exception {
    File multipleClassDir = setupStaticGroovies().getRootDir();
    Set<String> entitledTargets = attributeService.getEntitledTargets(barney);
    assertThat(entitledTargets,contains("Active Directory ADT Domain"));
    File rootFile = new File(multipleClassDir,"TargetListf.groovy");
    attributeService.reloadGroovyClass(Paths.get(rootFile.toURI()));
    assertThat(entitledTargets,contains("Active Directory ADT Domain"));

  }

  @Test
  public void dependentClassChangeReload() throws Exception {
    GroovyWriter gw = setupStaticGroovies();
    Set<String> entitledTargets = attributeService.getEntitledTargets(barney);
    assertThat(entitledTargets,contains("Active Directory ADT Domain"));

    Thread.sleep(1500);  //allow filesystem timestamp to differ enough from previous
    File targetListfile = changeDomainNameConstant(gw);
    attributeService.reloadGroovyClass(
            Paths.get(targetListfile.toURI())); // this should cause dependent classes to get recompiled
    entitledTargets = attributeService.getEntitledTargets(barney);
    assertThat(entitledTargets,contains("new domain name"));
  }

  private File changeDomainNameConstant(GroovyWriter gw) throws IOException {
    return gw.write("package com.multipleclass\n" +
                    "class TargetList {\n" +
                    "    public static final String ADT = \"new domain name\"\n" +
                    "}\n","TargetList.groovy");
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Test
  public void dependentClassChangeTouch() throws InterruptedException, IOException {
    GroovyWriter gw = setupStaticGroovies();
    File multipleClassDir = gw.getRootDir();
    Set<String> entitledTargets = attributeService.getEntitledTargets(barney);
    assertThat(entitledTargets,contains("Active Directory ADT Domain"));

    Thread.sleep(1500);  //allow filesystem timestamp to differ enough from previous
    File targetListfile = changeDomainNameConstant(gw);
    File singleAttributeFile = new File(multipleClassDir,"SingleAttribute.groovy");
    singleAttributeFile.setLastModified(System.currentTimeMillis());
    attributeService.reloadGroovyClass(
            Paths.get(targetListfile.toURI())); // this should cause dependent classes to get recompiled
    entitledTargets = attributeService.getEntitledTargets(barney);
    assertThat(entitledTargets,contains("new domain name"));
  }

  @Test
  public void dependentClassChangeMultiple() throws Exception {
    GroovyWriter gw = setupStaticGroovies();
    Set<String> entitledTargets = attributeService.getEntitledTargets(barney);
    assertThat(entitledTargets,contains("Active Directory ADT Domain"));

    Thread.sleep(1500);  //allow filesystem timestamp to differ enough from previous
    File targetListfile = changeDomainNameConstant(gw);
    attributeService.reloadGroovyClass(
            Paths.get(targetListfile.toURI())); // this should cause dependent classes to get recompiled
    gw.write("package com.multipleclass\n" +
             "import com.garyclayburg.attributes.AttributesClass\n" +
             "import com.garyclayburg.attributes.TargetAttribute\n" +
             "import com.garyclayburg.persistence.domain.User\n" +
             "\n" +
             "@AttributesClass\n" +
             "class SingleAttribute {\n" +
             "\n" +
             "    @TargetAttribute(target = TargetList.ADT,attributeName = \"dept\")\n" +
             "    static String department(User user){\n" +
             "        return \"dept 771\"\n" +
             "    }\n" +
             "}\n","SingleAttribute.groovy");

    attributeService.reloadGroovyClass(
            Paths.get(targetListfile.toURI())); // this should cause dependent classes to get recompiled
    attributeService.reloadGroovyClass(
            Paths.get(targetListfile.toURI())); // this should cause dependent classes to get recompiled
    attributeService.reloadGroovyClass(
            Paths.get(targetListfile.toURI())); // this should cause dependent classes to get recompiled

    entitledTargets = attributeService.getEntitledTargets(barney);
    assertThat(entitledTargets,contains("new domain name"));
  }

  private GroovyWriter setupStaticGroovies() throws IOException {
    String tmpDir = System.getProperty("java.io.tmpdir");
    File grooviesDir = new File(tmpDir + File.separatorChar + "grooviesStatic");
    assertTrue(grooviesDir.mkdir());
    File multipleClassDir = new File(
            tmpDir + File.separatorChar + "grooviesStatic" + File.separatorChar + "com" + File.separatorChar +
            "multipleclass");
    log.info("tmp: {}",tmpDir);
    assertTrue(multipleClassDir.mkdirs());

    GroovyWriter gw = new GroovyWriter(multipleClassDir);

    gw.write("package com.multipleclass\n" +
             "class TargetList {\n" +
             "    public static final String ADT = \"Active Directory ADT Domain\"\n" +
             "}\n","TargetList.groovy");

    gw.write("package com.multipleclass\n" +
             "import com.garyclayburg.attributes.AttributesClass\n" +
             "import com.garyclayburg.attributes.TargetAttribute\n" +
             "import com.garyclayburg.persistence.domain.User\n" +
             "\n" +
             "@AttributesClass\n" +
             "class SingleAttribute {\n" +
             "\n" +
             "    @TargetAttribute(target = TargetList.ADT,attributeName = \"dept\")\n" +
             "    static String department(User user){\n" +
             "        return \"dept 77\"\n" +
             "    }\n" +
             "}\n","SingleAttribute.groovy");

    setUpBeans(grooviesDir.getPath());
    return gw;
  }

}
