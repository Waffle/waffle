/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.spring;

import org.springframework.security.core.AuthenticationException;

/**
 * Guest login is disabled authentication exception.
 * @author dblock[at]dblock[dot]org
 */
public class GuestLoginDisabledAuthenticationException extends AuthenticationException {

	private static final long serialVersionUID = 1L;
	
	public GuestLoginDisabledAuthenticationException(String msg) {
		super(msg);
	}
}
