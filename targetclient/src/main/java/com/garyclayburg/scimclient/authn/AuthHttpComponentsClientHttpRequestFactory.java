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

package com.garyclayburg.scimclient.authn;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nullable;
import java.net.URI;

/**
 * From http://www.baeldung.com/2012/04/16/how-to-use-resttemplate-with-basic-authentication-in-spring-3-1/
 * <p/>
 * <p>And with that, everything is in place – the {@link RestTemplate} will now be able to support the Basic
 * Authentication scheme; a simple usage pattern would be:
 * <p/>
 * <pre>
 * final AuthHttpComponentsClientHttpRequestFactory requestFactory = new AuthHttpComponentsClientHttpRequestFactory(
 *                  httpClient, host, userName, password);
 * final RestTemplate restTemplate = new RestTemplate(requestFactory);
 * </pre>
 * <p/>
 * And the request:
 * <p/>
 * <pre>
 * restTemplate.get("http://localhost:8080/spring-security-rest-template/api/foos/1", Foo.class);
 * </pre>
 *
 * @author anton
 */
public class AuthHttpComponentsClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {

    protected HttpHost host;
    @Nullable
    protected String userName;
    @Nullable
    protected String password;

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LoggerFactory.getLogger(AuthHttpComponentsClientHttpRequestFactory.class);

    public AuthHttpComponentsClientHttpRequestFactory(HttpHost host) {
        this(host,null,null);
    }

    public AuthHttpComponentsClientHttpRequestFactory(HttpHost host,@Nullable String userName,@Nullable String password) {
        super();
        this.host = host;
        this.userName = userName;
        this.password = password;
    }

    public AuthHttpComponentsClientHttpRequestFactory(HttpClient httpClient,HttpHost host) {
        this(httpClient,host,null,null);
    }

    public AuthHttpComponentsClientHttpRequestFactory(HttpClient httpClient,HttpHost host,@Nullable String userName,@Nullable String password) {
        super(httpClient);
        this.host = host;
        this.userName = userName;
        this.password = password;
    }

    @Override
    protected HttpContext createHttpContext(HttpMethod httpMethod,URI uri) {
        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local auth cache
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(host,basicAuth);

        // Add AuthCache to the execution context
        HttpClientContext localcontext = HttpClientContext.create();
        localcontext.setAuthCache(authCache);

        if (userName != null) {
            BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(host),new UsernamePasswordCredentials(userName,password));
            localcontext.setCredentialsProvider(credsProvider);
        }
        return localcontext;
    }

}