/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Authorization header.
 * @author dblock[at]dblock[dot]org
 */
public class AuthorizationHeader {

	private HttpServletRequest _request;
	
	public AuthorizationHeader(HttpServletRequest request) {
		_request = request;
	}
	
	public String getHeader() {
		return _request.getHeader("Authorization");
	}
	
	public boolean isNull() {
		String header = getHeader();
		return header == null || header.length() == 0;
	}
	
	/**
	 * Returns a supported security package string.
	 * @return
	 *  Negotiate or NTLM.
	 */
	public String getSecurityPackage() {
		String header = getHeader();
		
		if (header == null) {
			throw new RuntimeException("Missing Authorization: header");
		}
		
		int space = header.indexOf(' ');
		if (space > 0) {
			return header.substring(0, space);
		}
		
		throw new RuntimeException("Invalid Authorization header: " + header);
	}
	
	@Override
	public String toString() {
		return isNull() ? "<none>" : getHeader();
	}
	
	public String getToken() {
		return getHeader().substring(getSecurityPackage().length() + 1);
	}
	
	public byte[] getTokenBytes() {
		return Base64.decode(getToken());
	}
	
	public boolean isNtlmType1Message() {
		if (isNull()) 
			return false;

		byte[] tokenBytes = getTokenBytes();
		if (! NtlmMessage.isNtlmMessage(tokenBytes))
			return false;
		
		return (1 == NtlmMessage.getMessageType(tokenBytes));
	}
	
	/**
	 * When using NTLM authentication and the browser is making a POST request, it
	 * preemptively sends a Type 2 authentication message (without the POSTed data). 
	 * The server responds with a 401, and the browser sends a Type 3 request with 
	 * the POSTed data. This is to avoid the situation where user's credentials 
	 * might be potentially invalid, and all this data is being POSTed across the wire.
	 * @return
	 *  True if request is an NTLM POST or PUT with an Authorization header and no data.
	 */
	public boolean isNtlmType1PostAuthorizationHeader() {
		if (! _request.getMethod().equals("POST") && ! _request.getMethod().equals("PUT"))
			return false;
		
		if (_request.getContentLength() != 0)
			return false;
		
		return isNtlmType1Message();
	}
}
