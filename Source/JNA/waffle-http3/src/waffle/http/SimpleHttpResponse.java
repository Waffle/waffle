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
package waffle.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author dblock[at]dblock[dot]org
 */
public class SimpleHttpResponse implements HttpServletResponse {
	private int _status = 500;
	private Map<String, List<String>> _headers = new HashMap<String, List<String>>();

	@Override
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

	public String[] getHeaderValues(String headerName) {
		List<String> headerValues = _headers.get(headerName);
		return headerValues == null ? null : headerValues
				.toArray(new String[0]);
	}

	@Override
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
	public Collection<String> getHeaderNames() {
		return _headers.keySet();
	}

	@Override
	public void sendError(int rc, String message) {
		_status = rc;
	}

	@Override
	public void sendError(int rc) {
		_status = rc;
	}

	@Override
	public int getBufferSize() {
		return 0;
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
	public Locale getLocale() {
		return null;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return null;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return null;
	}

	@Override
	public boolean isCommitted() {
		return false;
	}

	@Override
	public void reset() {

	}

	@Override
	public void resetBuffer() {

	}

	@Override
	public void setBufferSize(int arg0) {

	}

	@Override
	public void setCharacterEncoding(String arg0) {

	}

	@Override
	public void setContentLength(int arg0) {

	}

	@Override
	public void setContentType(String arg0) {

	}

	@Override
	public void setLocale(Locale arg0) {

	}

	@Override
	public void addCookie(Cookie arg0) {

	}

	@Override
	public void addDateHeader(String arg0, long arg1) {

	}

	@Override
	public void addIntHeader(String arg0, int arg1) {

	}

	@Override
	public boolean containsHeader(String arg0) {
		return false;
	}

	@Override
	public String encodeRedirectURL(String arg0) {
		return null;
	}

	@Override
	public String encodeRedirectUrl(String arg0) {
		return null;
	}

	@Override
	public String encodeURL(String arg0) {
		return null;
	}

	@Override
	public String encodeUrl(String arg0) {
		return null;
	}

	@Override
	public void sendRedirect(String arg0) throws IOException {

	}

	@Override
	public void setDateHeader(String arg0, long arg1) {

	}

	@Override
	public void setIntHeader(String arg0, int arg1) {

	}

	@Override
	public void setStatus(int arg0, String arg1) {

	}

	@Override
	public Collection<String> getHeaders(String arg0) {
		return null;
	}
}
