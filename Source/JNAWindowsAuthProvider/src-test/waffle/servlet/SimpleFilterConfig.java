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
package waffle.servlet;

import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

/**
 * @author dblock[at]dblock[dot]org
 */
public class SimpleFilterConfig implements FilterConfig {
	
	private String _filterName = "Simple Filter";
	private Map<String, String> _parameters = new TreeMap<String, String>();

	public String getFilterName() {
		return _filterName;
	}
	
	public void setFilterName(String filterName) {
		_filterName = filterName;
	}

	public String getInitParameter(String s) {
		return _parameters.get(s);
	}

	public Enumeration<String> getInitParameterNames() {
		Vector<String> keys = new Vector<String>();
		keys.addAll(_parameters.keySet());		
		return keys.elements();
	}

	public ServletContext getServletContext() {
		return null;
	}
	
	public void setParameter(String parameterName, String parameterValue) {
		_parameters.put(parameterName, parameterValue);
	}
}
