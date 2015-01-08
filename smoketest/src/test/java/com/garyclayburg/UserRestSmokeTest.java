/*
 * VisualSync - a tool to visualize user data synchronization
 * Copyright (c) 2015 Gary Clayburg
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.MatcherAssertionErrors;
import org.springframework.web.client.RestTemplate;

/**
 * Created by IntelliJ IDEA.
 * Date: 3/30/14
 * Time: 11:53 AM
 *
 * @author Gary Clayburg
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {BootSmoke.class})
/*
all context configuration must be done via spring boot(@SpringApplicationContext) and not regular
JUnit test context (@ContextConfiguration) so that spring boot web controllers can be found
 */
public class UserRestSmokeTest {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(UserRestSmokeTest.class);

    @Value(value = "${endpoint:localhost:8080/}")
    private String endpoint;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setUp() throws Exception {
        log.debug("Running test setUp: " + testName.getMethodName());
    }

    @Test
    public void testName() throws Exception {
        Assert.assertTrue(true);

    }

    @Test
    public void testOne() throws Exception {
        RestTemplate rest = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        SimpleUser user1 = new SimpleUser();
        user1.setFirstname("Tommy");
        user1.setLastname("Deleteme");
        user1.setId("112" + (int) (Math.floor(Math.random() * 10000)));

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Content-Type", "application/hal+json");
//        HttpEntity<?> requestEntity = new HttpEntity(requestHeaders);
        HttpEntity<?> requestEntity = new HttpEntity(user1,requestHeaders);

        ResponseEntity<SimpleUser> simpleUserResponseEntity = rest.exchange(
            "http://" + endpoint + "/audited-users/auditedsave",HttpMethod.POST,requestEntity,SimpleUser.class);

//        ResponseEntity<SimpleUser> userResponseEntity =
//            rest.postForEntity("http://" + endpoint + "/audited-users/auditedsave",user1,SimpleUser.class);
        log.info("got a response");
        MatcherAssertionErrors.assertThat(simpleUserResponseEntity.getStatusCode(),Matchers.equalTo(HttpStatus.OK));

    }

    @Test
    public void testPlainApache() throws Exception{
        RestTemplate rest = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        SimpleUser user1 = new SimpleUser();
        user1.setFirstname("Tommy");
        user1.setLastname("Deleteme");
        user1.setId("112" + (int) (Math.floor(Math.random() * 10000)));

        ResponseEntity<SimpleUser> userResponseEntity =
            rest.postForEntity("http://" + endpoint + "/audited-users/auditedsave",user1,SimpleUser.class);
        log.info("got a response");
        MatcherAssertionErrors.assertThat(userResponseEntity.getStatusCode(),Matchers.equalTo(HttpStatus.OK));

    }
    @Test
    public void testPlain() throws Exception{
        RestTemplate rest = new RestTemplate();
        SimpleUser user1 = new SimpleUser();
        user1.setFirstname("Tommy");
        user1.setLastname("Deleteme");
        user1.setId("112" + (int) (Math.floor(Math.random() * 10000)));

        ResponseEntity<SimpleUser> userResponseEntity =
            rest.postForEntity("http://" + endpoint + "/audited-users/auditedsave",user1,SimpleUser.class);
        log.info("got a response");
        MatcherAssertionErrors.assertThat(userResponseEntity.getStatusCode(),Matchers.equalTo(HttpStatus.OK));

    }

}

@JsonIgnoreProperties(ignoreUnknown = true)
class SimpleUser {
    String firstname;
    String lastname;
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}