/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.apache;

import java.security.Principal;

import org.apache.catalina.realm.RealmBase;

/**
 * A rudimentary Windows realm.
 * @author dblock[at]dblock[dot]org
 */
public class WindowsRealm extends RealmBase {

    protected static final String _name = "waffle.apache.WindowsRealm/1.0";

	@Override
	protected String getName() {
		return _name;
	}

	@Override
	protected String getPassword(String arg0) {
		return null;
	}

	@Override
	protected Principal getPrincipal(String arg0) {
		return null;
	}
}
