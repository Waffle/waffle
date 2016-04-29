/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010-2016 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.shiro.negotiate;

/**
 * Derived from net.skorgenes.security.jsecurity.negotiate.NegotiateToken. see:
 * https://bitbucket.org/lothor/shiro-negotiate
 * /src/7b25efde130b/src/main/java/net/skorgenes/security/jsecurity/negotiate/NegotiateInfo.java?at=default
 *
 * @author Dan Rollo Date: 1/15/13 Time: 11:00 PM
 */
import javax.security.auth.Subject;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

/**
 * Information about a user authenticated via the HTTP Negotiate authentication mechanism.
 * 
 * @author Dan Rollo
 * @since 1.0.0
 */
public class NegotiateInfo implements AuthenticationInfo {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1537448549089922914L;

    /** The subject. */
    private final Subject     subject;

    /** The realm name. */
    private final String      realmName;

    /**
     * Creates a new info object.
     * 
     * @param newSubject
     *            a subject containing the authenticated users {@link waffle.servlet.WindowsPrincipal}.
     * @param newRealmName
     *            a <code>String</code> containing the name of the authentication realm
     */
    public NegotiateInfo(final Subject newSubject, final String newRealmName) {
        this.subject = newSubject;
        this.realmName = newRealmName;
    }

    /**
     * Creates a new principal collection using the subject as the principal.
     * 
     * @return a new principal collection using the subject as the principal
     */
    @Override
    public PrincipalCollection getPrincipals() {
        return new SimplePrincipalCollection(this.subject.getPrincipals(), this.realmName);
    }

    /**
     * Returns the subject.
     * 
     * @return the subject
     */
    @Override
    public Object getCredentials() {
        return this.subject;
    }
}
