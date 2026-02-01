/*
 * SPDX-License-Identifier: MIT
 * See LICENSE file for details.
 *
 * Copyright 2010-2026 The Waffle Project Contributors: https://github.com/Waffle/waffle/graphs/contributors
 */
package waffle.apache;

import java.security.Principal;

import org.apache.catalina.realm.RealmBase;

/**
 * A rudimentary Windows realm.
 */
public class WindowsRealm extends RealmBase {

    @Override
    protected String getPassword(final String value) {
        return null;
    }

    @Override
    protected Principal getPrincipal(final String value) {
        return null;
    }

}
