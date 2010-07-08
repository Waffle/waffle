/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.servlet.spi;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    private Log _log = LogFactory.getLog(NegotiateSecurityFilterProvider.class);
	private List<String> _protocols = new ArrayList<String>();
	private IWindowsAuthProvider _auth = null;

	public NegotiateSecurityFilterProvider(IWindowsAuthProvider auth) {
		_auth = auth;
		_protocols.add("Negotiate");
		_protocols.add("NTLM");
	}
	
	public List<String> getProtocols() {
		return _protocols;
	}
	
	public void setProtocols(List<String> protocols) {
		_protocols = protocols;
	}
	
	public void sendUnauthorized(HttpServletResponse response) {
		Iterator<String> protocolsIterator = _protocols.iterator();
		while(protocolsIterator.hasNext()) {
			response.addHeader("WWW-Authenticate", protocolsIterator.next());
		}
	}

	public boolean isPrincipalException(HttpServletRequest request) {
		AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
		boolean ntlmPost = authorizationHeader.isNtlmType1PostAuthorizationHeader();
		_log.info("authorization: " + authorizationHeader.toString() + ", ntlm post: " + ntlmPost);
		return ntlmPost;
	}

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

	public boolean isSecurityPackageSupported(String securityPackage) {
		for(String protocol : _protocols) {
			if (protocol.equalsIgnoreCase(securityPackage))
				return true;
		}
		return false;
	}

	public void initParameter(String parameterName, String parameterValue) {
		if (parameterName.equals("protocols")) {
			_protocols = new ArrayList<String>();
			String[] protocolNames = parameterValue.split("\n");
			for(String protocolName : protocolNames) {
				protocolName = protocolName.trim();
				if (protocolName.length() > 0) {
					_log.debug("init protocol: " + protocolName);
					if (protocolName.equals("Negotiate") ||
							protocolName.equals("NTLM")) {
						_protocols.add(protocolName);
					} else {
						_log.error("unsupported protocol: " + protocolName);
						throw new RuntimeException("Unsupported protocol: " + protocolName);
					}
				}
			}
		} else {
			throw new InvalidParameterException(parameterName);			
		}
	}	
}
