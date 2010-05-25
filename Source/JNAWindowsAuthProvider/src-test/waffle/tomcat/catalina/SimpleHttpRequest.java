/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.tomcat.catalina;

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
	
	private String _remoteUser = null;
	private String _method = "GET";
	private int _remotePort = -1;
	private Map<String, String> _headers = new HashMap<String, String>();
	private byte[] _content = null;
	private HttpSession _session = new SimpleHttpSession();
	
	public SimpleHttpRequest() {
		_remotePort = ++ _remotePort_s;
	}
	
	@Override
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
	
	@Override
	public void setMethod(String methodName) {
		_method = methodName;
	}
	
	@Override
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
}
