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
package waffle.apache.catalina;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.catalina.connector.Request;

/**
 * @author dblock[at]dblock[dot]org
 */
public class SimpleHttpRequest extends Request {

    private static int          remotePortS;

    private String              requestURI;
    private String              queryString;
    private String              remoteUser;
    private String              method      = "GET";
    private Map<String, String> headers     = new HashMap<String, String>();
    private Map<String, String> parameters  = new HashMap<String, String>();
    private byte[]              content;
    private HttpSession         httpSession = new SimpleHttpSession();
    private Principal           principal;

    public SimpleHttpRequest() {
        super();
        this.remotePort = nextRemotePort();
    }

    public synchronized static int nextRemotePort() {
        return ++remotePortS;
    }

    public synchronized static void resetRemotePort() {
        remotePortS = 0;
    }

    public void addHeader(String headerName, String headerValue) {
        this.headers.put(headerName, headerValue);
    }

    @Override
    public String getHeader(String headerName) {
        return this.headers.get(headerName);
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

    public void setMethod(String value) {
        this.method = value;
    }

    public void setContentLength(int length) {
        this.content = new byte[length];
    }

    public void setRemoteUser(String value) {
        this.remoteUser = value;
    }

    @Override
    public String getRemoteUser() {
        return this.remoteUser;
    }

    @Override
    public HttpSession getSession() {
        return this.httpSession;
    }

    @Override
    public HttpSession getSession(boolean create) {
        if (this.httpSession == null && create) {
            this.httpSession = new SimpleHttpSession();
        }
        return this.httpSession;
    }

    @Override
    public String getQueryString() {
        return this.queryString;
    }

    public void setQueryString(String queryValue) {
        this.queryString = queryValue;
        if (this.queryString != null) {
            for (String eachParameter : this.queryString.split("[&]")) {
                String[] pair = eachParameter.split("=");
                String value = (pair.length == 2) ? pair[1] : "";
                addParameter(pair[0], value);
            }
        }
    }

    public void setRequestURI(String value) {
        this.requestURI = value;
    }

    @Override
    public String getRequestURI() {
        return this.requestURI;
    }

    @Override
    public String getParameter(String parameterName) {
        return this.parameters.get(parameterName);
    }

    public void addParameter(String parameterName, String parameterValue) {
        this.parameters.put(parameterName, parameterValue);
    }

    @Override
    public String getRemoteHost() {
        return this.remoteHost;
    }

    @Override
    public void setRemoteHost(String value) {
        this.remoteHost = value;
    }

    @Override
    public String getRemoteAddr() {
        return this.remoteAddr;
    }

    @Override
    public void setRemoteAddr(String value) {
        this.remoteAddr = value;
    }

    @Override
    public Principal getUserPrincipal() {
        return this.principal;
    }

    @Override
    public void setUserPrincipal(Principal value) {
        this.principal = value;
    }
}
