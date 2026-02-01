/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.mock.http;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * The Class SimpleRequestDispatcher.
 */
public class SimpleRequestDispatcher implements RequestDispatcher {

    /** The url. */
    private final String url;

    /**
     * Instantiates a new simple request dispatcher.
     *
     * @param newUrl
     *            the new url
     */
    public SimpleRequestDispatcher(final String newUrl) {
        this.url = newUrl;
    }

    @Override
    public void forward(final ServletRequest request, final ServletResponse response)
            throws ServletException, IOException {
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(304);
        httpResponse.addHeader("Location", this.url);
    }

    @Override
    public void include(final ServletRequest request, final ServletResponse response)
            throws ServletException, IOException {
        // Do Nothing
    }

}
