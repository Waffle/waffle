/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.tomcat.catalina;

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

	@Override
	public void doFilter(ServletRequest request, ServletResponse response)
			throws IOException, ServletException {
		
	}
}
