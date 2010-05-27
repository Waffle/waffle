package waffle.tomcat;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import waffle.windows.auth.IWindowsIdentity;

public class MixedAuthenticator extends NegotiateAuthenticator {

    private static Log _log = LogFactory.getLog(MixedAuthenticator.class);

	@Override
	protected boolean authenticate(Request request, Response response, LoginConfig loginConfig) {

		String queryString = request.getQueryString();
		boolean negotiateCheck = (queryString != null && queryString.equals("j_negotiate_check"));
		_log.debug("negotiateCheck: " + negotiateCheck + " (" +  ((queryString == null) ? "<none>" : queryString) + ")");
		boolean securityCheck = (queryString != null && queryString.equals("j_security_check"));
		_log.debug("securityCheck: " + securityCheck + " (" +  ((queryString == null) ? "<none>" : queryString) + ")");

		Principal principal = request.getUserPrincipal();		
		_log.debug("principal: " + ((principal == null) ? "<none>" : principal.getName()));	
		AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
		_log.debug("authorization: " + authorizationHeader.toString());
		
		// When using NTLM authentication and the browser is making a POST request, it 
		// preemptively sends a Type 2 authentication message (without the POSTed 
		// data). The server responds with a 401, and the browser sends a Type 3 
		// request with the POSTed data. This is to avoid the situation where user's 
		// credentials might be potentially invalid, and all this data is being POSTed 
		// across the wire.

		boolean ntlmPost = authorizationHeader.isNtlmType1PostAuthorizationHeader();
		_log.debug("NTLM post: " + ntlmPost);
	
		if (principal != null && ! ntlmPost) {
			_log.debug("previously authenticated user: " + principal.getName());
			return true;
		} else if (negotiateCheck) {
			return super.authenticate(request, response, loginConfig);
		} else if (securityCheck) {
			boolean postResult = post(request, response, loginConfig);
			if (postResult) {
				redirectTo(request, response, request.getServletPath());
			} else {
				redirectTo(request, response, loginConfig.getErrorPage());
			}
			return postResult;
		} else {
			redirectTo(request, response, loginConfig.getLoginPage());
			return false;
		}
	}
	
	private boolean post(Request request, Response response, LoginConfig loginConfig) {
		
		String username = request.getParameter("j_username");
		String password = request.getParameter("j_password");
		
		_log.debug("logging in: " + username);
		
		IWindowsIdentity windowsIdentity = null;
        try {
        	windowsIdentity = _auth.logonUser(username, password);
        } catch (Exception e) {
        	_log.error(e.getMessage());
        	return false;
        }
        
        _log.debug("successfully logged in " + username + " (" + windowsIdentity.getSidString() + ")");       
        
		WindowsPrincipal windowsPrincipal = new WindowsPrincipal(
				windowsIdentity, context.getRealm(), _principalFormat, _roleFormat);
		
		if (_log.isDebugEnabled()) {
			for(String role : windowsPrincipal.getRoles()) {
				_log.debug(" role: " + role);
			}
		}
		
		register(request, response, windowsPrincipal, "FORM", windowsPrincipal.getName(), null);
		_log.info("successfully logged in user: " + windowsPrincipal.getName());
		
		return true;
	}
	
	private void redirectTo(Request request, Response response, String url) {
		try {
			_log.debug("redirecting to: " + url);
			ServletContext servletContext = context.getServletContext();
			RequestDispatcher disp = servletContext.getRequestDispatcher(url);
			disp.forward(request.getRequest(), response);
		} catch (IOException e) {
			_log.error(e.getMessage());
			throw new RuntimeException(e);
		} catch (ServletException e) {
			_log.error(e.getMessage());
			throw new RuntimeException(e);
		}		
	}
}

