package waffle.jaas;

import java.io.IOException;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author dblock[at]dblock[dot]org
 */
public class SecurityFilter implements Filter {
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest sreq, ServletResponse srep,
			FilterChain filterChain) throws IOException, ServletException {
		
		HttpServletResponse response = (HttpServletResponse) srep;
		HttpServletRequest request = (HttpServletRequest) sreq;
		
		HttpSession session = request.getSession(true);
		Subject subject = (Subject) session.getAttribute("javax.security.auth.subject");
		
		if (subject == null) {
			subject = new Subject();
		}
		
		session.setAttribute("javax.security.auth.subject", subject);
		
		LoginContext lc = null;
		try {
			lc = new LoginContext("Jaas", subject, new HttpAuthCallbackHandler(request));
		} catch (LoginException le) {
			le.printStackTrace();
			response.sendError(HttpServletResponse.SC_FORBIDDEN, request.getRequestURI());
			return;
		} 

		try {
			lc.login();
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, request.getRequestURI());
			return;
		}
		
		filterChain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}
}
