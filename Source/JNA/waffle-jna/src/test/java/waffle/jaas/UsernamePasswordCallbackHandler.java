/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2014 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 */
package waffle.jaas;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 * @author dblock[at]dblock[dot]org
 */
public class UsernamePasswordCallbackHandler implements CallbackHandler {

    private final String username;
    private final String password;

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
