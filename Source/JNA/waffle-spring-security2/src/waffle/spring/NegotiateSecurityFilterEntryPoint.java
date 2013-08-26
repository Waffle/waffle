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
package waffle.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.AuthenticationEntryPoint;

import waffle.servlet.spi.SecurityFilterProviderCollection;

/**
 * Sends back a request for a Negotiate Authentication to the browser.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilterEntryPoint implements
		AuthenticationEntryPoint {

	private Logger _log = LoggerFactory
			.getLogger(NegotiateSecurityFilterEntryPoint.class);
	private SecurityFilterProviderCollection _provider = null;

	public NegotiateSecurityFilterEntryPoint() {
		_log.debug("[waffle.spring.NegotiateEntryPoint] loaded");
	}

	@Override
	public void commence(ServletRequest sreq, ServletResponse srep,
			AuthenticationException arg2) throws IOException, ServletException {

		HttpServletResponse response = (HttpServletResponse) srep;

		_log.debug("[waffle.spring.NegotiateEntryPoint] commence");

		if (_provider == null) {
			throw new ServletException("Missing NegotiateEntryPoint.Provider");
		}

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setHeader("Connection", "keep-alive");
		_provider.sendUnauthorized(response);
		response.flushBuffer();
	}

	public SecurityFilterProviderCollection getProvider() {
		return _provider;
	}

	public void setProvider(SecurityFilterProviderCollection provider) {
		_provider = provider;
	}
}
