/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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
