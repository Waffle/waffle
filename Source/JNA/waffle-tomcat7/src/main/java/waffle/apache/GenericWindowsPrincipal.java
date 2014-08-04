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
package waffle.apache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.realm.GenericPrincipal;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.PrincipalFormat;
import waffle.windows.auth.WindowsAccount;

/**
 * A Windows Principal.
 * 
 * @author dblock[at]dblock[dot]org
 */
public class GenericWindowsPrincipal extends GenericPrincipal {

    private byte[]                      sid;
    private String                      sidString;
    private Map<String, WindowsAccount> groups;

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
    public GenericWindowsPrincipal(IWindowsIdentity windowsIdentity, PrincipalFormat principalFormat,
            PrincipalFormat roleFormat) {
        super(windowsIdentity.getFqn(), "", getRoles(windowsIdentity, principalFormat, roleFormat));
        this.sid = windowsIdentity.getSid();
        this.sidString = windowsIdentity.getSidString();
        this.groups = getGroups(windowsIdentity.getGroups());
    }

    private static List<String> getRoles(IWindowsIdentity windowsIdentity, PrincipalFormat principalFormat,
            PrincipalFormat roleFormat) {
        List<String> roles = new ArrayList<String>();
        roles.addAll(getPrincipalNames(windowsIdentity, principalFormat));
        for (IWindowsAccount group : windowsIdentity.getGroups()) {
            roles.addAll(getRoleNames(group, roleFormat));
        }
        return roles;
    }

    private static Map<String, WindowsAccount> getGroups(IWindowsAccount[] groups) {
        Map<String, WindowsAccount> groupMap = new HashMap<String, WindowsAccount>();
        for (IWindowsAccount group : groups) {
            groupMap.put(group.getFqn(), new WindowsAccount(group));
        }
        return groupMap;
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
     * Windows groups that the user is a member of.
     * 
     * @return A map of group names to groups.
     */
    public Map<String, WindowsAccount> getGroups() {
        return this.groups;
    }

    /**
     * Returns a list of role principal objects.
     * 
     * @param group
     *            Windows group.
     * @param principalFormat
     *            Principal format.
     * @return List of role principal objects.
     */
    private static List<String> getRoleNames(IWindowsAccount group, PrincipalFormat principalFormat) {

        List<String> principals = new ArrayList<String>();
        switch (principalFormat) {
            case fqn:
                principals.add(group.getFqn());
                break;
            case sid:
                principals.add(group.getSidString());
                break;
            case both:
                principals.add(group.getFqn());
                principals.add(group.getSidString());
                break;
            case none:
                break;
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
     * @return A list of user principal objects.
     */
    private static List<String> getPrincipalNames(IWindowsIdentity windowsIdentity, PrincipalFormat principalFormat) {

        List<String> principals = new ArrayList<String>();
        switch (principalFormat) {
            case fqn:
                principals.add(windowsIdentity.getFqn());
                break;
            case sid:
                principals.add(windowsIdentity.getSidString());
                break;
            case both:
                principals.add(windowsIdentity.getFqn());
                principals.add(windowsIdentity.getSidString());
                break;
            case none:
                break;
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
        StringBuilder sb = new StringBuilder();
        for (String role : getRoles()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(role);
        }
        return sb.toString();
    }
}
