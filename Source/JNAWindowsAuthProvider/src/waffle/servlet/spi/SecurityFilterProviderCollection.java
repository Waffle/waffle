/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.servlet.spi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import waffle.util.AuthorizationHeader;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;

/**
 * A collection of security filter providers.
 * @author dblock[at]dblock[dot]org
 */
public class SecurityFilterProviderCollection {

	private List<SecurityFilterProvider> _providers = new ArrayList<SecurityFilterProvider>();
	
	public SecurityFilterProviderCollection(IWindowsAuthProvider auth) {
		_providers.add(new NegotiateSecurityFilterProvider(auth));
		_providers.add(new BasicSecurityFilterProvider(auth));
	}
	
	private SecurityFilterProvider get(String securityPackage) {
		for(SecurityFilterProvider provider : _providers) {
			if (provider.isSecurityPackageSupported(securityPackage)) {
				return provider;
			}
		}		
		throw new RuntimeException("Unsupported security package: " + securityPackage);
	}
	
	/**
	 * Filter.
	 * @param request
	 *  Http Request
	 * @param response
	 *  Http Response
	 * @return
	 *  Windows Identity or NULL.
	 * @throws IOException
	 */
	public IWindowsIdentity doFilter(HttpServletRequest request, 
			HttpServletResponse response) throws IOException {
		
		AuthorizationHeader authorizationHeader = new AuthorizationHeader(request);
		SecurityFilterProvider provider = get(authorizationHeader.getSecurityPackage());
		return provider.doFilter(request, response);
	}
	
	/**
	 * Returns true if authenticaiton still needs to happen despite an existing principal.
	 * @param request
	 *  Http Request
	 * @return
	 *  True if authenticaiton is required.
	 */
	public boolean isPrincipalException(HttpServletRequest request) {
		
		for(SecurityFilterProvider provider : _providers) {
			if (provider.isPrincipalException(request)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Send authorization headers.
	 * @param response
	 *  Http Response
	 */
	public void sendUnauthorized(HttpServletResponse response) {
		for(SecurityFilterProvider provider : _providers) {
			provider.sendUnauthorized(response);
		}
	} 
}
