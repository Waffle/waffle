/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
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
 * @author dblock[at]dblock[dot]org
 */
public class WindowsPrincipal implements Principal, Serializable {

	private static final long serialVersionUID = 1L;	
	private String _fqn;
	private byte[] _sid;
	private String _sidString;
	private List<String> _roles;
	private Map<String, WindowsAccount> _groups;

	/**
	 * A windows principal.
	 * @param windowsIdentity
	 *  Windows identity.
	 */
	public WindowsPrincipal(IWindowsIdentity windowsIdentity) {
		this(windowsIdentity, PrincipalFormat.fqn, PrincipalFormat.fqn);
	}
	
	/**
	 * A windows principal.
	 * @param windowsIdentity
	 *  Windows identity.
	 * @param principalFormat
	 *  Principal format.
	 * @param roleFormat
	 *  Role format.
	 */
	public WindowsPrincipal(IWindowsIdentity windowsIdentity, 
			PrincipalFormat principalFormat, PrincipalFormat roleFormat) {
		_fqn = windowsIdentity.getFqn();
		_sid = windowsIdentity.getSid();
		_sidString = windowsIdentity.getSidString();
		_groups = getGroups(windowsIdentity.getGroups());
		_roles = getRoles(windowsIdentity, principalFormat, roleFormat);	
	}

	private static List<String> getRoles(IWindowsIdentity windowsIdentity, 
			PrincipalFormat principalFormat, PrincipalFormat roleFormat) {
		List<String> roles = new ArrayList<String>();
		roles.addAll(getPrincipalNames(windowsIdentity, principalFormat));
		for(IWindowsAccount group : windowsIdentity.getGroups()) {
			roles.addAll(getRoleNames(group, roleFormat));
		}
		return roles;
	}
	
	private static Map<String, WindowsAccount> getGroups(IWindowsAccount[] groups) {
		Map<String, WindowsAccount> groupMap = new HashMap<String, WindowsAccount>();
		for(IWindowsAccount group : groups) {
			groupMap.put(group.getFqn(), new WindowsAccount(group));
		}
		return groupMap;
	}
		
	/**
	 * Byte representation of the SID.
	 * @return
	 *  Array of bytes.
	 */
	public byte[] getSid() {
		return _sid;
	}
	
	/**
	 * String representation of the SID. 
	 * @return
	 *  String.
	 */
	public String getSidString() {
		return _sidString;
	}
	
	/**
	 * Windows groups that the user is a member of.
	 * @return
	 *  A map of group names to groups.
	 */
	public Map<String, WindowsAccount> getGroups() {
		return _groups;
	}

	/**
	 * Returns a list of role principal objects.
	 * @param group
	 *  Windows group.
	 * @param principalFormat
	 *  Principal format.
	 * @return
	 *  List of role principal objects.
	 */
	private static List<String> getRoleNames(
			IWindowsAccount group, PrincipalFormat principalFormat) {
		
		List<String> principals = new ArrayList<String>();
        switch(principalFormat) {
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
        }
        
        return principals;
	}

	/**
	 * Returns a list of user principal objects.
	 * @param windowsIdentity
	 *  Windows identity.
	 * @param principalFormat
	 *  Principal format.
	 * @return
	 *  A list of user principal objects.
	 */
	private static List<String> getPrincipalNames(
			IWindowsIdentity windowsIdentity, PrincipalFormat principalFormat) {
		
		List<String> principals = new ArrayList<String>();
        switch(principalFormat) {
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
        }
        
        return principals;
	}
	
	/**
	 * Get an array of roles as a string.
	 * @return
	 *  Role1, Role2, ...
	 */
	public String getRolesString() {
		StringBuilder sb = new StringBuilder();
		for(String role : _roles) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(role);
		}
		return sb.toString();
	}
	
	/**
	 * Checks whether the principal has a given role.
	 * @param role
	 *  Role name.
	 * @return
	 *  True if the principal has a role, false otherwise.
	 */
	public boolean hasRole(String role) {
		return _roles.contains(role);
	}

	/**
	 * Fully qualified name.
	 */
	public String getName() {
		return _fqn;
	}
}
