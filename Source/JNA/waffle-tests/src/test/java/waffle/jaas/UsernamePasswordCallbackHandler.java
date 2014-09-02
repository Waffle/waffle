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
    private String username;
    private String password;

    public UsernamePasswordCallbackHandler(String newUsername, String newPassword) {
        this.username = newUsername;
        this.password = newPassword;
    }

    @Override
    public void handle(Callback[] cb) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < cb.length; i++) {
            if (cb[i] instanceof NameCallback) {
                NameCallback nc = (NameCallback) cb[i];
                nc.setName(this.username);
            } else if (cb[i] instanceof PasswordCallback) {
                PasswordCallback pc = (PasswordCallback) cb[i];
                pc.setPassword(this.password.toCharArray());
            } else {
                throw new UnsupportedCallbackException(cb[i], "UsernamePasswordCallbackHandler");
            }
        }
    }
}
