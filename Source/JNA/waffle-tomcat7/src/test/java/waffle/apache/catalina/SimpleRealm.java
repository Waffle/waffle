/**
 * Waffle (https://github.com/dblock/waffle)
 *
 * Copyright (c) 2010 - 2015 Application Security, Inc.
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

import org.apache.catalina.realm.RealmBase;

/**
 * Simple Realm.
 * 
 * @author dblock[at]dblock[dot]org
 */
public abstract class SimpleRealm extends RealmBase {

    /* (non-Javadoc)
     * @see org.apache.catalina.realm.RealmBase#getName()
     */
    @Override
    protected String getName() {
        return "simpleRealm";
    }

}
