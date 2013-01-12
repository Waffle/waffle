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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

import java.util.Collections;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.PrincipalCollection;
import org.junit.Before;
import org.junit.Test;

import waffle.mock.MockWindowsAuthProvider;

import com.sun.jna.platform.win32.Secur32.EXTENDED_NAME_FORMAT;
import com.sun.jna.platform.win32.Secur32Util;

public class GroupMappingWaffleRealmTests {
	private static final String ROLE_NAME = "ShiroUsers";
	private MockWindowsAuthProvider windowsAuthProvider;
	private GroupMappingWaffleRealm realm;

	@Before
	public void setUp() {
		windowsAuthProvider = new MockWindowsAuthProvider();
		realm = new GroupMappingWaffleRealm();
		realm.setProvider(windowsAuthProvider);
		realm.setGroupRolesMap(Collections.singletonMap("Users", ROLE_NAME));
	}

	@Test
	public void testValidUsernamePassword() {
		AuthenticationToken token = new UsernamePasswordToken(
				getCurrentUserName(), "somePassword");
		AuthenticationInfo authcInfo = realm.getAuthenticationInfo(token);
		PrincipalCollection principals = authcInfo.getPrincipals();
		assertThat(principals.isEmpty(), is(false));
		Object primaryPrincipal = principals.getPrimaryPrincipal();
		assertThat(primaryPrincipal, instanceOf(WaffleFqnPrincipal.class));
		WaffleFqnPrincipal fqnPrincipal = (WaffleFqnPrincipal) primaryPrincipal;
		assertThat(fqnPrincipal.getFqn(), equalTo(getCurrentUserName()));
		assertThat(fqnPrincipal.getGroupFqns(), hasItems("Users", "Everyone"));
		Object credentials = authcInfo.getCredentials();
		assertThat(credentials, instanceOf(char[].class));
		assertThat((char[]) credentials, equalTo("somePassword".toCharArray()));
		assertThat(realm.hasRole(principals, ROLE_NAME), is(true));
	}

	@Test(expected = AuthenticationException.class)
	public void testInvalidUsernamePassword() {
		AuthenticationToken token = new UsernamePasswordToken("InvalidUser",
				"somePassword");
		realm.getAuthenticationInfo(token);
	}

	@Test(expected = AuthenticationException.class)
	public void testGuestUsernamePassword() {
		AuthenticationToken token = new UsernamePasswordToken("Guest",
				"somePassword");
		realm.getAuthenticationInfo(token);
	}

	private String getCurrentUserName() {
		return Secur32Util
				.getUserNameEx(EXTENDED_NAME_FORMAT.NameSamCompatible);
	}
}
