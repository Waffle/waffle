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

    /**
     * Gets the name.
     *
     * 'waffle.apache.WindowsRealm/1.0' will no longer be logged. We don't internally use this so we must go with
     * standard java way that tomcat has accepted. This means, going to tomcat 9.0.0.M15+ will result simply in
     * 'WaffleRealm' or better stated the actual simple class name. Simple class name strips off the package name with
     * is what we were applying along with version 1.0 which is inaccurate anyways considering we are on version 1.8.1+
     * at this point.
     *
     * @return a short name for this Realm implementation, for use in log messages.
     * 
     * @deprecated This will be removed in Tomcat 9 onwards. Use {@link Class#getSimpleName()} instead.
     */
    @Deprecated
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
