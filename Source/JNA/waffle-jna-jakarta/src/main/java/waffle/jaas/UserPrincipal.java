/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.jaas;

import java.io.Serializable;
import java.security.Principal;

/**
 * User Principal.
 */
public class UserPrincipal implements Principal, Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The fqn. */
    private final String fqn;

    /**
     * A user principal.
     *
     * @param newFqn
     *            Fully qualified username.
     */
    public UserPrincipal(final String newFqn) {
        this.fqn = newFqn;
    }

    /**
     * Fully qualified username.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return this.fqn;
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o instanceof UserPrincipal) {
            return this.getName().equals(((UserPrincipal) o).getName());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
}
