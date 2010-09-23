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
 * Role principal.
 * @author dblock[at]dblock[dot]org
 */
public class RolePrincipal implements Principal, Serializable {

	private static final long serialVersionUID = 1L;
	private String _fqn;

	/**
	 * A windows principal.
	 * @param fqn
	 *  Fully qualified name.
	 */
	public RolePrincipal(String fqn) {
		_fqn = fqn;
	}

	/**
	 * Role name (Windows Group).
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
