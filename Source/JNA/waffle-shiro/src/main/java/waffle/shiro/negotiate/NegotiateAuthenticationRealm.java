/*******************************************************************************
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dan Rollo
 *******************************************************************************/

package waffle.shiro.negotiate;

/**
 * Derived from net.skorgenes.security.jsecurity.negotiate.NegotiateAuthenticationFilter.
 * see: https://bitbucket.org/lothor/shiro-negotiate/src/7b25efde130b/src/main/java/net/skorgenes/security/jsecurity/negotiate/NegotiateAuthenticationRealm.java?at=default
 *
 * @author Dan Rollo
 * Date: 1/16/13
 * Time: 12:23 AM
 */
import javax.security.auth.Subject;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.AuthenticatingRealm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import waffle.servlet.WindowsPrincipal;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsSecurityContext;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;

import java.security.Principal;

public class NegotiateAuthenticationRealm extends AuthenticatingRealm {

	/**
	 * This class's private logger.
	 */
	private static final Logger			log	= LoggerFactory.getLogger(NegotiateAuthenticationRealm.class);

	private final IWindowsAuthProvider	windowsAuthProvider;

	public NegotiateAuthenticationRealm() {
		windowsAuthProvider = new WindowsAuthProviderImpl();
	}

	@Override
	public boolean supports(final AuthenticationToken token) {
		return token instanceof NegotiateToken;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken t) throws AuthenticationException {

		final NegotiateToken token = (NegotiateToken) t;
		final byte[] inToken = token.getIn();

		if (token.isNtlmPost()) {
			// type 2 NTLM authentication message received
			windowsAuthProvider.resetSecurityToken(token.getConnectionId());
		}

		final IWindowsSecurityContext securityContext;
		try {
			securityContext = windowsAuthProvider.acceptSecurityToken(token.getConnectionId(), inToken,
					token.getSecurityPackage());
		} catch (Exception e) {
			log.warn("error logging in user: {}", e.getMessage());
			throw new AuthenticationException(e);
		}

		final byte[] continueTokenBytes = securityContext.getToken();
		token.setOut(continueTokenBytes);
		if (continueTokenBytes != null) {
			log.debug("continue token bytes: {}", Integer.valueOf(continueTokenBytes.length));
		} else {
			log.debug("no continue token bytes");
		}

		if (securityContext.isContinue() || token.isNtlmPost()) {
			throw new AuthenticationInProgressException();
		}

		final IWindowsIdentity windowsIdentity = securityContext.getIdentity();
		securityContext.dispose();

		log.debug("logged in user: {} ({})", windowsIdentity.getFqn(), windowsIdentity.getSidString());

		final Principal principal = new WindowsPrincipal(windowsIdentity);
		token.setPrincipal(principal);

		final Subject subject = new Subject();
		subject.getPrincipals().add(principal);
		token.setSubject(subject);

		return token.createInfo();
	}
}