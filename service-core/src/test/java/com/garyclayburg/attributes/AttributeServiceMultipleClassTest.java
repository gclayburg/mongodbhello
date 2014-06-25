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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * Date: 4/4/14
 * Time: 9:49 AM
 *
 * @author Gary Clayburg
 */
public class AttributeServiceMultipleClassTest extends AttributeServiceTestBase {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(AttributeServiceMultipleClassTest.class);

    @Test
    public void testMultipleClassFile() throws Exception {
        setUpBeansWithRootFromClasspath("groovies-multipleclassperfile/emptyscript.groovy");
        Map<String, String> generatedAttributes = attributeService.getGeneratedAttributes(barney);
        assertEquals(1,generatedAttributes.size());
    }

    @Test
    public void testReverseOrderMultipleClassFile() throws Exception {
        setUpBeansWithRootFromClasspath("groovies-reversemultipleclassperfile/emptyscript.groovy");

        Map<String, String> generatedAttributes = attributeService.getGeneratedAttributes(barney);
        assertEquals(1,generatedAttributes.size());

        Map<String, String> generatedAttributes1 =
            attributeService.getGeneratedAttributes(barney,"Active Directory BCT Domain");
        assertEquals(1,generatedAttributes1.size());

    }



}
