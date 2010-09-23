/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.jaas;

import java.io.Serializable;
import java.security.Principal;

/**
 * User Principal.
 * @author dblock[at]dblock[dot]org
 */
public class UserPrincipal implements Principal, Serializable {

	private static final long serialVersionUID = 1L;
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
	
	@Override
    public boolean equals(Object o) {

		if (this == o) {
			return true;			
		}
		
		if (o instanceof String) {
        	return getName().equals((String) o);
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
