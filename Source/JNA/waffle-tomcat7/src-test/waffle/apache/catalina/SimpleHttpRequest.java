/*******************************************************************************
* Waffle (http://waffle.codeplex.com)
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
package waffle.apache.catalina;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.connector.Request;

/**
 * @author dblock[at]dblock[dot]org
 */
public class SimpleHttpRequest extends Request implements HttpServletRequest {
	
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
		return ++ _remotePort_s;
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

	@Override
	public void setRemoteHost(String remoteHost) {
		_remoteHost = remoteHost;
	}
	
	@Override
	public String getRemoteAddr() {
		return _remoteAddr;
	}

	@Override
	public void setRemoteAddr(String remoteAddr) {
		_remoteAddr = remoteAddr;
	}
	
	@Override 
	public Principal getUserPrincipal() {
		return _principal;
	}
	
	@Override
	public void setUserPrincipal(Principal principal) {
		_principal = principal;
	}
}
