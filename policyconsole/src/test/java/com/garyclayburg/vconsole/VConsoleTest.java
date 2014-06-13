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

package com.garyclayburg.vconsole;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

public class VConsoleTest {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(VConsoleTest.class);

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {
        log.debug("Running test setUp: " + testName.getMethodName());
    }

    @Test
    public void testScrubStackTrace() throws Exception {
        String in__message = " @ line 20, column 5.\n" +
                             "       @TargetAttribute(target = \"myAD\",attributeName = \"cn\")\n" +
                             "       ^\n" +
                             "\n" +
                             "1 error\n";
        String out_message = "&nbsp@ line 20, column 5.</br>" +
                             "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp@TargetAttribute(target = \"myAD\",attributeName = \"cn\")</br>" +
                             "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp^</br>" +
                             "</br>" +
                             "1 error</br>";
        assertEquals(out_message,MessageHelper.scrubMessage(in__message));

    }

    @Test
    public void testTabs() throws Exception {
        String in__message = "java.lang.reflect.InvocationTargetException: null\n" +
                             "\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.7.0_51]\n" +
                             "\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57) ~[na:1.7.0_51]\n" +
                             "\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.7.0_51]\n" +
                             "\tat java.lang.reflect.Method.invoke(Method.java:606) ~[na:1.7.0_51]\n" +
                             "\tat com.garyclayburg.attributes.AttributeService.generateAttributes(AttributeService.java:228) [classes/:na]\n" +
                             "\tat com.garyclayburg.attributes.AttributeService.getEntitledTargets(AttributeService.java:162) [classes/:na]\n" +
                             "\tat com.garyclayburg.vconsole.VConsole.createUserTable(VConsole.java:413) [classes/:na]\n" +
                             "\tat com.garyclayburg.vconsole.VConsole.init(VConsole.java:163) [classes/:na]\n";
        String out_message = "java.lang.reflect.InvocationTargetException: null</br>" +
                             "&nbsp&nbsp&nbsp&nbsp&nbspat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.7.0_51]</br>" +
                             "&nbsp&nbsp&nbsp&nbsp&nbspat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57) ~[na:1.7.0_51]</br>" +
                             "&nbsp&nbsp&nbsp&nbsp&nbspat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.7.0_51]</br>" +
                             "&nbsp&nbsp&nbsp&nbsp&nbspat java.lang.reflect.Method.invoke(Method.java:606) ~[na:1.7.0_51]</br>" +
                             "&nbsp&nbsp&nbsp&nbsp&nbspat com.garyclayburg.attributes.AttributeService.generateAttributes(AttributeService.java:228) [classes/:na]</br>" +
                             "&nbsp&nbsp&nbsp&nbsp&nbspat com.garyclayburg.attributes.AttributeService.getEntitledTargets(AttributeService.java:162) [classes/:na]</br>" +
                             "&nbsp&nbsp&nbsp&nbsp&nbspat com.garyclayburg.vconsole.VConsole.createUserTable(VConsole.java:413) [classes/:na]</br>" +
                             "&nbsp&nbsp&nbsp&nbsp&nbspat com.garyclayburg.vconsole.VConsole.init(VConsole.java:163) [classes/:na]</br>";
        assertEquals(out_message,MessageHelper.scrubMessage(in__message));

    }
}