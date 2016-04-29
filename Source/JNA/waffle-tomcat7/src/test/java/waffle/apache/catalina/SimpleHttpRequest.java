/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010-2016 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.apache.catalina;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.catalina.connector.Request;

import mockit.Mocked;

/**
 * Simple HTTP Request.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class SimpleHttpRequest extends Request {

    /** The remote port s. */
    private static int remotePortS;

    /**
     * Next remote port.
     *
     * @return the int
     */
    public synchronized static int nextRemotePort() {
        return ++SimpleHttpRequest.remotePortS;
    }

    /**
     * Reset remote port.
     */
    public synchronized static void resetRemotePort() {
        SimpleHttpRequest.remotePortS = 0;
    }

    /** The request uri. */
    private String                    requestURI;

    /** The query string. */
    private String                    queryString;

    /** The remote user. */
    private String                    remoteUser;

    /** The method. */
    private String                    method     = "GET";

    /** The headers. */
    private final Map<String, String> headers    = new HashMap<>();

    /** The parameters. */
    private final Map<String, String> parameters = new HashMap<>();

    /** The content. */
    private byte[]                    content;

    /** The http session. */
    @Mocked
    private HttpSession               httpSession;

    /** The principal. */
    private Principal                 principal;

    /**
     * Instantiates a new simple http request.
     */
    public SimpleHttpRequest() {
        super();
        this.remotePort = SimpleHttpRequest.nextRemotePort();
    }

    /**
     * Adds the header.
     *
     * @param headerName
     *            the header name
     * @param headerValue
     *            the header value
     */
    public void addHeader(final String headerName, final String headerValue) {
        this.headers.put(headerName, headerValue);
    }

    /**
     * Adds the parameter.
     *
     * @param parameterName
     *            the parameter name
     * @param parameterValue
     *            the parameter value
     */
    public void addParameter(final String parameterName, final String parameterValue) {
        this.parameters.put(parameterName, parameterValue);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Request#getContentLength()
     */
    @Override
    public int getContentLength() {
        return this.content == null ? -1 : this.content.length;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Request#getHeader(java.lang.String)
     */
    @Override
    public String getHeader(final String headerName) {
        return this.headers.get(headerName);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Request#getMethod()
     */
    @Override
    public String getMethod() {
        return this.method;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Request#getParameter(java.lang.String)
     */
    @Override
    public String getParameter(final String parameterName) {
        return this.parameters.get(parameterName);
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Request#getQueryString()
     */
    @Override
    public String getQueryString() {
        return this.queryString;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Request#getRemoteAddr()
     */
    @Override
    public String getRemoteAddr() {
        return this.remoteAddr;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Request#getRemoteHost()
     */
    @Override
    public String getRemoteHost() {
        return this.remoteHost;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Request#getRemotePort()
     */
    @Override
    public int getRemotePort() {
        return this.remotePort;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Request#getRemoteUser()
     */
    @Override
    public String getRemoteUser() {
        return this.remoteUser;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Request#getRequestURI()
     */
    @Override
    public String getRequestURI() {
        return this.requestURI;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Request#getSession()
     */
    @Override
    public HttpSession getSession() {
        return this.httpSession;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Request#getSession(boolean)
     */
    @Override
    public HttpSession getSession(final boolean create) {
        return this.httpSession;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Request#getUserPrincipal()
     */
    @Override
    public Principal getUserPrincipal() {
        return this.principal;
    }

    /**
     * Sets the content length.
     *
     * @param length
     *            the new content length
     */
    public void setContentLength(final int length) {
        this.content = new byte[length];
    }

    /**
     * Sets the method.
     *
     * @param value
     *            the new method
     */
    public void setMethod(final String value) {
        this.method = value;
    }

    /**
     * Sets the query string.
     *
     * @param queryValue
     *            the new query string
     */
    public void setQueryString(final String queryValue) {
        this.queryString = queryValue;
        if (this.queryString != null) {
            for (final String eachParameter : this.queryString.split("[&]")) {
                final String[] pair = eachParameter.split("=");
                final String value = pair.length == 2 ? pair[1] : "";
                this.addParameter(pair[0], value);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Request#setRemoteAddr(java.lang.String)
     */
    @Override
    public void setRemoteAddr(final String value) {
        this.remoteAddr = value;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Request#setRemoteHost(java.lang.String)
     */
    @Override
    public void setRemoteHost(final String value) {
        this.remoteHost = value;
    }

    /**
     * Sets the remote user.
     *
     * @param value
     *            the new remote user
     */
    public void setRemoteUser(final String value) {
        this.remoteUser = value;
    }

    /**
     * Sets the request uri.
     *
     * @param value
     *            the new request uri
     */
    public void setRequestURI(final String value) {
        this.requestURI = value;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.catalina.connector.Request#setUserPrincipal(java.security.Principal)
     */
    @Override
    public void setUserPrincipal(final Principal value) {
        this.principal = value;
    }
}
