package waffle.tomcat;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Realm;
import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

public class NegotiateAuthenticator extends AuthenticatorBase {

    private static Log _log = LogFactory.getLog(NegotiateAuthenticator.class);
	private static IWindowsAuthProvider _auth = new WindowsAuthProviderImpl();
    protected static final String _info = "waffle.tomcat.NegotiateAuthenticator/1.0";

    @Override
    public String getInfo() {
        return _info;
    }

	public NegotiateAuthenticator() {
		_log.debug("[waffle.tomcat.NegotiateAuthenticator] loaded");
	}
	
	@Override
	public void start() {
		_log.info("[waffle.tomcat.NegotiateAuthenticator] started");		
	}
	
	@Override
	public void stop() {
		_log.info("[waffle.tomcat.NegotiateAuthenticator] stopped");		
	}

	@Override
	protected boolean authenticate(Request request, Response response, LoginConfig loginConfig)
			throws IOException {
		
		Principal principal = request.getUserPrincipal();
		
		_log.debug("principal: " + 
				((principal == null) ? "<none>" : principal.getName()));
		
		String authorization = request.getHeader("Authorization");

		_log.debug("authorization: " + 
				((authorization == null) ? "<none>" : authorization));
		
		// When using NTLM authentication and the browser is making a POST request, it 
		// pre-emptively sends a Type 2 authentication message (without the POSTed 
		// data). The server responds with a 401, and the browser sends a Type 3 
		// request *with* the POSTed data.This is to avoid the situation where a user's 
		// credentials might be potentially invalid, and all this data is being POSTed 
		// across the wire.

		boolean ntlmPost = (request.getMethod() == "POST" 
			&& request.getContentLength() == 0
			&& authorization != null);
		
		_log.debug("request method: " + request.getMethod());
		_log.debug("contentLength: " + request.getContentLength());
		_log.debug("NTLM post: " + ntlmPost);
		
		if (principal != null && ! ntlmPost) {
			// user already authenticated
			_log.debug("previously authenticated user: " + principal.getName());
			return true;
		}
			
		// authenticate user
		if (authorization != null) {
			
			// extract security package from the authorization header
			String securityPackage = getSecurityPackage(authorization);
			_log.debug("security package: " + securityPackage);
			
			// maintain a connection-based session for NTLM tokens
			String connectionId = Integer.toString(request.getRemotePort());
			_log.debug("connection id: " + connectionId);
			
			if (ntlmPost) {
				// type 2 NTLM authentication message received
				_auth.resetSecurityToken(connectionId);
			}
			
			// log the user in using the token
			IWindowsSecurityContext securityContext = null;
			String token = authorization.substring(securityPackage.length() + 1);
			
			try {
				byte[] tokenBuffer = Base64.decode(token);
				_log.debug("token buffer: " + tokenBuffer.length + " bytes");
				securityContext = _auth.acceptSecurityToken(connectionId, tokenBuffer, securityPackage);
				_log.debug("continue required: " + securityContext.getContinue());
    			if (securityContext.getContinue() || ntlmPost) {
    				response.setHeader("Connection", "keep-alive");
    				String continueToken = new String(Base64.encode(securityContext.getToken()));
    				_log.debug("continue token: " + continueToken);
    				response.addHeader("WWW-Authenticate", securityPackage + " " + continueToken);
    				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    				response.flushBuffer();
    				return false;
    			}
			} catch (Exception e) {
				_log.warn("error logging in user: " + e.getMessage());
				responseUnauthorized(response);
				return false;
			}
			
			// realm: fail if no realm is configured
			Realm realm = context.getRealm();
			if(realm == null) {
				_log.warn("missing realm");
				response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
				return false;
			}
			
			IWindowsIdentity windowsIdentity = securityContext.getIdentity();
			_log.debug("logged in user: " + windowsIdentity.getFqn() + 
					" (" + windowsIdentity.getSidString() + ")");
			principal = getGenericPrincipal(windowsIdentity);			
			register(request, response, principal, securityPackage, principal.getName(), null);
			_log.info("successfully logged in user: " + principal.getName());
			return true;
		}
		
		_log.debug("authorization required");
		responseUnauthorized(response);
		return false;
	}
	
	private void responseUnauthorized(Response response) throws IOException {
		response.addHeader("WWW-Authenticate", "Negotiate");
		response.addHeader("WWW-Authenticate", "NTLM");
		response.setHeader("Connection", "close");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.flushBuffer();		
	}
	
	/**
	 * Retrieve a generic principal where the principal name is the user fully qualified name
	 * and the password is the SID.
	 * @param securityContext
	 *  Security context.
	 * @return
	 *  A generic principal.
	 */
	private Principal getGenericPrincipal(IWindowsIdentity windowsIdentity) {
		
		IWindowsAccount[] groups = windowsIdentity.getGroups();
		List<String> groupNames = new ArrayList<String>(groups.length);
		for(IWindowsAccount group : groups) {
			_log.debug(" group: " + group.getFqn());
			groupNames.add(group.getFqn());
		}		
		
		return new GenericPrincipal(context.getRealm(),
				windowsIdentity.getFqn(), 
				windowsIdentity.getSidString(), 
				groupNames);	
	}
	
	private static String getSecurityPackage(String authorization) {
		if (authorization.startsWith("Negotiate ")) {
			return "Negotiate";
		} else if (authorization.startsWith("NTLM ")) {
			return "NTLM";
		} else {
			throw new RuntimeException("Unsupported security package: " + authorization);
		}		
	}
}
