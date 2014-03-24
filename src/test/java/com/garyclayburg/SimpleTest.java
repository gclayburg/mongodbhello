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

package com.garyclayburg;

import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;
/**
 * Created by maven archtype: trident-simple-archetype
 * Date: 9/18/12
 * Time: 10:37 AM
 */
public class SimpleTest {

    private static final Logger log = LoggerFactory.getLogger(SimpleTest.class);

    @BeforeClass
    public static void switchOn() {
        log.debug("Switch on");
    }

    @AfterClass
    public static void switchOff() {
        log.debug("Switch off");
    }

    @Before
    public void setUp() {
        log.debug("setUp test");
    }

    @After
    public void tearDown() {
        log.debug("teardown test");
    }

    @Test
    public void bareBones(){
        assertTrue(true);
    }
}