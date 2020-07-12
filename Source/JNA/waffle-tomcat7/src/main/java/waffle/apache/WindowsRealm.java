/*
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2020 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.apache;

import java.security.Principal;

import org.apache.catalina.realm.RealmBase;

/**
 * A rudimentary Windows realm.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsRealm extends RealmBase {

    /** The Constant NAME. */
    protected static final String NAME = "waffle.apache.WindowsRealm/1.0";

    @Override
    protected String getName() {
        return WindowsRealm.NAME;
    }

    @Override
    protected String getPassword(final String value) {
        return null;
    }

    @Override
    protected Principal getPrincipal(final String value) {
        return null;
    }

}
