/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Sends back a request for a Negotiate Authentication to the browser.
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateEntryPoint implements AuthenticationEntryPoint {

    private Log _log = LogFactory.getLog(NegotiateEntryPoint.class);
    private NegotiateAuthenticationProvider _provider = null;

	public NegotiateEntryPoint() {
		_log.debug("[waffle.spring.NegotiateEntryPoint] loaded");
	}
	
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException ex) throws IOException, ServletException {
    	
    	_log.debug("[waffle.spring.NegotiateEntryPoint] commence");
    	
    	if (_provider == null) {
    		throw new ServletException("Missing NegotiateEntryPoint.Provider");
    	}
    	
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    	response.setHeader("Connection", "keep-alive");
        _provider.getProviders().sendUnauthorized(response);
        response.flushBuffer();
    }
    
    public NegotiateAuthenticationProvider getProvider() {
    	return _provider;
    }
    
    public void setProvider(NegotiateAuthenticationProvider provider) {
    	_provider = provider;
    }
}
