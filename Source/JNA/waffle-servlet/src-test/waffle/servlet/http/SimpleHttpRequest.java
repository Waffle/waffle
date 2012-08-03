/*******************************************************************************
 * Waffle (https://github.com/dblock/waffle)
 * 
 * Copyright (c) 2010 Application Security, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 *******************************************************************************/
package waffle.servlet.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author dblock[at]dblock[dot]org
 */
public class SimpleHttpRequest implements HttpServletRequest {

	private static int _remotePort_s = 0;

	private String _requestURI = null;
	private String _queryString = null;
	private String _remoteUser = null;
	private String _method = "GET";
	private String _remoteHost = null;
	private String _remoteAddr = null;
	private int _remotePort = -1;
	private Map<String, String> _headers = new HashMap<String, String>();
	private Map<String, String> _parameters = new HashMap<String, String>();
	private byte[] _content = null;
	private HttpSession _session = new SimpleHttpSession();
	private Principal _principal = null;

	public SimpleHttpRequest() {
		_remotePort = nextRemotePort();
	}

	public synchronized static int nextRemotePort() {
		return ++_remotePort_s;
	}

	public synchronized static void resetRemotePort() {
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

	@Override
	public Object getAttribute(String arg0) {
		return null;
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return null;
	}

	@Override
	public String getCharacterEncoding() {
		return null;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return null;
	}

	@Override
	public String getLocalAddr() {
		return null;
	}

	@Override
	public String getLocalName() {
		return null;
	}

	@Override
	public int getLocalPort() {
		return 0;
	}

	@Override
	public Locale getLocale() {
		return null;
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return null;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return null;
	}

	@Override
	public String[] getParameterValues(String arg0) {
		return null;
	}

	@Override
	public String getProtocol() {
		return null;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getRealPath(String arg0) {
		return null;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return null;
	}

	@Override
	public String getScheme() {
		return null;
	}

	@Override
	public String getServerName() {
		return null;
	}

	@Override
	public int getServerPort() {
		return 0;
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public void removeAttribute(String arg0) {

	}

	@Override
	public void setAttribute(String arg0, Object arg1) {

	}

	@Override
	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
	}

	@Override
	public String getAuthType() {
		return null;
	}

	@Override
	public String getContextPath() {
		return null;
	}

	@Override
	public Cookie[] getCookies() {
		return null;
	}

	@Override
	public long getDateHeader(String arg0) {
		return 0;
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		return null;
	}

	@Override
	public Enumeration<String> getHeaders(String arg0) {
		return null;
	}

	@Override
	public int getIntHeader(String arg0) {
		return 0;
	}

	@Override
	public String getPathInfo() {
		return null;
	}

	@Override
	public String getPathTranslated() {
		return null;
	}

	@Override
	public StringBuffer getRequestURL() {
		return null;
	}

	@Override
	public String getRequestedSessionId() {
		return null;
	}

	@Override
	public String getServletPath() {
		return null;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return false;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return false;
	}

	@Override
	public boolean isUserInRole(String arg0) {
		return false;
	}
}
