/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.tomcat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.Realm;
import org.apache.catalina.realm.GenericPrincipal;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsIdentity;

/**
 * A Windows Principal.
 * @author dblock[at]dblock[dot]org
 */
public class WindowsPrincipal extends GenericPrincipal {

	private byte[] _sid;
	private String _sidString;
	private Map<String, WindowsAccount> _groups;

	/**
	 * A windows principal.
	 * @param windowsIdentity
	 *  Windows identity.
	 * @param realm
	 *  Authentication realm.
	 */
	public WindowsPrincipal(IWindowsIdentity windowsIdentity, Realm realm) {
		super(realm, windowsIdentity.getFqn(), "", getRoles(windowsIdentity.getGroups()));	
		_sid = windowsIdentity.getSid();
		_sidString = windowsIdentity.getSidString();
		_groups = getGroups(windowsIdentity.getGroups());
	}

	private static List<String> getRoles(IWindowsAccount[] groups) {
		List<String> groupNames = new ArrayList<String>(groups.length);
		for(IWindowsAccount group : groups) {
			groupNames.add(group.getFqn());
		}
		return groupNames;
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
}
