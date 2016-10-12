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

import java.security.Principal;
import java.security.acl.Group;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Group principal.
 * 
 * @author rockchip[dot]tv[at]gmail[dot]com
 */
public class GroupPrincipal extends UserPrincipal implements Group {

    /** The Constant serialVersionUID. */
    private static final long               serialVersionUID = 1L;

    /** The fqn. */
    private final String                    fqn;

    /** A list of fqn members for this group. */
    private final Map<Principal, Principal> members;

    public GroupPrincipal(final String fqn) {
        super(fqn);

        this.fqn = fqn;
        this.members = new HashMap<>();
    }

    /*
     * (non-Javadoc)
     * @see waffle.jaas.UserPrincipal#getName()
     */
    @Override
    public String getName() {
        return fqn;
    }

    /*
     * (non-Javadoc)
     * @see java.security.acl.Group#addMember(java.security.Principal)
     */
    @Override
    public boolean addMember(final Principal user) {
        final boolean isMember = members.containsKey(user);
        if (!isMember) {
            members.put(user, user);
        }
        return isMember;
    }

    /*
     * (non-Javadoc)
     * @see java.security.acl.Group#isMember(java.security.Principal)
     */
    @Override
    public boolean isMember(final Principal user) {
        boolean isMember = members.containsKey(user);
        if (!isMember) {
            final Collection<Principal> values = members.values();
            final Iterator<Principal> iter = values.iterator();
            while (!isMember && iter.hasNext()) {
                final Object next = iter.next();
                if (next instanceof Group) {
                    final Group group = (Group) next;
                    isMember = group.isMember(user);
                }
            }
        }
        return isMember;
    }

    /*
     * (non-Javadoc)
     * @see java.security.acl.Group#members()
     */
    @Override
    public Enumeration<? extends Principal> members() {
        return Collections.enumeration(members.values());
    }

    /*
     * (non-Javadoc)
     * @see java.security.acl.Group#removeMember(java.security.Principal)
     */
    @Override
    public boolean removeMember(final Principal user) {
        final Object prev = members.remove(user);
        return prev != null;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuffer tmp = new StringBuffer(getName());
        tmp.append("(members:");
        final Iterator<Principal> iter = members.keySet().iterator();
        while (iter.hasNext()) {
            tmp.append(iter.next());
            tmp.append(',');
        }
        tmp.setCharAt(tmp.length() - 1, ')');
        return tmp.toString();
    }

}
