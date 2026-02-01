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
class UsernamePasswordCallbackHandler implements CallbackHandler {

    /** The username. */
    private final String username;

    /** The password. */
    private final String password;

    /**
     * Instantiates a new username password callback handler.
     *
     * @param newUsername
     *            the new username
     * @param newPassword
     *            the new password
     */
    UsernamePasswordCallbackHandler(final String newUsername, final String newPassword) {
        this.username = newUsername;
        this.password = newPassword;
    }

    @Override
    public void handle(final Callback[] cb) throws IOException, UnsupportedCallbackException {
        for (final Callback cb1 : cb) {
            if (cb1 instanceof NameCallback) {
                final NameCallback nc = (NameCallback) cb1;
                nc.setName(this.username);
            } else if (cb1 instanceof PasswordCallback) {
                final PasswordCallback pc = (PasswordCallback) cb1;
                pc.setPassword(this.password.toCharArray());
            } else {
                throw new UnsupportedCallbackException(cb1, "UsernamePasswordCallbackHandler");
            }
        }
    }
}
