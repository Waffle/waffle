/*******************************************************************************
 * Waffle (https://github.com/dblock/waffle)
 * 
 * Copyright (c) 2010 Application Security, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 *******************************************************************************/
package waffle.jaas;

import java.io.Serializable;
import java.security.Principal;

/**
 * User Principal.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class UserPrincipal implements Principal, Serializable {

	private static final long	serialVersionUID	= 1L;
	private String				_fqn;

	/**
	 * A user principal.
	 * 
	 * @param fqn
	 *            Fully qualified username.
	 */
	public UserPrincipal(String fqn) {
		_fqn = fqn;
	}

	/**
	 * Fully qualified username.
	 */
	@Override
	public String getName() {
		return _fqn;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}

		if (o instanceof UserPrincipal) {
			return getName().equals(((UserPrincipal) o).getName());
		}

		return false;
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}
}
