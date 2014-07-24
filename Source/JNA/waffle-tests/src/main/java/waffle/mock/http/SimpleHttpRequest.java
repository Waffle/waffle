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

    private static int          _remotePort_s = 0;

    private String              _requestURI;
    private String              _queryString;
    private String              _remoteUser;
    private String              _method       = "GET";
    private String              _remoteHost;
    private String              _remoteAddr;
    private int                 _remotePort   = -1;
    private Map<String, String> _headers      = new HashMap<String, String>();
    private Map<String, String> _parameters   = new HashMap<String, String>();
    private byte[]              _content;
    private HttpSession         _session      = new SimpleHttpSession();
    private Principal           _principal;

    public SimpleHttpRequest() {
        super(Mockito.mock(HttpServletRequest.class));
        _remotePort = nextRemotePort();
    }

    public static synchronized int nextRemotePort() {
        return ++_remotePort_s;
    }

    public static synchronized void resetRemotePort() {
        _remotePort_s = 0;
    }

    public void addHeader(String headerName, String headerValue) {
        _headers.put(headerName, headerValue);
    }

    @Override
    public String getHeader(String headerName) {
        return _headers.get(headerName);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Iterators.asEnumeration(_headers.keySet().iterator());
    }

    @Override
    public String getMethod() {
        return _method;
    }

    @Override
    public int getContentLength() {
        return _content == null ? -1 : _content.length;
    }

    @Override
    public int getRemotePort() {
        return _remotePort;
    }

    public void setMethod(String methodName) {
        _method = methodName;
    }

    public void setContentLength(int length) {
        _content = new byte[length];
    }

    public void setRemoteUser(String username) {
        _remoteUser = username;
    }

    @Override
    public String getRemoteUser() {
        return _remoteUser;
    }

    @Override
    public HttpSession getSession() {
        return _session;
    }

    @Override
    public HttpSession getSession(boolean create) {
        if (_session == null && create) {
            _session = new SimpleHttpSession();
        }
        return _session;
    }

    @Override
    public String getQueryString() {
        return _queryString;
    }

    public void setQueryString(String queryString) {
        _queryString = queryString;
        if (_queryString != null) {
            for (String eachParameter : _queryString.split("[&]")) {
                String[] pair = eachParameter.split("=");
                String value = (pair.length == 2) ? pair[1] : "";
                addParameter(pair[0], value);
            }
        }
    }

    public void setRequestURI(String uri) {
        _requestURI = uri;
    }

    @Override
    public String getRequestURI() {
        return _requestURI;
    }

    @Override
    public String getParameter(String parameterName) {
        return _parameters.get(parameterName);
    }

    public void addParameter(String parameterName, String parameterValue) {
        _parameters.put(parameterName, parameterValue);
    }

    @Override
    public String getRemoteHost() {
        return _remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        _remoteHost = remoteHost;
    }

    @Override
    public String getRemoteAddr() {
        return _remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        _remoteAddr = remoteAddr;
    }

    @Override
    public Principal getUserPrincipal() {
        return _principal;
    }

    public void setUserPrincipal(Principal principal) {
        _principal = principal;
    }
}