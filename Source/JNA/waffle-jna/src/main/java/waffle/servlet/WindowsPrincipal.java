/*
 * MIT License
 *
 * Copyright (c) 2010-2024 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
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
package waffle.servlet;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.WindowsAccount;

/**
 * A Windows Principal.
 */
public class WindowsPrincipal implements Principal, Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The fqn. */
    private final String fqn;

    /** The sid. */
    private final byte[] sid;

    /** The sid string. */
    private final String sidString;

    /** The roles. */
    private final List<String> roles;

    /** The identity. */
    private transient IWindowsIdentity identity;

    /** The groups. */
    private final Map<String, WindowsAccount> groups;

    /**
     * A windows principal.
     *
     * @param windowsIdentity
     *            Windows identity.
     */
    public WindowsPrincipal(final IWindowsIdentity windowsIdentity) {
        this(windowsIdentity, PrincipalFormat.FQN, PrincipalFormat.FQN);
    }

    /**
     * A windows principal.
     *
     * @param windowsIdentity
     *            Windows identity.
     * @param principalFormat
     *            Principal format.
     * @param roleFormat
     *            Role format.
     */
    public WindowsPrincipal(final IWindowsIdentity windowsIdentity, final PrincipalFormat principalFormat,
            final PrincipalFormat roleFormat) {
        this.identity = windowsIdentity;
        this.fqn = windowsIdentity.getFqn();
        this.sid = windowsIdentity.getSid();
        this.sidString = windowsIdentity.getSidString();
        this.groups = WindowsPrincipal.getGroups(windowsIdentity.getGroups());
        this.roles = WindowsPrincipal.getRoles(windowsIdentity, principalFormat, roleFormat);
    }

    /**
     * Gets the roles.
     *
     * @param windowsIdentity
     *            the windows identity
     * @param principalFormat
     *            the principal format
     * @param roleFormat
     *            the role format
     *
     * @return the roles
     */
    private static List<String> getRoles(final IWindowsIdentity windowsIdentity, final PrincipalFormat principalFormat,
            final PrincipalFormat roleFormat) {
        final List<String> roles = new ArrayList<>();
        roles.addAll(WindowsPrincipal.getPrincipalNames(windowsIdentity, principalFormat));
        for (final IWindowsAccount group : windowsIdentity.getGroups()) {
            roles.addAll(WindowsPrincipal.getRoleNames(group, roleFormat));
        }
        return roles;
    }

    /**
     * Gets the groups.
     *
     * @param groups
     *            the groups
     *
     * @return the groups
     */
    private static Map<String, WindowsAccount> getGroups(final IWindowsAccount[] groups) {
        final Map<String, WindowsAccount> groupMap = new HashMap<>();
        for (final IWindowsAccount group : groups) {
            groupMap.put(group.getFqn(), new WindowsAccount(group));
        }
        return groupMap;
    }

    /**
     * Windows groups that the user is a member of.
     *
     * @return A map of group names to groups.
     */
    public Map<String, WindowsAccount> getGroups() {
        return this.groups;
    }

    /**
     * Byte representation of the SID.
     *
     * @return Array of bytes.
     */
    public byte[] getSid() {
        return this.sid.clone();
    }

    /**
     * String representation of the SID.
     *
     * @return String.
     */
    public String getSidString() {
        return this.sidString;
    }

    /**
     * Returns a list of role principal objects.
     *
     * @param group
     *            Windows group.
     * @param principalFormat
     *            Principal format.
     *
     * @return List of role principal objects.
     */
    private static List<String> getRoleNames(final IWindowsAccount group, final PrincipalFormat principalFormat) {
        final List<String> principals = new ArrayList<>();
        switch (principalFormat) {
            case FQN:
                principals.add(group.getFqn());
                break;
            case SID:
                principals.add(group.getSidString());
                break;
            case BOTH:
                principals.add(group.getFqn());
                principals.add(group.getSidString());
                break;
            case NONE:
            default:
                break;
        }
        return principals;
    }

    /**
     * Returns a list of user principal objects.
     *
     * @param windowsIdentity
     *            Windows identity.
     * @param principalFormat
     *            Principal format.
     *
     * @return A list of user principal objects.
     */
    private static List<String> getPrincipalNames(final IWindowsIdentity windowsIdentity,
            final PrincipalFormat principalFormat) {
        final List<String> principals = new ArrayList<>();
        switch (principalFormat) {
            case FQN:
                principals.add(windowsIdentity.getFqn());
                break;
            case SID:
                principals.add(windowsIdentity.getSidString());
                break;
            case BOTH:
                principals.add(windowsIdentity.getFqn());
                principals.add(windowsIdentity.getSidString());
                break;
            case NONE:
            default:
                break;
        }
        return principals;
    }

    /**
     * Get an array of roles as a string.
     *
     * @return Role1, Role2, ...
     */
    public String getRolesString() {
        return String.join(", ", this.roles);
    }

    /**
     * Checks whether the principal has a given role.
     *
     * @param role
     *            Role name.
     *
     * @return True if the principal has a role, false otherwise.
     */
    public boolean hasRole(final String role) {
        return this.roles.contains(role);
    }

    /**
     * Fully qualified name.
     *
     * @return String.
     */
    @Override
    public String getName() {
        return this.fqn;
    }

    /**
     * Underlying identity.
     *
     * @return String.
     */
    public IWindowsIdentity getIdentity() {
        return this.identity;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof WindowsPrincipal) {
            return this.getName().equals(((WindowsPrincipal) o).getName());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

}
