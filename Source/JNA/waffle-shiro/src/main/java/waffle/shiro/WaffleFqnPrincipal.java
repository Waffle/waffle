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
package waffle.shiro;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsIdentity;

public class WaffleFqnPrincipal implements Serializable {
    private static final long serialVersionUID = 1;
    private final String      fqn;
    private final Set<String> groupFqns        = new HashSet<String>();

    WaffleFqnPrincipal(final IWindowsIdentity identity) {
        this.fqn = identity.getFqn();
        for (IWindowsAccount group : identity.getGroups()) {
            this.groupFqns.add(group.getFqn());
        }
    }

    /**
     * Returns the fully qualified name of the user
     */
    public String getFqn() {
        return this.fqn;
    }

    /**
     * Returns the fully qualified names of all groups that the use belongs to
     */
    public Set<String> getGroupFqns() {
        return Collections.unmodifiableSet(this.groupFqns);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof WaffleFqnPrincipal) {
            return this.fqn.equals(((WaffleFqnPrincipal) obj).fqn);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.fqn.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        stringBuilder.append(getClass().getSimpleName());
        stringBuilder.append(":");
        stringBuilder.append(this.fqn);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
