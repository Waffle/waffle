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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import waffle.windows.auth.IWindowsAccount;
import waffle.windows.auth.IWindowsIdentity;

public class WaffleFqnPrincipal implements Serializable {
	private static final long	serialVersionUID	= 1;
	private final String		fqn;
	private final Set<String>	groupFqns			= new HashSet<String>();

	WaffleFqnPrincipal(IWindowsIdentity identity) {
		fqn = identity.getFqn();
		for (IWindowsAccount group : identity.getGroups()) {
			groupFqns.add(group.getFqn());
		}
	}

	/**
	 * Returns the fully qualified name of the user
	 */
	public String getFqn() {
		return fqn;
	}

	/**
	 * Returns the fully qualified names of all groups that the use belongs to
	 */
	public Set<String> getGroupFqns() {
		return Collections.unmodifiableSet(groupFqns);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WaffleFqnPrincipal) {
			return fqn.equals(((WaffleFqnPrincipal) obj).fqn);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return fqn.hashCode();
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{");
		stringBuilder.append(getClass().getSimpleName());
		stringBuilder.append(":");
		stringBuilder.append(fqn);
		stringBuilder.append("}");
		return stringBuilder.toString();
	}
}
