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
package waffle.jaas;

import java.io.Serializable;
import java.security.Principal;

/**
 * Role principal.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class RolePrincipal implements Principal, Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The fqn. */
    private final String      fqn;

    /**
     * A windows principal.
     * 
     * @param newFqn
     *            Fully qualified name.
     */
    public RolePrincipal(final String newFqn) {
        this.fqn = newFqn;
    }

    /**
     * Role name (Windows Group).
     *
     * @return the name
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
     * @return true, if successful
     */
    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o instanceof RolePrincipal) {
            return this.getName().equals(((RolePrincipal) o).getName());
        }

        return false;
    }

    /**
     * Role Principal HashCode for FQN.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

}
