/*
 * MIT License
 *
 * Copyright (c) 2010-2024 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
