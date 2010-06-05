/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.servlet.spi;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import waffle.util.AuthorizationHeader;
import waffle.util.Base64;
import waffle.util.NtlmServletRequest;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;

/**
 * A negotiate security filter provider.
 * @author dblock[at]dblock[dot]org
 */
public class NegotiateSecurityFilterProvider implements SecurityFilterProvider {

    private static Log _log = LogFactory.getLog(NegotiateSecurityFilterProvider.class);
	private String[] _protocols = { "Negotiate", "NTLM" };
	private static IWindowsAuthProvider _auth = null;

	public NegotiateSecurityFilterProvider(IWindowsAuthProvider auth) {
		_auth = auth;
	}
	
	@Override
	public void sendUnauthorized(HttpServletResponse response) {
		for(String protocol : _protocols) {
			response.addHeader("WWW-Authenticate", protocol);
		}
	}

	@Override
	public boolean isPrincipalException(HttpServletRequest request) {
		AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
		boolean ntlmPost = authorizationHeader.isNtlmType1PostAuthorizationHeader();
		_log.info("authorization: " + authorizationHeader.toString() + ", ntlm post: " + ntlmPost);
		return ntlmPost;
	}

	@Override
	public IWindowsIdentity doFilter(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
		boolean ntlmPost = authorizationHeader.isNtlmType1PostAuthorizationHeader();
		
		// maintain a connection-based session for NTLM tokns
		String connectionId = NtlmServletRequest.getConnectionId(request);
		String securityPackage = authorizationHeader.getSecurityPackage();
		_log.info("security package: " + securityPackage + ", connection id: " + connectionId);
		
		if (ntlmPost) {
			// type 2 NTLM authentication message received
			_auth.resetSecurityToken(connectionId);
		}
		
		byte[] tokenBuffer = authorizationHeader.getTokenBytes();
		_log.info("token buffer: " + tokenBuffer.length + " byte(s)");
		IWindowsSecurityContext securityContext = _auth.acceptSecurityToken(connectionId, tokenBuffer, securityPackage);
			
		byte[] continueTokenBytes = securityContext.getToken();
		if (continueTokenBytes != null) {
			String continueToken = new String(Base64.encode(continueTokenBytes));
			_log.info("continue token: " + continueToken);
			response.addHeader("WWW-Authenticate", securityPackage + " " + continueToken);
		}
			
		_log.info("continue required: " + securityContext.getContinue());
		if (securityContext.getContinue() || ntlmPost) {
			response.setHeader("Connection", "keep-alive");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.flushBuffer();
			return null;
		}
		
		return securityContext.getIdentity();
	}

	@Override
	public boolean isSecurityPackageSupported(String securityPackage) {
		for(String protocol : _protocols) {
			if (protocol.equalsIgnoreCase(securityPackage))
				return true;
		}
		return false;
	}	
}
