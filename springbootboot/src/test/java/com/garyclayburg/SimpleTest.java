package com.garyclayburg;

import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * Created by maven archtype: trident-simple-archetype
 * Date: 9/18/12
 * Time: 10:37 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Example.class)
public class SimpleTest {

    private static final Logger log = LoggerFactory.getLogger(SimpleTest.class);

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    WebApplicationContext webApplicationContext;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    Example example;

    private MockMvc mockMvc;

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
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() {
        log.debug("teardown test");
    }

    @Test
    public void bareBones() {
        assertTrue(true);
    }

    @Test
    public void testhi() throws Exception {
        mockMvc.perform(get("/")).andDo(print()).andExpect(content().string(containsString("hell")));

    }

    @Test
    public void testExampleController() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(example).build();
        mockMvc.perform(get("/")).andDo(print()).andExpect(content().string(containsString("hell")));

    }
}