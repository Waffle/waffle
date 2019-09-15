/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2019 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.jaas;

import java.security.Principal;
import java.security.acl.Group;
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
// TODO: Review replacement options for 'Group' as it is officially removed from jdk 14. See
// https://bugs.openjdk.java.net/browse/JDK-8217101?attachmentOrder=desc.
public class GroupPrincipal extends UserPrincipal implements Group {

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

    @Override
    public boolean addMember(final Principal user) {
        final boolean isMember = this.members.containsKey(user);
        if (!isMember) {
            this.members.put(user, user);
        }
        return isMember;
    }

    @Override
    public boolean isMember(final Principal user) {
        boolean isMember = this.members.containsKey(user);
        if (!isMember) {
            final Collection<Principal> values = this.members.values();
            for (final Principal principal : values) {
                if (principal instanceof Group) {
                    final Group group = (Group) principal;
                    isMember = group.isMember(user);
                    if (isMember) {
                        break;
                    }
                }
            }
        }
        return isMember;
    }

    @Override
    public Enumeration<? extends Principal> members() {
        return Collections.enumeration(this.members.values());
    }

    @Override
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
