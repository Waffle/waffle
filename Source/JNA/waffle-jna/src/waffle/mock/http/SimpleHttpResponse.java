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
package waffle.mock.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.mockito.Mockito;

/**
 * @author dblock[at]dblock[dot]org
 */
public class SimpleHttpResponse extends HttpServletResponseWrapper {
	private int _status = 500;
	private Map<String, List<String>> _headers = new HashMap<String, List<String>>();

	public SimpleHttpResponse() {
    super(Mockito.mock(HttpServletResponse.class));
	}
	
  public int getStatus() {
		return _status;
	}

	@Override
	public void addHeader(String headerName, String headerValue) {
		List<String> current = _headers.get(headerName);
		if (current == null) {
			current = new ArrayList<String>();
		}
		current.add(headerValue);
		_headers.put(headerName, current);
	}

	@Override
	public void setHeader(String headerName, String headerValue) {
		List<String> current = _headers.get(headerName);
		if (current == null) {
			current = new ArrayList<String>();
		} else {
			current.clear();
		}
		current.add(headerValue);
		_headers.put(headerName, current);
	}

	@Override
	public void setStatus(int value) {
		_status = value;
	}

	public String getStatusString() {
		if (_status == 401) {
			return "Unauthorized";
		}
		return "Unknown";
	}

	@Override
	public void flushBuffer() {
		System.out.println(_status + " " + getStatusString());
		for (String header : _headers.keySet()) {
			for (String headerValue : _headers.get(header)) {
				System.out.println(header + ": " + headerValue);
			}
		}
	}
	
	/**
	 * Use this for testing the number of headers
	 */
	public int getHeaderNamesSize() {
	  return _headers.size();
	}

	public String[] getHeaderValues(String headerName) {
		List<String> headerValues = _headers.get(headerName);
		return headerValues == null ? null : headerValues
				.toArray(new String[0]);
	}

	public String getHeader(String headerName) {
		List<String> headerValues = _headers.get(headerName);
		if (headerValues == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (String headerValue : headerValues) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(headerValue);
		}
		return sb.toString();
	}

	@Override
	public void sendError(int rc, String message) {
		_status = rc;
	}

	@Override
	public void sendError(int rc) {
		_status = rc;
	}
}
