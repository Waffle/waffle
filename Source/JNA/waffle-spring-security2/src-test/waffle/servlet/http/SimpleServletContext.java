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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author dblock[at]dblock[dot]org
 */
@SuppressWarnings("rawtypes")
public class SimpleServletContext implements ServletContext {

	@Override
	public Object getAttribute(String arg0) {		
		return null;
	}

	@Override
	public Enumeration getAttributeNames() {		
		return null;
	}

	@Override
	public ServletContext getContext(String arg0) {		
		return null;
	}

	@Override
	public String getContextPath() {		
		return null;
	}

	@Override
	public String getInitParameter(String arg0) {		
		return null;
	}

	@Override
	public Enumeration getInitParameterNames() {		
		return null;
	}

	@Override
	public int getMajorVersion() {		
		return 0;
	}

	@Override
	public String getMimeType(String arg0) {		
		return null;
	}

	@Override
	public int getMinorVersion() {		
		return 0;
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String name) {
		return null;
	}

	@Override
	public String getRealPath(String arg0) {		
		return null;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String url) {		
		return new SimpleRequestDispatcher(url);
	}

	@Override
	public URL getResource(String arg0) throws MalformedURLException {	
		return null;
	}

	@Override
	public InputStream getResourceAsStream(String arg0) {		
		return null;
	}

	@Override
	public Set getResourcePaths(String arg0) {		
		return null;
	}

	@Override
	public String getServerInfo() {		
		return null;
	}

	@Override
	public Servlet getServlet(String arg0) throws ServletException {		
		return null;
	}

	@Override
	public String getServletContextName() {		
		return null;
	}

	@Override
	public Enumeration getServletNames() {		
		return null;
	}

	@Override
	public Enumeration getServlets() {
		return null;
	}

	@Override
	public void log(String arg0) {		
		
	}

	@Override
	public void log(Exception arg0, String arg1) {
		
	}

	@Override
	public void log(String arg0, Throwable arg1) {
				
	}

	@Override
	public void removeAttribute(String arg0) {
				
	}
	
	@Override
	public void setAttribute(String arg0, Object arg1) {
				
	}
}
