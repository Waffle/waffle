/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.mock.http;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

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
