/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.servlet.spi;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import waffle.util.AuthorizationHeader;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;

/**
 * A collection of security filter providers.
 * @author dblock[at]dblock[dot]org
 */
public class SecurityFilterProviderCollection {

    private Log _log = LogFactory.getLog(SecurityFilterProviderCollection.class);
	private List<SecurityFilterProvider> _providers = new ArrayList<SecurityFilterProvider>();

	public SecurityFilterProviderCollection(SecurityFilterProvider[] providers) {
		for(SecurityFilterProvider provider : providers) {
			_log.info("using '" + provider.getClass().getName() + "'");
			_providers.add(provider);
		}		
	}
	
	@SuppressWarnings("unchecked")
	public SecurityFilterProviderCollection(String[] providerNames, IWindowsAuthProvider auth) {
		for(String providerName : providerNames) {
			providerName = providerName.trim();
			_log.info("loading '" + providerName + "'");
			try {
				Class<SecurityFilterProvider> providerClass = (Class<SecurityFilterProvider>) Class.forName(providerName);
				Constructor c = providerClass.getConstructor(IWindowsAuthProvider.class);
				SecurityFilterProvider provider = (SecurityFilterProvider) c.newInstance(auth); 
				_providers.add(provider);
			} catch (Exception e) {
				_log.error("error loading '" + providerName + "': " + e.getMessage());				
				throw new RuntimeException(e);
			}
		}
	}
	
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
	
	/**
	 * Number of providers.
	 * @return
	 *  Number of providers.
	 */
	public int size() {
		return _providers.size();
	}
	
	/**
	 * Get a security provider by class name.
	 * @param name
	 *  Class name.
	 * @return
	 *  A security provider instance.
	 * @throws ClassNotFoundException
	 */
	public SecurityFilterProvider getByClassName(String name) throws ClassNotFoundException {
		for(SecurityFilterProvider provider : _providers) {
			if (provider.getClass().getName().equals(name)) {
				return provider;
			}
		}		
		throw new ClassNotFoundException(name);
	}
}
