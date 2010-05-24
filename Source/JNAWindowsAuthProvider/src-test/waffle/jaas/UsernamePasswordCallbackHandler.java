/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
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
	private String _username;
	private String _password;

	public UsernamePasswordCallbackHandler(String username, String password) {
		_username = username;
		_password = password;
	}

	public void handle(Callback[] cb) throws IOException, UnsupportedCallbackException {
		for (int i = 0; i < cb.length; i++) {
			if (cb[i] instanceof NameCallback) {
				NameCallback nc = (NameCallback) cb[i];
				nc.setName(_username);
			} else if (cb[i] instanceof PasswordCallback) {
				PasswordCallback pc = (PasswordCallback) cb[i];
				pc.setPassword(_password.toCharArray());
			} else {
				throw new UnsupportedCallbackException(cb[i], 
						"UsernamePasswordCallbackHandler");
			}
		}
	}
}
