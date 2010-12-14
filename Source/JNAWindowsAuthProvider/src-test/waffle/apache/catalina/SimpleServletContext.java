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

	public Object getAttribute(String arg0) {		
		return null;
	}

	public Enumeration getAttributeNames() {		
		return null;
	}

	public ServletContext getContext(String arg0) {		
		return null;
	}

	public String getContextPath() {		
		return null;
	}

	public String getInitParameter(String arg0) {		
		return null;
	}

	public Enumeration getInitParameterNames() {		
		return null;
	}

	public int getMajorVersion() {		
		return 0;
	}

	public String getMimeType(String arg0) {		
		return null;
	}

	public int getMinorVersion() {		
		return 0;
	}

	public RequestDispatcher getNamedDispatcher(String name) {
		return null;
	}

	public String getRealPath(String arg0) {		
		return null;
	}

	public RequestDispatcher getRequestDispatcher(String url) {		
		return new SimpleRequestDispatcher(url);
	}

	public URL getResource(String arg0) throws MalformedURLException {	
		return null;
	}

	public InputStream getResourceAsStream(String arg0) {		
		return null;
	}

	public Set getResourcePaths(String arg0) {		
		return null;
	}

	public String getServerInfo() {		
		return null;
	}

	public Servlet getServlet(String arg0) throws ServletException {		
		return null;
	}

	public String getServletContextName() {		
		return null;
	}

	public Enumeration getServletNames() {		
		return null;
	}

	public Enumeration getServlets() {
		return null;
	}

	public void log(String arg0) {		
		
	}

	public void log(Exception arg0, String arg1) {
		
	}

	public void log(String arg0, Throwable arg1) {
				
	}

	public void removeAttribute(String arg0) {
				
	}
	
	public void setAttribute(String arg0, Object arg1) {
				
	}
}
