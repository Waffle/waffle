/*
 * MIT License
 *
 * Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package waffle.apache.catalina;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import mockit.Mocked;

import org.apache.catalina.connector.Request;

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
    private String requestURI;

    /** The query string. */
    private String queryString;

    /** The remote user. */
    private String remoteUser;

    /** The method. */
    private String method = "GET";

    /** The headers. */
    private final Map<String, String> headers = new HashMap<>();

    /** The parameters. */
    private final Map<String, String> parameters = new HashMap<>();

    /** The content. */
    private byte[] content;

    /** The http session. */
    @Mocked
    private HttpSession httpSession;

    /** The principal. */
    private Principal principal;

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

    @Override
    public int getContentLength() {
        return this.content == null ? -1 : this.content.length;
    }

    @Override
    public String getHeader(final String headerName) {
        return this.headers.get(headerName);
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public String getParameter(final String parameterName) {
        return this.parameters.get(parameterName);
    }

    @Override
    public String getQueryString() {
        return this.queryString;
    }

    @Override
    public String getRemoteAddr() {
        return this.remoteAddr;
    }

    @Override
    public String getRemoteHost() {
        return this.remoteHost;
    }

    @Override
    public int getRemotePort() {
        return this.remotePort;
    }

    @Override
    public String getRemoteUser() {
        return this.remoteUser;
    }

    @Override
    public String getRequestURI() {
        return this.requestURI;
    }

    @Override
    public HttpSession getSession() {
        return this.httpSession;
    }

    @Override
    public HttpSession getSession(final boolean create) {
        return this.httpSession;
    }

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
            for (final String eachParameter : this.queryString.split("[&]", -1)) {
                final String[] pair = eachParameter.split("=", -1);
                final String value = pair.length == 2 ? pair[1] : "";
                this.addParameter(pair[0], value);
            }
        }
    }

    @Override
    public void setRemoteAddr(final String value) {
        this.remoteAddr = value;
    }

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

    @Override
    public void setUserPrincipal(final Principal value) {
        this.principal = value;
    }

}
