/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.jaas;

import java.security.Principal;

/**
 * User Principal.
 * @author dblock[at]dblock[dot]org
 */
public class UserPrincipal implements Principal {

	private String _fqn;

	/**
	 * A user principal.
	 * @param fqn
	 *  Fully qualified username.
	 */
	public UserPrincipal(String fqn) {
		_fqn = fqn;
	}

	/**
	 * Fully qualified username.
	 */
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
        
        if (o instanceof UserPrincipal)
        	return getName().equals(((UserPrincipal) o).getName());

        return false;
    }
}
