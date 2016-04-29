/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010-2016 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.mock.http;

import com.sun.jna.platform.win32.Advapi32Util;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Filter chain that records current username.
 */
public class RecordUserNameFilterChain extends SimpleFilterChain {

    /** The user name. */
    private String userName;

    /*
     * (non-Javadoc)
     * @see waffle.mock.http.SimpleFilterChain#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse)
            throws IOException, ServletException {
        super.doFilter(servletRequest, servletResponse);
        this.userName = Advapi32Util.getUserName();
    }

    /**
     * Gets the user name.
     *
     * @return the user name
     */
    public String getUserName() {
        return this.userName;
    }
}