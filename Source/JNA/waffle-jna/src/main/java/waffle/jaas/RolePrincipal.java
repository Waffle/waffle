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
 * Role principal.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class RolePrincipal implements Principal, Serializable {

	private static final long	serialVersionUID	= 1L;
	private String				_fqn;

	/**
	 * A windows principal.
	 * 
	 * @param fqn
	 *            Fully qualified name.
	 */
	public RolePrincipal(String fqn) {
		_fqn = fqn;
	}

	/**
	 * Role name (Windows Group).
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

		if (o instanceof RolePrincipal) {
			return getName().equals(((RolePrincipal) o).getName());
		}

		return false;
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}
}
