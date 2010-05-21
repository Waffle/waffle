/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.tomcat.catalina;

import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.connector.Request;

/**
 * @author dblock[at]dblock[dot]org
 */
public class SimpleHttpRequest extends Request {
	
	private String _method = "GET";
	private int _remotePort = -1;
	private Map<String, String> _headers = new HashMap<String, String>();
	private byte[] _content = null;
	
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
}
