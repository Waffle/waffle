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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * Simple Http Session
 * @author dblock[at]dblock[dot]org
 */
@SuppressWarnings("deprecation")
public class SimpleHttpSession implements HttpSession {

	private Map<String, Object> _attributes = new HashMap<String, Object>();
	
	public Object getAttribute(String attributeName) {
		return _attributes.get(attributeName);
	}

	@SuppressWarnings("rawtypes")
	public Enumeration getAttributeNames() {
		return null;
	}

	public long getCreationTime() {
		return 0;
	}

	public String getId() {
		return null;
	}

	public long getLastAccessedTime() {
		return 0;
	}

	public int getMaxInactiveInterval() {
		return 0;
	}

	public ServletContext getServletContext() {
		return null;
	}

	public HttpSessionContext getSessionContext() {
		return null;
	}

	public Object getValue(String arg0) {
		return null;
	}

	public String[] getValueNames() {
		return null;
	}

	public void invalidate() {
		
	}

	public boolean isNew() {
		return false;
	}

	public void putValue(String arg0, Object arg1) {
	}

	public void removeAttribute(String attributeName) {
		_attributes.remove(attributeName);
	}

	public void removeValue(String arg0) {
		
	}

	public void setAttribute(String attributeName, Object attributeValue) {
		_attributes.put(attributeName, attributeValue);
	}

	public void setMaxInactiveInterval(int arg0) {
		
	}
}
