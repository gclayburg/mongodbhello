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

package com.garyclayburg.scimclient;

import com.garyclayburg.scimclient.authn.AuthHttpComponentsClientHttpRequestFactory;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 2/2/15
 * Time: 9:53 AM
 *
 * @author Gary Clayburg
 */
public class ScimApp {
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(ScimApp.class);

    public static void main(String[] args) {
        /*
        String plainCreds = "willie:p@ssword";
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
*/

        HttpClient httpClient = new DefaultHttpClient();
        final AuthHttpComponentsClientHttpRequestFactory requestFactory =
            new AuthHttpComponentsClientHttpRequestFactory(httpClient,new HttpHost("localhost"),"bjensen","password");
//            new AuthHttpComponentsClientHttpRequestFactory(httpClient,host,userName,password);
        final RestTemplate restTemplate = new RestTemplate(requestFactory);

//        RestTemplate restTemplate = new RestTemplate();
        SCIMusersDoc page = restTemplate.getForObject("http://localhost:8080/Users",SCIMusersDoc.class);
        log.info("total results: " + page.getTotalResults());
        List<String> schemas = page.getSchemas();
        for (String schema : schemas) {
            log.info("schema found: " + schema);
        }
        for (Resources user : page.getResources()) {
            log.info("username " + user.getUserName());
            log.info("formatted name:" +user.getName().getFormatted());
            log.info("work phone: " + ( user.getPhoneNumbers().get(0).getValue()));
        }

//        User userMatch =
    }
}
