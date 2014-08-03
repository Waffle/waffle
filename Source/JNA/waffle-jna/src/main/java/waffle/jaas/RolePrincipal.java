/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2014 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Application Security, Inc.
 */
package waffle.jaas;

import java.io.Serializable;
import java.security.Principal;

/**
 * Role principal.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class RolePrincipal implements Principal, Serializable {

    private static final long serialVersionUID = 1L;
    private String            fqn;

    /**
     * A windows principal.
     * 
     * @param fqn
     *            Fully qualified name.
     */
    public RolePrincipal(final String fqn) {
        this.fqn = fqn;
    }

    /**
     * Role name (Windows Group).
     */
    @Override
    public String getName() {
        return this.fqn;
    }

    /**
     * Role Principal Equals for FQN.
     * 
     * @param o
     *            Object used for Equality Check.
     */
    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o instanceof RolePrincipal) {
            return getName().equals(((RolePrincipal) o).getName());
        }

        return false;
    }

    /**
     * Role Principal HashCode for FQN.
     */
    @Override
    public int hashCode() {
        return getName().hashCode();
    }

}
