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

package com.garyclayburg.springdata;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: gclaybur
 * Date: 3/19/14
 * Time: 11:38 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {FooConfig.class})
public class FooServiceTest {

    private static final Logger log = LoggerFactory.getLogger(FooServiceTest.class);

    //autowire simple bean
    @Autowired FooService fooService;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() {
        log.debug("Running test setUp: " + testName.getMethodName());

    }

    @Test
    public void testSomething() {
        assertNotNull(fooService);
    }

}