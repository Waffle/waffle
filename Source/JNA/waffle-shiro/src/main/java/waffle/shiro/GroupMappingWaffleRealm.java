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

    /** The group roles map. */
    private final Map<String, String> groupRolesMap = new HashMap<>();

    /**
     * Sets the translation from group names to role names. If not set, the map is empty, resulting in no users getting
     * roles.
     *
     * @param value
     *            the group roles map to set
     */
    public void setGroupRolesMap(final Map<String, String> value) {
        this.groupRolesMap.clear();
        if (value != null) {
            this.groupRolesMap.putAll(value);
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
    protected Collection<String> getRoleNamesForGroups(final Collection<String> groupNames) {
        final Set<String> roleNames = new HashSet<>();
        for (final String groupName : groupNames) {
            final String roleName = this.groupRolesMap.get(groupName);
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
    protected AuthorizationInfo buildAuthorizationInfo(final WaffleFqnPrincipal principal) {
        final SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addRoles(this.getRoleNamesForGroups(principal.getGroupFqns()));
        return authorizationInfo;
    }
}
