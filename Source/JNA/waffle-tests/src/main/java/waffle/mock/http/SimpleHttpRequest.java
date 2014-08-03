/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2014 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 */
package waffle.mock.http;

import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import org.mockito.Mockito;

import com.google.common.collect.Iterators;

/**
 * @author dblock[at]dblock[dot]org
 */
public class SimpleHttpRequest extends HttpServletRequestWrapper {

    private static int          remotePortS = 0;

    private String              requestURI;
    private String              queryString;
    private String              remoteUser;
    private String              method      = "GET";
    private String              remoteHost;
    private String              remoteAddr;
    private int                 remotePort  = -1;
    private Map<String, String> headers     = new HashMap<String, String>();
    private Map<String, String> parameters  = new HashMap<String, String>();
    private byte[]              content;
    private HttpSession         session     = new SimpleHttpSession();
    private Principal           principal;

    public SimpleHttpRequest() {
        super(Mockito.mock(HttpServletRequest.class));
        this.remotePort = nextRemotePort();
    }

    public static synchronized int nextRemotePort() {
        return ++remotePortS;
    }

    public static synchronized void resetRemotePort() {
        remotePortS = 0;
    }

    public void addHeader(final String headerName, final String headerValue) {
        this.headers.put(headerName, headerValue);
    }

    @Override
    public String getHeader(final String headerName) {
        return this.headers.get(headerName);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Iterators.asEnumeration(this.headers.keySet().iterator());
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public int getContentLength() {
        return this.content == null ? -1 : this.content.length;
    }

    @Override
    public int getRemotePort() {
        return this.remotePort;
    }

    public void setMethod(final String methodName) {
        this.method = methodName;
    }

    public void setContentLength(final int length) {
        this.content = new byte[length];
    }

    public void setRemoteUser(final String username) {
        this.remoteUser = username;
    }

    @Override
    public String getRemoteUser() {
        return this.remoteUser;
    }

    @Override
    public HttpSession getSession() {
        return this.session;
    }

    @Override
    public HttpSession getSession(final boolean create) {
        if (this.session == null && create) {
            this.session = new SimpleHttpSession();
        }
        return this.session;
    }

    @Override
    public String getQueryString() {
        return this.queryString;
    }

    public void setQueryString(final String query) {
        this.queryString = query;
        if (this.queryString != null) {
            for (String eachParameter : this.queryString.split("[&]")) {
                final String[] pair = eachParameter.split("=");
                final String value = (pair.length == 2) ? pair[1] : "";
                addParameter(pair[0], value);
            }
        }
    }

    public void setRequestURI(final String uri) {
        this.requestURI = uri;
    }

    @Override
    public String getRequestURI() {
        return this.requestURI;
    }

    @Override
    public String getParameter(final String parameterName) {
        return this.parameters.get(parameterName);
    }

    public void addParameter(final String parameterName, final String parameterValue) {
        this.parameters.put(parameterName, parameterValue);
    }

    @Override
    public String getRemoteHost() {
        return this.remoteHost;
    }

    public void setRemoteHost(final String value) {
        this.remoteHost = value;
    }

    @Override
    public String getRemoteAddr() {
        return this.remoteAddr;
    }

    public void setRemoteAddr(final String value) {
        this.remoteAddr = value;
    }

    @Override
    public Principal getUserPrincipal() {
        return this.principal;
    }

    public void setUserPrincipal(final Principal value) {
        this.principal = value;
    }
}