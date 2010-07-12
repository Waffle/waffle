/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.apache.catalina;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * @author dblock[at]dblock[dot]org
 */
public class SimpleRequestDispatcher implements RequestDispatcher {

	private String _url;
	
	public SimpleRequestDispatcher(String url) {
		_url = url;
	}
	
	public void forward(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setStatus(304);
		httpResponse.addHeader("Location", _url);
	}

	public void include(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
	}
}
