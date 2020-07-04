/*
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2020 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
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
 *
 * @author dblock[at]dblock[dot]org
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
