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

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Simple filter chain.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class SimpleFilterChain implements FilterChain {

	private ServletRequest	_request;
	private ServletResponse	_response;

	public ServletRequest getRequest() {
		return _request;
	}

	public ServletResponse getResponse() {
		return _response;
	}

	@Override
	public void doFilter(ServletRequest sreq, ServletResponse srep) throws IOException, ServletException {

		_request = sreq;
		_response = srep;
	}
}
