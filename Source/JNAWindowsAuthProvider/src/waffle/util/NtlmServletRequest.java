/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.util;

import javax.servlet.http.HttpServletRequest;

/**
 * @author dblock[at]dblock[dot]org
 */
public abstract class NtlmServletRequest {
	
	/**
	 * Returns a unique connection id for a given servlet request.
	 * @param request
	 *  Servlet request.
	 * @return
	 *  String.
	 */
	public static String getConnectionId(HttpServletRequest request) {
		String remoteHost = request.getRemoteHost();
		if (remoteHost == null) remoteHost = request.getRemoteAddr();
		if (remoteHost == null) remoteHost = "";
		String remotePort = Integer.toString(request.getRemotePort());
		return remoteHost + ":" + remotePort;
	}
}
