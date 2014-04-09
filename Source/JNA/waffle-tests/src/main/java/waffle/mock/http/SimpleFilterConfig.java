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
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

/**
 * @author dblock[at]dblock[dot]org
 */
public class SimpleFilterConfig implements FilterConfig {

	private String _filterName = "Simple Filter";
	private Map<String, String> _parameters = new TreeMap<String, String>();

	@Override
	public String getFilterName() {
		return _filterName;
	}

	public void setFilterName(String filterName) {
		_filterName = filterName;
	}

	@Override
	public String getInitParameter(String s) {
		return _parameters.get(s);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		List<String> keys = new ArrayList<String>();
		keys.addAll(_parameters.keySet());
		return Collections.enumeration(keys);
	}

	@Override
	public ServletContext getServletContext() {
		return null;
	}

	public void setParameter(String parameterName, String parameterValue) {
		_parameters.put(parameterName, parameterValue);
	}
}
