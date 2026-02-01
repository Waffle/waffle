/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.mock.http;

import com.sun.jna.platform.win32.Advapi32Util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Filter chain that records current username.
 */
public class RecordUserNameFilterChain extends SimpleFilterChain {

    /** The user name. */
    private String userName;

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
