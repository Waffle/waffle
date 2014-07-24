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
