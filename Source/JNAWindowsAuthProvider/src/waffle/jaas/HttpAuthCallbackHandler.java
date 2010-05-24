package waffle.jaas;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.servlet.http.HttpServletRequest;

public class HttpAuthCallbackHandler implements CallbackHandler {
	private String _username;

	public HttpAuthCallbackHandler(HttpServletRequest request) {
		_username = request.getRemoteUser();
	}

	public void handle(Callback[] cb) throws IOException,
			UnsupportedCallbackException {
		for (int i = 0; i < cb.length; i++) {
			if (cb[i] instanceof NameCallback) {
				NameCallback nc = (NameCallback) cb[i];
				nc.setName(_username);
			} else {
				throw new UnsupportedCallbackException(cb[i],
						"HttpAuthCallbackHandler");
			}
		}
	}
}
