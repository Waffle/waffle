/*
 * Copyright (c) Application Security Inc., 2010
 * All Rights Reserved
 * Eclipse Public License (EPLv1)
 * http://waffle.codeplex.com/license
 */
package waffle.apache.catalina;

import java.security.Principal;

import org.apache.catalina.realm.RealmBase;

/**
 * @author dblock[at]dblock[dot]org
 */
public class SimpleRealm extends RealmBase {

	@Override
	protected String getName() {
		return "simpleRealm";
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
