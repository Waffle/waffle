/*******************************************************************************
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David M. Carr
 *******************************************************************************/

package waffle.shiro;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;

/**
 * A {@link org.apache.shiro.realm.Realm} that authenticates with Active Directory using WAFFLE and assigns roles to
 * users based on a mapping from their groups. To define permissions based on these roles, set a
 * {@link org.apache.shiro.authz.permission.RolePermissionResolver}.
 */
public class GroupMappingWaffleRealm extends AbstractWaffleRealm {
	private final Map<String, String>	groupRolesMap	= new HashMap<String, String>();

	/**
	 * Sets the translation from group names to role names. If not set, the map is empty, resulting in no users getting
	 * roles.
	 */
	public void setGroupRolesMap(Map<String, String> groupRolesMap) {
		this.groupRolesMap.clear();
		if (groupRolesMap != null) {
			this.groupRolesMap.putAll(groupRolesMap);
		}
	}

	/**
	 * This method is called by to translate group names to role names. This implementation uses the groupRolesMap to
	 * map group names to role names.
	 * 
	 * @param groupNames
	 *            the group names that apply to the current user
	 * @return a collection of roles that are implied by the given role names
	 * @see #setGroupRolesMap
	 */
	protected Collection<String> getRoleNamesForGroups(Collection<String> groupNames) {
		Set<String> roleNames = new HashSet<String>();
		for (String groupName : groupNames) {
			String roleName = groupRolesMap.get(groupName);
			if (roleName != null) {
				roleNames.add(roleName);
			}
		}
		return roleNames;
	}

	/**
	 * Builds an {@link AuthorizationInfo} object based on the user's groups. The groups are translated to roles names
	 * by using the configured groupRolesMap.
	 * 
	 * @param principal
	 *            the principal of Subject that is being authorized
	 * @return the AuthorizationInfo for the given Subject principal
	 * 
	 * @see #setGroupRolesMap
	 * @see #getRoleNamesForGroups
	 */
	@Override
	protected AuthorizationInfo buildAuthorizationInfo(WaffleFqnPrincipal principal) {
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		authorizationInfo.addRoles(getRoleNamesForGroups(principal.getGroupFqns()));
		return authorizationInfo;
	}
}
