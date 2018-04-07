/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2018 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.windows.auth;

import java.io.Serializable;

/**
 * A flattened Windows Account used in a Windows principal.
 *
 * @author dblock[at]dblock[dot]org
 */
public class WindowsAccount implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The sid string. */
    private final String sidString;

    /** The fqn. */
    private final String fqn;

    /** The name. */
    private final String name;

    /** The domain. */
    private final String domain;

    /**
     * Instantiates a new windows account.
     *
     * @param account
     *            the account
     */
    public WindowsAccount(final IWindowsAccount account) {
        this.sidString = account.getSidString();
        this.fqn = account.getFqn();
        this.name = account.getName();
        this.domain = account.getDomain();
    }

    /**
     * Gets the sid string.
     *
     * @return the sid string
     */
    public String getSidString() {
        return this.sidString;
    }

    /**
     * Gets the fqn.
     *
     * @return the fqn
     */
    public String getFqn() {
        return this.fqn;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the domain.
     *
     * @return the domain
     */
    public String getDomain() {
        return this.domain;
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof WindowsAccount)) {
            return false;
        }

        return ((WindowsAccount) o).getSidString().equals(this.getSidString());
    }

    @Override
    public int hashCode() {
        return this.getSidString().hashCode();
    }
}
