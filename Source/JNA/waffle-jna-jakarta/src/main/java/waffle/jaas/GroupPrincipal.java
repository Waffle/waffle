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
package waffle.jaas;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Group principal.
 *
 * @author rockchip[dot]tv[at]gmail[dot]com
 */
public class GroupPrincipal extends UserPrincipal {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The fqn. */
    private final String fqn;

    /** A list of fqn members for this group. */
    private final Map<Principal, Principal> members;

    /**
     * Instantiates a new group principal.
     *
     * @param fqn
     *            the fqn
     */
    public GroupPrincipal(final String fqn) {
        super(fqn);

        this.fqn = fqn;
        this.members = new HashMap<>();
    }

    @Override
    public String getName() {
        return this.fqn;
    }

    public boolean addMember(final Principal user) {
        final boolean isMember = this.members.containsKey(user);
        if (!isMember) {
            this.members.put(user, user);
        }
        return isMember;
    }

    public boolean isMember(final Principal user) {
        boolean isMember = this.members.containsKey(user);
        if (!isMember) {
            final Collection<Principal> values = this.members.values();
            for (final Principal principal : values) {
                if (principal instanceof GroupPrincipal) {
                    final GroupPrincipal group = (GroupPrincipal) principal;
                    isMember = group.isMember(user);
                    if (isMember) {
                        break;
                    }
                }
            }
        }
        return isMember;
    }

    public Enumeration<? extends Principal> members() {
        return Collections.enumeration(this.members.values());
    }

    public boolean removeMember(final Principal user) {
        final Object prev = this.members.remove(user);
        return prev != null;
    }

    @Override
    public String toString() {
        final StringBuilder tmp = new StringBuilder(this.getName());
        tmp.append("(members:");
        for (final Principal principal : this.members.keySet()) {
            tmp.append(principal);
            tmp.append(',');
        }
        tmp.setCharAt(tmp.length() - 1, ')');
        return tmp.toString();
    }

}
