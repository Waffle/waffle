/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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
 * @deprecated This class is deprecated as hiding a principal inside another principal is not JAAS compliant. Use the
 *             Principals in the Subject to directly enroll groups or roles by name.
 */
@Deprecated
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

    /**
     * Add user principal to member.
     *
     * @param user
     *            principal
     *
     * @return True if user principal is a member
     */
    public boolean addMember(final Principal user) {
        final boolean isMember = this.members.containsKey(user);
        if (!isMember) {
            this.members.put(user, user);
        }
        return isMember;
    }

    /**
     * Is user principal a member of the group.
     *
     * @param user
     *            principal
     *
     * @return True if user principal is a member
     */
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

    /**
     * Member enumeration.
     *
     * @return enumerated members
     */
    public Enumeration<Principal> members() {
        return Collections.enumeration(this.members.values());
    }

    /**
     * Remove user from member.
     *
     * @param user
     *            principal
     *
     * @return True if user principal is removed
     */
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
