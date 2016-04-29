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

    /*
     * (non-Javadoc)
     * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
     */
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
