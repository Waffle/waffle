/*
 * MIT License
 *
 * Copyright (c) 2010-2020 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package waffle.shiro;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsIdentity;

/**
 * The Class WaffleFqnPrincipal.
 */
public class WaffleFqnPrincipal implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1;

    /** The fqn. */
    private final String fqn;

    /** The group fqns. */
    private final Set<String> groupFqns = new HashSet<>();

    /**
     * Instantiates a new waffle fqn principal.
     *
     * @param identity
     *            the identity
     */
    WaffleFqnPrincipal(final IWindowsIdentity identity) {
        this.fqn = identity.getFqn();
        for (final IWindowsAccount group : identity.getGroups()) {
            this.groupFqns.add(group.getFqn());
        }
    }

    /**
     * Gets the fqn.
     *
     * @return the fully qualified name of the user
     */
    public String getFqn() {
        return this.fqn;
    }

    /**
     * Gets the group fqns.
     *
     * @return the fully qualified names of all groups that the use belongs to
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
        stringBuilder.append(this.getClass().getSimpleName());
        stringBuilder.append(":");
        stringBuilder.append(this.fqn);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
