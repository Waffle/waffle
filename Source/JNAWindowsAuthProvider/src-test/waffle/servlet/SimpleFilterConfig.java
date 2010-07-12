/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
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

	@SuppressWarnings("unchecked")
	public Enumeration getInitParameterNames() {
		Vector<String> keys = new Vector<String>();
		keys.addAll(_parameters.keySet());		
		return (Enumeration) keys.elements();
	}

	public ServletContext getServletContext() {
		return null;
	}
	
	public void setParameter(String parameterName, String parameterValue) {
		_parameters.put(parameterName, parameterValue);
	}
}
