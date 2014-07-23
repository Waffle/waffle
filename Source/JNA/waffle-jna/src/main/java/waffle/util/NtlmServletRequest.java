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
package waffle.util;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Strings;

/**
 * @author dblock[at]dblock[dot]org
 */
public final class NtlmServletRequest {

    /**
     * Returns a unique connection id for a given servlet request.
     * 
     * @param request
     *            Servlet request.
     * @return String.
     */
    public static String getConnectionId(HttpServletRequest request) {
        String remoteHost = request.getRemoteHost();
        if (remoteHost == null) {
            remoteHost = request.getRemoteAddr();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Strings.nullToEmpty(remoteHost));
        stringBuilder.append(":");
        stringBuilder.append(request.getRemotePort());
        return stringBuilder.toString();
    }

    private NtlmServletRequest() {
        // Prevent Instantiation of object
    }
}
