/*
 * MIT License
 *
 * Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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
package waffle.servlet;

import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;

import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.PrincipalFormat;

/**
 * The Class AutoDisposableWindowsPrincipal.
 */
public class AutoDisposableWindowsPrincipal extends WindowsPrincipal implements HttpSessionBindingListener {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new auto disposable windows principal.
     *
     * @param windowsIdentity
     *            the windows identity
     */
    public AutoDisposableWindowsPrincipal(final IWindowsIdentity windowsIdentity) {
        super(windowsIdentity);
    }

    /**
     * Instantiates a new auto disposable windows principal.
     *
     * @param windowsIdentity
     *            the windows identity
     * @param principalFormat
     *            the principal format
     * @param roleFormat
     *            the role format
     */
    public AutoDisposableWindowsPrincipal(final IWindowsIdentity windowsIdentity, final PrincipalFormat principalFormat,
            final PrincipalFormat roleFormat) {
        super(windowsIdentity, principalFormat, roleFormat);
    }

    @Override
    public void valueBound(final HttpSessionBindingEvent evt) {
        // Do nothing
    }

    @Override
    public void valueUnbound(final HttpSessionBindingEvent evt) {
        if (this.getIdentity() != null) {
            this.getIdentity().dispose();
        }
    }

}
