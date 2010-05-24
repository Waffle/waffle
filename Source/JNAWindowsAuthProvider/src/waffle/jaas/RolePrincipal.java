/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.jaas;

import java.security.Principal;

/**
 * Role principal.
 * @author dblock[at]dblock[dot]org
 */
public class RolePrincipal implements Principal {

	private String _fqn;

	/**
	 * A windows principal.
	 * @param windowsIdentity
	 *  Windows identity.
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
	
    public boolean equals(Object o) {    	
        if (o == null)
            return false;

        if (this == o)
            return true;

        if (o instanceof String)
        	return getName().equals((String) o);
        
        if (o instanceof RolePrincipal)
        	return getName().equals(((RolePrincipal) o).getName());

        return false;
    }
}
