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
package waffle.jaas;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 * The Class UsernamePasswordCallbackHandler.
 *
 * @author dblock[at]dblock[dot]org
 */
public class UsernamePasswordCallbackHandler implements CallbackHandler {

    /** The username. */
    private final String username;

    /** The password. */
    private final String password;

    /**
     * Instantiates a new username password callback handler.
     *
     * @param newUserName
     *            the new user name
     * @param newPassword
     *            the new password
     */
    public UsernamePasswordCallbackHandler(final String newUserName, final String newPassword) {
        this.username = newUserName;
        this.password = newPassword;
    }

    @Override
    public void handle(final Callback[] cb) throws IOException, UnsupportedCallbackException {
        for (final Callback element : cb) {
            if (element instanceof NameCallback) {
                final NameCallback nc = (NameCallback) element;
                nc.setName(this.username);
            } else if (element instanceof PasswordCallback) {
                final PasswordCallback pc = (PasswordCallback) element;
                pc.setPassword(this.password == null ? null : this.password.toCharArray());
            } else {
                throw new UnsupportedCallbackException(element, "UsernamePasswordCallbackHandler");
            }
        }
    }

}
