/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.apache.catalina;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Simple filter chain.
 * @author dblock[at]dblock[dot]org
 */
public class SimpleFilterChain implements FilterChain {

	private ServletRequest _request;
	private ServletResponse _response;
	
	public ServletRequest getRequest() {
		return _request;
	}
	
	public ServletResponse getResponse() {
		return _response;
	}
	
	public void doFilter(ServletRequest sreq, ServletResponse srep)
			throws IOException, ServletException {
		
		_request = sreq;
		_response = srep;		
	}
}
